package com.fooddelivery.restaurant.service.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReadyState implements RestaurantOrderState {

    @Override
    public void cancel(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
        
        ObjectNode payloadNode = ctx.getActionService().createPayloadNode();
        payloadNode.put("eventType", EventType.ORDER_CANCELLED_BY_RESTAURANT);
        payloadNode.put("orderId", order.getOrderId().toString());
        payloadNode.put("restaurantId", order.getRestaurantId().toString());
        
        ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_CANCELLED_BY_RESTAURANT, payloadNode);
    }

    @Override
    public void dispatch(RestaurantOrderContext ctx, String otp) {
        RestaurantOrder order = ctx.getOrder();
        
        if (otp == null || otp.isEmpty()) {
            throw new RuntimeException("OTP is required");
        }
        if (!otp.equals(order.getPickupOtp()) && !otp.equals(order.getDeliveryOtp())) {
            throw new RuntimeException("Invalid OTP");
        }

        order.setStatus(OrderStatus.DISPATCHED);
        ctx.getActionService().saveOrder(order);

        ObjectNode payloadNode = ctx.getActionService().createPayloadNode();
        payloadNode.put("eventType", EventType.ORDER_STATUS_UPDATED);
        payloadNode.put("orderId", order.getOrderId().toString());
        payloadNode.put("status", OrderStatus.DISPATCHED.name());
        
        ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_STATUS_UPDATED, payloadNode);
        log.info("Order {} dispatched from restaurant {}", order.getOrderId(), order.getRestaurantId());
    }

    @Override
    public void handleDriverAssigned(RestaurantOrderContext ctx) {
        log.info("Driver assigned for order {}", ctx.getOrder().getOrderId());
    }

    @Override
    public void handleOrderStatusUpdated(RestaurantOrderContext ctx) {
        String newStatusStr = ctx.getEventPayload().path("status").asText("");
        if (OrderStatus.DISPATCHED.name().equals(newStatusStr) || "OUT_FOR_DELIVERY".equals(newStatusStr)) {
            RestaurantOrder order = ctx.getOrder();
            order.setStatus(OrderStatus.DISPATCHED);
            ctx.getActionService().saveOrder(order);
            log.info("Order {} transitioned to DISPATCHED", order.getOrderId());
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
