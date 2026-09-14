package com.fooddelivery.restaurant.service.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
@lombok.extern.slf4j.Slf4j

public class PreparingState implements RestaurantOrderState {
@Override
    public void ready(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.READY_FOR_PICKUP);
        ctx.getActionService().saveOrder(order);
        com.fooddelivery.common.event.OrderReadyEvent event = com.fooddelivery.common.event.OrderReadyEvent.builder()
                .orderId(order.getOrderId().toString())
                .restaurantId(order.getRestaurantId().toString())
                .build();
        ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_READY, event);
    }

    @Override
    public void cancel(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
        com.fooddelivery.common.event.OrderCancelledByRestaurantEvent event = com.fooddelivery.common.event.OrderCancelledByRestaurantEvent.builder()
                .orderId(order.getOrderId().toString())
                .reason(ctx.getCancelReason() != null ? ctx.getCancelReason() : "")
                .build();
        ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_CANCELLED_BY_RESTAURANT, event);
    }

    @Override
    public void handleOrderCancelled(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
        log.info("Order {} cancelled while in PREPARING state", order.getOrderId());
    }
}
