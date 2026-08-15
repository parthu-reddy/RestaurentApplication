package com.fooddelivery.restaurant.service.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
@lombok.extern.slf4j.Slf4j

public class AcceptedState implements RestaurantOrderState {
    @java.lang.SuppressWarnings("all")

    @Override
    public void prepare(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.PREPARING);
        ctx.getActionService().saveOrder(order);
        ObjectNode payloadNode = ctx.getActionService().createPayloadNode();
        payloadNode.put("eventType", EventType.ORDER_PREPARING.name());
        payloadNode.put("orderId", order.getOrderId().toString());
        payloadNode.put("restaurantId", order.getRestaurantId().toString());
        ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_PREPARING, payloadNode);
    }

    @Override
    public void cancel(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
        ObjectNode payloadNode = ctx.getActionService().createPayloadNode();
        payloadNode.put("eventType", EventType.ORDER_CANCELLED_BY_RESTAURANT.name());
        payloadNode.put("orderId", order.getOrderId().toString());
        payloadNode.put("restaurantId", order.getRestaurantId().toString());
        payloadNode.put("reason", ctx.getCancelReason() != null ? ctx.getCancelReason() : "");
        ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_CANCELLED_BY_RESTAURANT, payloadNode);
    }

    @Override
    public void handleOrderCancelled(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
        log.info("Order {} cancelled while in ACCEPTED state", order.getOrderId());
    }
}
