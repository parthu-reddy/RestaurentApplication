package com.fooddelivery.restaurant.service.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
@lombok.extern.slf4j.Slf4j

public class ReadyState implements RestaurantOrderState {
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
    public void handleDriverAtRestaurant(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setDeliveryStatus(com.fooddelivery.common.enums.DeliveryStatus.AT_RESTAURANT);
        ctx.getActionService().saveOrder(order);
        log.info("Driver is at restaurant for order {}", order.getOrderId());
    }

    @Override
    public void handleOrderStatusUpdated(RestaurantOrderContext ctx) {
        String newStatusStr = ctx.getEventPayload().path("status").asText("");
        if (OrderStatus.HANDED_OVER.name().equals(newStatusStr) || com.fooddelivery.common.enums.OrderStatus.HANDED_OVER.name().equals(newStatusStr) || "OUT_FOR_DELIVERY".equals(newStatusStr)) {
            RestaurantOrder order = ctx.getOrder();
            order.setStatus(OrderStatus.HANDED_OVER);
            order.setDeliveryStatus(com.fooddelivery.common.enums.DeliveryStatus.OUT_FOR_DELIVERY);
            ctx.getActionService().saveOrder(order);
            log.info("Order {} transitioned to HANDED_OVER", order.getOrderId());
        }
    }

    @Override
    public void handleOrderCancelled(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
        log.info("Order {} cancelled while in READY state", order.getOrderId());
    }
}
