package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.OutletRepository;
import com.fooddelivery.common.exception.ResourceNotFoundException;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurant.service.state.RestaurantActionService;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
import com.fooddelivery.restaurant.service.state.RestaurantOrderStateFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class FulfillmentService {
private static final String DRIVER_FIELD_ID = "id";
    private static final String DRIVER_FIELD_FULL_NAME = "fullName";
    private static final String DRIVER_FIELD_PHONE_NUMBER = "phoneNumber";
    private static final String SORT_FIELD_CREATED_AT = "createdAt";
    private final OutletRepository outletRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantActionService actionService;
    private final com.fooddelivery.restaurant.client.DeliveryClient deliveryClient;
    private final com.fooddelivery.restaurant.client.OrderClient orderClient;

    /**
     * A missing order and another restaurant's order are reported identically and as 404.
     *
     * <p>Distinguishing them would turn these endpoints into an oracle for which order ids exist.
     * It also replaces four silent no-ops: the mutators used to return 200 "Order rejected by
     * restaurant" when the order did not exist at all.
     */
    private ResourceNotFoundException notFound(UUID orderId, UUID restaurantId) {
        log.warn("Order {} not found for restaurant {}", orderId, restaurantId);
        return new ResourceNotFoundException("Order not found: " + orderId);
    }

    public java.util.List<RestaurantOrder> getOrdersByRestaurant(UUID restaurantId) {
        return restaurantOrderRepository.findByRestaurantId(restaurantId);
    }

    private static final java.util.List<com.fooddelivery.restaurant.entity.OrderStatus> CANCELLED_STATUSES = java.util.List.of(com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED, com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED_BY_RESTAURANT);

    public java.util.List<RestaurantOrder> getActiveOrdersByRestaurant(UUID restaurantId) {
        java.util.List<RestaurantOrder> orders = restaurantOrderRepository.findActiveOrdersByRestaurantId(restaurantId, CANCELLED_STATUSES, java.util.List.of(com.fooddelivery.common.enums.DeliveryStatus.DELIVERED, com.fooddelivery.common.enums.DeliveryStatus.FAILED, com.fooddelivery.common.enums.DeliveryStatus.CANCELLED));
        populateDriverDetails(orders, true);
        return orders;
    }

    private void populateDriverDetails(Iterable<RestaurantOrder> orders, boolean includePhone) {
        java.util.Set<UUID> driverIds = new java.util.HashSet<>();
        for (RestaurantOrder order : orders) {
            if (order.getDeliveryExecutiveId() != null) {
                driverIds.add(order.getDeliveryExecutiveId());
            }
        }
        if (driverIds.isEmpty()) return;
        try {
            org.springframework.http.ResponseEntity<java.util.List<java.util.Map<String, Object>>> response = deliveryClient.getDriversByIds(new java.util.ArrayList<>(driverIds));
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                java.util.Map<String, java.util.Map<String, Object>> driverMap = new java.util.HashMap<>();
                for (java.util.Map<String, Object> driver : response.getBody()) {
                    if (driver.containsKey(DRIVER_FIELD_ID)) {
                        driverMap.put(driver.get(DRIVER_FIELD_ID).toString(), driver);
                    }
                }
                for (RestaurantOrder order : orders) {
                    if (order.getDeliveryExecutiveId() != null) {
                        java.util.Map<String, Object> driver = driverMap.get(order.getDeliveryExecutiveId().toString());
                        if (driver != null) {
                            if (driver.containsKey(DRIVER_FIELD_FULL_NAME)) {
                                order.setRiderName(driver.get(DRIVER_FIELD_FULL_NAME).toString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch batched driver details", e);
        }
    }

    public org.springframework.data.domain.Page<RestaurantOrder> getHistoricalOrdersByRestaurant(UUID restaurantId, String date, int page, int size) {
        int safeSize = Math.min(size, 100);
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, safeSize, org.springframework.data.domain.Sort.by(SORT_FIELD_CREATED_AT).descending());
        java.time.LocalDateTime start = null;
        java.time.LocalDateTime end = null;
        if (date != null && !date.trim().isEmpty()) {
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            start = localDate.atStartOfDay();
            end = localDate.atTime(java.time.LocalTime.MAX);
        }
        org.springframework.data.domain.Page<RestaurantOrder> resultPage = restaurantOrderRepository.findHistoryOrdersByRestaurantId(restaurantId, CANCELLED_STATUSES, java.util.List.of(com.fooddelivery.common.enums.DeliveryStatus.DELIVERED, com.fooddelivery.common.enums.DeliveryStatus.FAILED, com.fooddelivery.common.enums.DeliveryStatus.CANCELLED), start, end, pageable);
        populateDriverDetails(resultPage.getContent(), false);
        return resultPage;
    }

    @Transactional
    public void acceptOrder(UUID restaurantId, UUID orderId, Integer additionalPrepTime, String delayReason) {
        log.info("Restaurant {} accepting order {} with additional prep time {} and reason {}", restaurantId, orderId, additionalPrepTime, delayReason);
        // Authorise first. Looking up the outlet and its coordinates before deciding whether the
        // caller may touch this order at all is work done on behalf of a request that is refused.
        RestaurantOrder order = restaurantOrderRepository
                .findByOrderIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() -> notFound(orderId, restaurantId));
        outletRepository.findById(restaurantId)
                .orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
        double[] coords = actionService.getRestaurantCoordinates(restaurantId);
        double lat = coords[0];
        double lng = coords[1];
        RestaurantOrderContext ctx = RestaurantOrderContext.builder().order(order).actionService(actionService).restaurantId(restaurantId).additionalPrepTime(additionalPrepTime).delayReason(delayReason).restaurantLat(lat).restaurantLng(lng).build();
        RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
        try {
            if (additionalPrepTime != null && additionalPrepTime > 10) {
                state.requestDelay(ctx);
            } else {
                state.accept(ctx);
            }
        } catch (com.fooddelivery.common.exception.IllegalStateTransitionException e) {
            log.error("Illegal state transition for order {}", orderId, e);
            throw e;
        }
    }

    @Transactional
    public void prepareOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} preparing order {}", restaurantId, orderId);
        RestaurantOrder order = restaurantOrderRepository
            .findByOrderIdAndRestaurantId(orderId, restaurantId)
            .orElseThrow(() -> notFound(orderId, restaurantId));
        RestaurantOrderContext ctx = RestaurantOrderContext.builder().order(order).actionService(actionService).restaurantId(restaurantId).build();
        RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
        try {
            state.prepare(ctx);
        } catch (com.fooddelivery.common.exception.IllegalStateTransitionException e) {
            log.error("Illegal state transition for order {}", orderId, e);
            throw e;
        }
    }

    @Transactional
    public void rejectOrder(UUID restaurantId, UUID orderId, String reason) {
        log.info("Restaurant {} rejecting order {}", restaurantId, orderId);
        RestaurantOrder order = restaurantOrderRepository
            .findByOrderIdAndRestaurantId(orderId, restaurantId)
            .orElseThrow(() -> notFound(orderId, restaurantId));
        RestaurantOrderContext ctx = RestaurantOrderContext.builder().order(order).actionService(actionService).restaurantId(restaurantId).rejectReason(reason).build();
        RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
        try {
            state.reject(ctx);
        } catch (com.fooddelivery.common.exception.IllegalStateTransitionException e) {
            log.error("Illegal state transition for order {}", orderId, e);
            throw e;
        }
    }

    @Transactional
    public void readyOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} marked order {} as ready", restaurantId, orderId);
        RestaurantOrder order = restaurantOrderRepository
            .findByOrderIdAndRestaurantId(orderId, restaurantId)
            .orElseThrow(() -> notFound(orderId, restaurantId));
        RestaurantOrderContext ctx = RestaurantOrderContext.builder().order(order).actionService(actionService).restaurantId(restaurantId).build();
        RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
        try {
            state.ready(ctx);
        } catch (com.fooddelivery.common.exception.IllegalStateTransitionException e) {
            log.error("Illegal state transition for order {}", orderId, e);
            throw e;
        }
    }

    @Transactional
    public void cancelOrderAfterAccept(UUID restaurantId, UUID orderId, String reason) {
        log.info("Restaurant {} cancelling order {} after acceptance", restaurantId, orderId);
        RestaurantOrder order = restaurantOrderRepository
            .findByOrderIdAndRestaurantId(orderId, restaurantId)
            .orElseThrow(() -> notFound(orderId, restaurantId));
        RestaurantOrderContext ctx = RestaurantOrderContext.builder().order(order).actionService(actionService).restaurantId(restaurantId).cancelReason(reason).build();
        RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
        try {
            state.cancel(ctx);
        } catch (com.fooddelivery.common.exception.IllegalStateTransitionException e) {
            log.error("Illegal state transition for order {}", orderId, e);
            throw e;
        }
    }

    public void initiatePartialRefund(UUID restaurantId, UUID orderId, java.math.BigDecimal amount, java.util.List<String> items, String reason) {
        log.info("Restaurant {} initiating partial refund of {} for order {}", restaurantId, amount, orderId);
        RestaurantOrder order = restaurantOrderRepository
                .findByOrderIdAndRestaurantId(orderId, restaurantId)
                .orElseThrow(() -> notFound(orderId, restaurantId));
        
        java.util.Map<String, Object> payload = new java.util.HashMap<>();
        payload.put("amount", amount);
        payload.put("reason", reason);
        if (items != null && !items.isEmpty()) {
            payload.put("items", items);
        }
        payload.put("initiatorType", "RESTAURANT");
        payload.put("initiatorId", restaurantId.toString());
        
        org.springframework.http.ResponseEntity<java.util.Map<String, Object>> response = orderClient.initiatePartialRefund(orderId, payload);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to initiate partial refund through CustomerApplication: " + response.getStatusCode());
        }
    }



}
