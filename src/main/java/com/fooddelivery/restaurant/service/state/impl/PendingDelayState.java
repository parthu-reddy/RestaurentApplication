package com.fooddelivery.restaurant.service.state.impl;

import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PendingDelayState implements RestaurantOrderState {
    
    private final CreatedState createdState;

    public PendingDelayState(CreatedState createdState) {
        this.createdState = createdState;
    }

    @Override
    public void accept(RestaurantOrderContext ctx) {
        log.info("Order {} accepted by restaurant while in ON_HOLD state (overriding delay)", ctx.getOrder().getOrderId());
        createdState.accept(ctx);
    }

    @Override
    public void requestDelay(RestaurantOrderContext ctx) {
        log.info("Order {} delay requested again by restaurant while in ON_HOLD state", ctx.getOrder().getOrderId());
        createdState.requestDelay(ctx);
    }
    
    @Override
    public void handleDelayApproved(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        log.info("Order {} delay approved. Proceeding to accept.", order.getOrderId());
        
        Integer originalAdditional = ctx.getAdditionalPrepTime();
        ctx.setAdditionalPrepTime(null);
        
        createdState.accept(ctx);
        
        ctx.setAdditionalPrepTime(originalAdditional);
    }

    @Override
    public void handleDelayRejected(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        log.info("Order {} delay rejected. Rejecting order.", order.getOrderId());
        
        createdState.reject(ctx);
    }

    @Override
    public void handleOrderCancelled(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
        log.info("Order {} cancelled while in ON_HOLD state", order.getOrderId());
    }

    @Override
    public void reject(RestaurantOrderContext ctx) {
        log.info("Order {} rejected by restaurant while in ON_HOLD state", ctx.getOrder().getOrderId());
        createdState.reject(ctx);
    }
}
