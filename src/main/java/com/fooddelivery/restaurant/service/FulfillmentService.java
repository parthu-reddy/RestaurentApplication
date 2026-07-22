package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.OutletRepository;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurant.service.state.RestaurantActionService;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
import com.fooddelivery.restaurant.service.state.RestaurantOrderStateFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FulfillmentService {

    private final OutletRepository outletRepository;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantActionService actionService;
    private final com.fooddelivery.restaurant.client.DeliveryClient deliveryClient;

    public java.util.List<RestaurantOrder> getOrdersByRestaurant(UUID restaurantId) {
        return restaurantOrderRepository.findByRestaurantId(restaurantId);
    }


    public java.util.List<RestaurantOrder> getActiveOrdersByRestaurant(UUID restaurantId) {
        java.util.List<RestaurantOrder> orders = restaurantOrderRepository.findByRestaurantIdAndStatusIn(restaurantId, java.util.Arrays.asList(
            com.fooddelivery.restaurant.entity.OrderStatus.CREATED, 
            com.fooddelivery.restaurant.entity.OrderStatus.PAID,
            com.fooddelivery.restaurant.entity.OrderStatus.ON_HOLD,
            com.fooddelivery.restaurant.entity.OrderStatus.ACCEPTED, 
            com.fooddelivery.restaurant.entity.OrderStatus.PREPARING,
            com.fooddelivery.restaurant.entity.OrderStatus.READY,
            com.fooddelivery.restaurant.entity.OrderStatus.DISPATCHED));
            
        populateDriverDetails(orders, true);
        return orders;
    }

    private void populateDriverDetails(Iterable<RestaurantOrder> orders, boolean includePhone) {
        for (RestaurantOrder order : orders) {
            if (order.getDeliveryExecutiveId() != null) {
                try {
                    org.springframework.http.ResponseEntity<java.util.Map<String, Object>> response = deliveryClient.getDriverById(order.getDeliveryExecutiveId());
                    if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                        java.util.Map<String, Object> driver = response.getBody();
                        if (driver.containsKey("fullName")) {
                            order.setRiderName(driver.get("fullName").toString());
                        }
                        if (includePhone && driver.containsKey("phoneNumber")) {
                            order.setRiderPhone(driver.get("phoneNumber").toString());
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to fetch driver details for driver ID: " + order.getDeliveryExecutiveId(), e);
                }
            }
        }
    }

    public org.springframework.data.domain.Page<RestaurantOrder> getHistoricalOrdersByRestaurant(UUID restaurantId, String date, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());
        java.util.List<com.fooddelivery.restaurant.entity.OrderStatus> activeStatuses = java.util.Arrays.asList(
            com.fooddelivery.restaurant.entity.OrderStatus.CREATED, 
            com.fooddelivery.restaurant.entity.OrderStatus.PAID,
            com.fooddelivery.restaurant.entity.OrderStatus.ON_HOLD,
            com.fooddelivery.restaurant.entity.OrderStatus.ACCEPTED, 
            com.fooddelivery.restaurant.entity.OrderStatus.PREPARING,
            com.fooddelivery.restaurant.entity.OrderStatus.READY,
            com.fooddelivery.restaurant.entity.OrderStatus.DISPATCHED
        );

        org.springframework.data.domain.Page<RestaurantOrder> resultPage;
        if (date != null && !date.trim().isEmpty()) {
            java.time.LocalDate localDate = java.time.LocalDate.parse(date);
            java.time.LocalDateTime start = localDate.atStartOfDay();
            java.time.LocalDateTime end = localDate.atTime(java.time.LocalTime.MAX);
            resultPage = restaurantOrderRepository.findByRestaurantIdAndStatusNotInAndCreatedAtBetween(restaurantId, activeStatuses, start, end, pageable);
        } else {
            resultPage = restaurantOrderRepository.findByRestaurantIdAndStatusNotIn(restaurantId, activeStatuses, pageable);
        }
        
        populateDriverDetails(resultPage.getContent(), false);
        return resultPage;
    }

    @Transactional
    public void acceptOrder(UUID restaurantId, UUID orderId, Integer additionalPrepTime, String delayReason) {
        log.info("Restaurant {} accepting order {} with additional prep time {} and reason {}", 
                restaurantId, orderId, additionalPrepTime, delayReason);
        
        Outlet restaurant = outletRepository.findById(restaurantId)
            .orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
            
        double lat = restaurant.getLocation() != null ? restaurant.getLocation().getY() : 0.0;
        double lng = restaurant.getLocation() != null ? restaurant.getLocation().getX() : 0.0;
        
        RestaurantOrder order = restaurantOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        RestaurantOrderContext ctx = RestaurantOrderContext.builder()
                .order(order)
                .actionService(actionService)
                .restaurantId(restaurantId)
                .additionalPrepTime(additionalPrepTime)
                .delayReason(delayReason)
                .restaurantLat(lat)
                .restaurantLng(lng)
                .build();
                
        RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
        
        try {
            if (additionalPrepTime != null && additionalPrepTime > 10) {
                state.requestDelay(ctx);
            } else {
                state.accept(ctx);
            }
        } catch (com.fooddelivery.restaurant.exception.IllegalStateTransitionException e) {
            log.error("Illegal state transition for order {}", orderId, e);
            throw e;
        }
    }

    @Transactional
    public void prepareOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} preparing order {}", restaurantId, orderId);
        
        RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            RestaurantOrderContext ctx = RestaurantOrderContext.builder()
                    .order(order)
                    .actionService(actionService)
                    .restaurantId(restaurantId)
                    .build();
            
            RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
            try {
                state.prepare(ctx);
            } catch (com.fooddelivery.restaurant.exception.IllegalStateTransitionException e) {
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
            RestaurantOrderContext ctx = RestaurantOrderContext.builder()
                    .order(order)
                    .actionService(actionService)
                    .restaurantId(restaurantId)
                    .rejectReason(reason)
                    .build();
                    
            RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
            try {
                state.reject(ctx);
            } catch (com.fooddelivery.restaurant.exception.IllegalStateTransitionException e) {
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
            RestaurantOrderContext ctx = RestaurantOrderContext.builder()
                    .order(order)
                    .actionService(actionService)
                    .restaurantId(restaurantId)
                    .build();
                    
            RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
            try {
                state.ready(ctx);
            } catch (com.fooddelivery.restaurant.exception.IllegalStateTransitionException e) {
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
            RestaurantOrderContext ctx = RestaurantOrderContext.builder()
                    .order(order)
                    .actionService(actionService)
                    .restaurantId(restaurantId)
                    .cancelReason(reason)
                    .build();
                    
            RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
            try {
                state.cancel(ctx);
            } catch (com.fooddelivery.restaurant.exception.IllegalStateTransitionException e) {
                log.error("Illegal state transition for order {}", orderId, e);
                throw e;
            }
        }
    }

}
