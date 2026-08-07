package com.fooddelivery.restaurant.service.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;

public class CreatedState implements RestaurantOrderState {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(CreatedState.class);

    @Override
    public void requestDelay(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        Integer additionalPrepTime = ctx.getAdditionalPrepTime();
        if (additionalPrepTime != null && additionalPrepTime > 10) {
            order.setAdditionalPrepTime(additionalPrepTime);
            order.setStatus(OrderStatus.AWAITING_DELAY_APPROVAL);
            ctx.getActionService().saveOrder(order);
            ObjectNode payloadNode = ctx.getActionService().createPayloadNode();
            payloadNode.put("eventType", EventType.ORDER_DELAY_APPROVAL_REQUESTED.name());
            payloadNode.put("orderId", order.getOrderId().toString());
            payloadNode.put("restaurantId", order.getRestaurantId().toString());
            payloadNode.put("additionalPrepTimeMinutes", additionalPrepTime);
            payloadNode.put("delayReason", ctx.getDelayReason() != null ? ctx.getDelayReason() : "");
            ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_DELAY_APPROVAL_REQUESTED, payloadNode);
        } else {
            // Treat as accepted automatically
            accept(ctx);
        }
    }

    @Override
    public void accept(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        Integer additionalPrepTime = ctx.getAdditionalPrepTime();
        if (additionalPrepTime != null) {
            order.setAdditionalPrepTime(additionalPrepTime);
        }
        int prepTime = order.getPrepTime() != null ? order.getPrepTime() : 15;
        int finalPrepTime = prepTime + (order.getAdditionalPrepTime() != null ? order.getAdditionalPrepTime() : 0);
        long estimatedCompletionTime = System.currentTimeMillis() + (finalPrepTime * 60 * 1000L);
        order.setEstimatedCompletionTime(estimatedCompletionTime);
        order.setStatus(OrderStatus.ACCEPTED);
        ctx.getActionService().saveOrder(order);
        ObjectNode payloadNode = ctx.getActionService().createPayloadNode();
        payloadNode.put("eventType", EventType.ORDER_ACCEPTED.name());
        payloadNode.put("orderId", order.getOrderId().toString());
        payloadNode.put("restaurantId", order.getRestaurantId().toString());
        payloadNode.put("restaurantLat", ctx.getRestaurantLat());
        payloadNode.put("restaurantLng", ctx.getRestaurantLng());
        payloadNode.put("estimatedCompletionTime", estimatedCompletionTime);
        payloadNode.put("estimatedPrepTimeMinutes", finalPrepTime);
        payloadNode.put("deliveryLat", order.getDeliveryLat() != null ? order.getDeliveryLat() : 0.0);
        payloadNode.put("deliveryLng", order.getDeliveryLng() != null ? order.getDeliveryLng() : 0.0);
        payloadNode.put("deliveryAddress", order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "");
        payloadNode.put("pickupOtp", order.getPickupOtp() != null ? order.getPickupOtp() : "");
        payloadNode.put("deliveryOtp", order.getDeliveryOtp() != null ? order.getDeliveryOtp() : "");
        payloadNode.put("customerName", order.getCustomerName() != null ? order.getCustomerName() : "");
        ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_ACCEPTED, payloadNode);
    }

    @Override
    public void reject(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.CANCELLED_BY_RESTAURANT);
        ctx.getActionService().saveOrder(order);
        ObjectNode payloadNode = ctx.getActionService().createPayloadNode();
        payloadNode.put("eventType", EventType.ORDER_REJECTED.name());
        payloadNode.put("orderId", order.getOrderId().toString());
        payloadNode.put("restaurantId", order.getRestaurantId().toString());
        payloadNode.put("reason", ctx.getRejectReason() != null ? ctx.getRejectReason() : "");
        ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_REJECTED, payloadNode);
    }

    @Override
    public void handleOrderCancelled(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
        log.info("Order {} cancelled while in CREATED state", order.getOrderId());
    }

    @Override
    public void handleOrderCancelledByCustomer(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
        log.info("Order {} cancelled by customer while in CREATED state", order.getOrderId());
    }
}
