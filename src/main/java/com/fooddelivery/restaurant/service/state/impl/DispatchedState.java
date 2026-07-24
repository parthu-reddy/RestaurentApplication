package com.fooddelivery.restaurant.service.state.impl;

import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
public class DispatchedState implements RestaurantOrderState {

    @Override
    public void handleOrderDelivered(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.DELIVERED);
        order.setDeliveryStatus(com.fooddelivery.common.enums.DeliveryStatus.DELIVERED);
        ctx.getActionService().saveOrder(order);
        log.info("Order {} transitioned to DELIVERED", order.getOrderId());
    }

    @Override
    public void handleDeliveryFailed(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.DELIVERY_FAILED);
        order.setDeliveryStatus(com.fooddelivery.common.enums.DeliveryStatus.FAILED);
        ctx.getActionService().saveOrder(order);
        log.info("Order {} transitioned to DELIVERY_FAILED", order.getOrderId());
    }

    @Override
    public void handleOrderStatusUpdated(RestaurantOrderContext ctx) {
        String newStatusStr = ctx.getEventPayload().path("status").asText("");
        try {
            OrderStatus newStatus = OrderStatus.valueOf(newStatusStr);
            if (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.DELIVERY_FAILED) {
                RestaurantOrder order = ctx.getOrder();
                order.setStatus(newStatus);
                if (newStatus == OrderStatus.DELIVERED) {
                    order.setDeliveryStatus(com.fooddelivery.common.enums.DeliveryStatus.DELIVERED);
                } else if (newStatus == OrderStatus.DELIVERY_FAILED) {
                    order.setDeliveryStatus(com.fooddelivery.common.enums.DeliveryStatus.FAILED);
                }
                ctx.getActionService().saveOrder(order);
                log.info("Order {} transitioned to {}", order.getOrderId(), newStatus);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Unknown status update {} for order {}", newStatusStr, ctx.getOrder().getOrderId());
        }
    }
}
