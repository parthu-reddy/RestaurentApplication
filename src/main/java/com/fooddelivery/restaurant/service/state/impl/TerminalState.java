package com.fooddelivery.restaurant.service.state.impl;

import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TerminalState implements RestaurantOrderState {
    private static final Logger log = LoggerFactory.getLogger(TerminalState.class);

    @Override
    public void handleOrderPaid(com.fooddelivery.restaurant.service.state.RestaurantOrderContext ctx) {
        log.warn("Ignoring ORDER_PAID for Order {}. Already terminal: {}", ctx.getOrder().getOrderId(), ctx.getOrder().getStatus());
    }

    @Override
    public void handleOrderCancelled(com.fooddelivery.restaurant.service.state.RestaurantOrderContext ctx) {
        log.warn("Ignoring ORDER_CANCELLED for Order {}. Already terminal: {}", ctx.getOrder().getOrderId(), ctx.getOrder().getStatus());
    }

    @Override
    public void handleOrderCancelledByCustomer(com.fooddelivery.restaurant.service.state.RestaurantOrderContext ctx) {
        log.warn("Ignoring ORDER_CANCELLED_BY_CUSTOMER for Order {}. Already terminal: {}", ctx.getOrder().getOrderId(), ctx.getOrder().getStatus());
    }

    @Override
    public void handleDelayApproved(com.fooddelivery.restaurant.service.state.RestaurantOrderContext ctx) {
        log.warn("Ignoring ORDER_DELAY_APPROVED for Order {}. Already terminal: {}", ctx.getOrder().getOrderId(), ctx.getOrder().getStatus());
    }

    @Override
    public void handleDelayRejected(com.fooddelivery.restaurant.service.state.RestaurantOrderContext ctx) {
        log.warn("Ignoring ORDER_DELAY_REJECTED for Order {}. Already terminal: {}", ctx.getOrder().getOrderId(), ctx.getOrder().getStatus());
    }

    @Override
    public void handleDispatchFailed(com.fooddelivery.restaurant.service.state.RestaurantOrderContext ctx) {
        log.warn("Ignoring DISPATCH_FAILED for Order {}. Already terminal: {}", ctx.getOrder().getOrderId(), ctx.getOrder().getStatus());
    }
}
