package com.fooddelivery.restaurant.service.state.impl;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
@lombok.extern.slf4j.Slf4j

public class CreatedState implements RestaurantOrderState {
@Override
    public void requestDelay(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        Integer additionalPrepTime = ctx.getAdditionalPrepTime();
        if (additionalPrepTime != null && additionalPrepTime > 10) {
            order.setAdditionalPrepTime(additionalPrepTime);
            order.setStatus(OrderStatus.AWAITING_DELAY_APPROVAL);
            com.fooddelivery.common.event.OrderDelayApprovalRequestedEvent event = com.fooddelivery.common.event.OrderDelayApprovalRequestedEvent.builder()
                    .orderId(order.getOrderId().toString())
                    .restaurantId(order.getRestaurantId().toString())
                    .additionalPrepTimeMinutes(additionalPrepTime)
                    .delayReason(ctx.getDelayReason() != null ? ctx.getDelayReason() : "")
                    .build();
            ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_DELAY_APPROVAL_REQUESTED, event);
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
        com.fooddelivery.common.event.OrderAcceptedEvent event = com.fooddelivery.common.event.OrderAcceptedEvent.builder()
                .orderId(order.getOrderId().toString())
                .restaurantId(order.getRestaurantId().toString())
                .restaurantLat(ctx.getRestaurantLat())
                .restaurantLng(ctx.getRestaurantLng())
                .estimatedCompletionTime(estimatedCompletionTime)
                .estimatedPrepTimeMinutes(finalPrepTime)
                .deliveryLat(order.getDeliveryLat())
                .deliveryLng(order.getDeliveryLng())
                .deliveryAddress(order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "")
                .pickupOtp(order.getPickupOtp())
                .deliveryOtp(order.getDeliveryOtp())
                .dispatchCityId(order.getDispatchCityId())
                .fleetSearchRadiusKm(order.getFleetSearchRadiusKm())
                .customerName(order.getCustomerName() != null ? order.getCustomerName() : "")
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null)
                .build();
        log.info("Dispatching ORDER_ACCEPTED for order {} with validated OTPs", order.getOrderId());
        ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_ACCEPTED, event);
    }

    @Override
    public void reject(RestaurantOrderContext ctx) {
        RestaurantOrder order = ctx.getOrder();
        order.setStatus(OrderStatus.CANCELLED_BY_RESTAURANT);
        ctx.getActionService().saveOrder(order);
        com.fooddelivery.common.event.OrderRejectedEvent event = com.fooddelivery.common.event.OrderRejectedEvent.builder()
                .orderId(order.getOrderId().toString())
                .restaurantId(order.getRestaurantId().toString())
                .reason(ctx.getRejectReason() != null ? ctx.getRejectReason() : "")
                .build();
        ctx.getActionService().publishEvent(order.getOrderId().toString(), EventType.ORDER_REJECTED, event);
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
