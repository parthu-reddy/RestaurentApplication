package com.fooddelivery.restaurant.service.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
@lombok.extern.slf4j.Slf4j

public class AcceptedState implements RestaurantOrderState {
@Override
    public void prepare(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.PREPARING);
        ctx.getActionService().saveOrder(order);
        com.fooddelivery.common.event.OrderPreparingEvent event = com.fooddelivery.common.event.OrderPreparingEvent.builder()
                .orderId(order.getOrderId().toString())
                .restaurantId(order.getRestaurantId().toString())
                .build();
        ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_PREPARING, event);
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
        log.info("Order {} cancelled while in ACCEPTED state", order.getOrderId());
    }
}
