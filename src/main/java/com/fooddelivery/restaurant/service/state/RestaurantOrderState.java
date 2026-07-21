package com.fooddelivery.restaurant.service.state;

import com.fooddelivery.restaurant.exception.IllegalStateTransitionException;
import com.fooddelivery.common.enums.OrderStatus;

public interface RestaurantOrderState {
    
    // API Actions
    default void requestDelay(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot request delay in state: " + ctx.getOrder().getStatus());
    }

    default void accept(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot accept order in state: " + ctx.getOrder().getStatus());
    }
    
    default void prepare(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot prepare order in state: " + ctx.getOrder().getStatus());
    }

    default void reject(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot reject order in state: " + ctx.getOrder().getStatus());
    }

    default void ready(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot ready order in state: " + ctx.getOrder().getStatus());
    }

    default void cancel(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot cancel order in state: " + ctx.getOrder().getStatus());
    }

    default void dispatch(RestaurantOrderContext ctx, String otp) {
        throw new IllegalStateTransitionException("Cannot dispatch order in state: " + ctx.getOrder().getStatus());
    }

    // Kafka Event Handlers
    default void handleOrderPaid(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot process ORDER_PAID in state: " + ctx.getOrder().getStatus());
    }

    default void handleOrderCancelled(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot process ORDER_CANCELLED in state: " + ctx.getOrder().getStatus());
    }

    default void handleOrderCancelledByCustomer(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot process ORDER_CANCELLED_BY_CUSTOMER in state: " + ctx.getOrder().getStatus());
    }

    default void handleDelayApproved(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot process ORDER_DELAY_APPROVED in state: " + ctx.getOrder().getStatus());
    }

    default void handleDelayRejected(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot process ORDER_DELAY_REJECTED in state: " + ctx.getOrder().getStatus());
    }

    default void handleDriverAssigned(RestaurantOrderContext ctx) {
        // Generally safe to ignore if already terminal
    }

    default void handleOrderStatusUpdated(RestaurantOrderContext ctx) {
        // Driver picked up, etc.
    }

    default void handleOrderStatusSync(RestaurantOrderContext ctx) {
        String targetStatusStr = ctx.getEventPayload().path("status").asText(null);
        if (targetStatusStr != null) {
            try {
                if (OrderStatus.PAID.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.PAID.name();
                } else if (OrderStatus.CANCELLED.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED.name();
                } else if (OrderStatus.AWAITING_DELAY_APPROVAL.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.ON_HOLD.name();
                } else if (OrderStatus.READY_FOR_PICKUP.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.READY.name();
                } else if (OrderStatus.OUT_FOR_DELIVERY.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.DISPATCHED.name();
                } else if (OrderStatus.CANCELLED_BY_RESTAURANT.name().equals(targetStatusStr) || 
                           OrderStatus.CANCELLED.name().equals(targetStatusStr) || 
                           OrderStatus.CANCELLED_AND_REFUNDED.name().equals(targetStatusStr) || 
                           OrderStatus.PARTIALLY_REFUNDED.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED.name();
                }
                
                com.fooddelivery.restaurant.entity.OrderStatus targetStatus = com.fooddelivery.restaurant.entity.OrderStatus.valueOf(targetStatusStr);
                com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
                
                if (targetStatus.getSequence() < order.getStatus().getSequence()) {
                    // Gracefully ignore outdated backward state syncs
                    return;
                }
                
                order.setStatus(targetStatus);
                ctx.getActionService().saveOrder(order);
            } catch (IllegalArgumentException e) {
                // Invalid status string, ignore
            }
        }
    }

    default void handleDispatchFailed(RestaurantOrderContext ctx) {
        com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
        order.setStatus(com.fooddelivery.restaurant.entity.OrderStatus.DELIVERY_FAILED);
        ctx.getActionService().saveOrder(order);
    }

    default void handleDeliveryFailed(RestaurantOrderContext ctx) {
        // Terminal update
    }

    default void handleOrderDelivered(RestaurantOrderContext ctx) {
        // Terminal update
    }
}
