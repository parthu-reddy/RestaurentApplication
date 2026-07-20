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

    public java.util.List<RestaurantOrder> getOrdersByRestaurant(UUID restaurantId) {
        return restaurantOrderRepository.findByRestaurantId(restaurantId);
    }

    public java.util.List<RestaurantOrder> getActiveOrdersByRestaurant(UUID restaurantId) {
        return restaurantOrderRepository.findByRestaurantIdAndStatusIn(restaurantId, java.util.Arrays.asList(
            com.fooddelivery.restaurant.entity.OrderStatus.CREATED, 
            com.fooddelivery.restaurant.entity.OrderStatus.ON_HOLD,
            com.fooddelivery.restaurant.entity.OrderStatus.ACCEPTED, 
            com.fooddelivery.restaurant.entity.OrderStatus.READY));
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
        }
    }

    @Transactional
    public void rejectOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} rejecting order {}", restaurantId, orderId);
        
        RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            RestaurantOrderContext ctx = RestaurantOrderContext.builder()
                    .order(order)
                    .actionService(actionService)
                    .restaurantId(restaurantId)
                    .build();
                    
            RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
            try {
                state.reject(ctx);
            } catch (com.fooddelivery.restaurant.exception.IllegalStateTransitionException e) {
                log.error("Illegal state transition for order {}", orderId, e);
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
            }
        }
    }

    @Transactional
    public void cancelOrderAfterAccept(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} cancelling order {} after acceptance", restaurantId, orderId);
        
        RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            RestaurantOrderContext ctx = RestaurantOrderContext.builder()
                    .order(order)
                    .actionService(actionService)
                    .restaurantId(restaurantId)
                    .build();
                    
            RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
            try {
                state.cancel(ctx);
            } catch (com.fooddelivery.restaurant.exception.IllegalStateTransitionException e) {
                log.error("Illegal state transition for order {}", orderId, e);
            }
        }
    }

    @Transactional
    public void dispatchOrder(UUID restaurantId, UUID orderId, String otp) {
        RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            RestaurantOrderContext ctx = RestaurantOrderContext.builder()
                    .order(order)
                    .actionService(actionService)
                    .restaurantId(restaurantId)
                    .build();
                    
            RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
            try {
                state.dispatch(ctx, otp);
            } catch (com.fooddelivery.restaurant.exception.IllegalStateTransitionException e) {
                log.error("Illegal state transition for order {}", orderId, e);
            }
        }
    }
}
