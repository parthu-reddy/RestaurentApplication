package com.fooddelivery.restaurant.service.state;

import com.fooddelivery.restaurant.exception.IllegalStateTransitionException;

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

    default void handleDispatchFailed(RestaurantOrderContext ctx) {
        // Could cancel the order
    }

    default void handleDeliveryFailed(RestaurantOrderContext ctx) {
        // Terminal update
    }

    default void handleOrderDelivered(RestaurantOrderContext ctx) {
        // Terminal update
    }
}
