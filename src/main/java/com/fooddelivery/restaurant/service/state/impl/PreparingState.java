package com.fooddelivery.restaurant.service.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PreparingState implements RestaurantOrderState {
    
    @Override
    public void ready(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.READY_FOR_PICKUP);
        ctx.getActionService().saveOrder(order);
        
        ObjectNode payloadNode = ctx.getActionService().createPayloadNode();
        payloadNode.put("eventType", EventType.ORDER_READY.name());
        payloadNode.put("orderId", order.getOrderId().toString());
        payloadNode.put("restaurantId", order.getRestaurantId().toString());
        
        ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_READY, payloadNode);
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
        log.info("Order {} cancelled while in PREPARING state", order.getOrderId());
    }
}
