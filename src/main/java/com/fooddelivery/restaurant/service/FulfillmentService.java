package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.OutletRepository;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurant.service.state.RestaurantActionService;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
import com.fooddelivery.restaurant.service.state.RestaurantOrderStateFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class FulfillmentService {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FulfillmentService.class);
    private static final String DRIVER_FIELD_ID = "id";
    private static final String DRIVER_FIELD_FULL_NAME = "fullName";
    private static final String DRIVER_FIELD_PHONE_NUMBER = "phoneNumber";
    private static final String SORT_FIELD_CREATED_AT = "createdAt";
    private final OutletRepository outletRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantActionService actionService;
    private final com.fooddelivery.restaurant.client.DeliveryClient deliveryClient;
    private final com.fooddelivery.restaurant.client.OrderClient orderClient;

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
        Outlet restaurant = outletRepository.findById(restaurantId).orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
        double lat = 0.0;
        double lng = 0.0;
        String locationWkt = outletRepository.findLocationWktById(restaurantId);
        if (locationWkt != null && locationWkt.startsWith("POINT(")) {
            String coords = locationWkt.substring(6, locationWkt.length() - 1);
            String[] parts = coords.split(" ");
            if (parts.length == 2) {
                lng = Double.parseDouble(parts[0]); // PostGIS X is Longitude
                lat = Double.parseDouble(parts[1]); // PostGIS Y is Latitude
            }
        }
        RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
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
        RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            RestaurantOrderContext ctx = RestaurantOrderContext.builder().order(order).actionService(actionService).restaurantId(restaurantId).build();
            RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
            try {
                state.prepare(ctx);
            } catch (com.fooddelivery.common.exception.IllegalStateTransitionException e) {
                log.error("Illegal state transition for order {}", orderId, e);
                throw e;
            }
        }
    }

    @Transactional
    public void rejectOrder(UUID restaurantId, UUID orderId, String reason) {
        log.info("Restaurant {} rejecting order {}", restaurantId, orderId);
        RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            RestaurantOrderContext ctx = RestaurantOrderContext.builder().order(order).actionService(actionService).restaurantId(restaurantId).rejectReason(reason).build();
            RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
            try {
                state.reject(ctx);
            } catch (com.fooddelivery.common.exception.IllegalStateTransitionException e) {
                log.error("Illegal state transition for order {}", orderId, e);
                throw e;
            }
        }
    }

    @Transactional
    public void readyOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} marked order {} as ready", restaurantId, orderId);
        RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            RestaurantOrderContext ctx = RestaurantOrderContext.builder().order(order).actionService(actionService).restaurantId(restaurantId).build();
            RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
            try {
                state.ready(ctx);
            } catch (com.fooddelivery.common.exception.IllegalStateTransitionException e) {
                log.error("Illegal state transition for order {}", orderId, e);
                throw e;
            }
        }
    }

    @Transactional
    public void cancelOrderAfterAccept(UUID restaurantId, UUID orderId, String reason) {
        log.info("Restaurant {} cancelling order {} after acceptance", restaurantId, orderId);
        RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            RestaurantOrderContext ctx = RestaurantOrderContext.builder().order(order).actionService(actionService).restaurantId(restaurantId).cancelReason(reason).build();
            RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
            try {
                state.cancel(ctx);
            } catch (com.fooddelivery.common.exception.IllegalStateTransitionException e) {
                log.error("Illegal state transition for order {}", orderId, e);
                throw e;
            }
        }
    }

    public void initiatePartialRefund(UUID restaurantId, UUID orderId, java.math.BigDecimal amount) {
        log.info("Restaurant {} initiating partial refund of {} for order {}", restaurantId, amount, orderId);
        RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        if (!order.getRestaurantId().equals(restaurantId)) {
            throw new IllegalArgumentException("Order does not belong to this restaurant");
        }
        
        java.util.Map<String, String> payload = new java.util.HashMap<>();
        payload.put("amount", amount.toString());
        
        org.springframework.http.ResponseEntity<java.util.Map<String, String>> response = orderClient.initiatePartialRefund(orderId, payload);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to initiate partial refund through CustomerApplication: " + response.getStatusCode());
        }
    }

    public java.util.Map<String, Object> getOrderInvoice(UUID restaurantId, UUID orderId) {
        RestaurantOrder order = restaurantOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        if (!order.getRestaurantId().equals(restaurantId)) {
            throw new IllegalArgumentException("Order does not belong to this restaurant");
        }
        
        org.springframework.http.ResponseEntity<java.util.Map<String, Object>> response = orderClient.getOrderInvoice(orderId);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Failed to fetch order invoice from CustomerApplication: " + response.getStatusCode());
        }
        return response.getBody();
    }

    @java.lang.SuppressWarnings("all")
    public FulfillmentService(final OutletRepository outletRepository, final RestaurantOrderRepository restaurantOrderRepository, final RestaurantActionService actionService, final com.fooddelivery.restaurant.client.DeliveryClient deliveryClient, final com.fooddelivery.restaurant.client.OrderClient orderClient) {
        this.outletRepository = outletRepository;
        this.restaurantOrderRepository = restaurantOrderRepository;
        this.actionService = actionService;
        this.deliveryClient = deliveryClient;
        this.orderClient = orderClient;
    }
}
