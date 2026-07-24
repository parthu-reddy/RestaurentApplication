package com.fooddelivery.restaurant.service.state;

import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.common.exception.IllegalStateTransitionException;
import com.fooddelivery.common.enums.OrderStatus;

public interface RestaurantOrderState {
    
    // API Actions
    default void requestDelay(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot request delay in state: " + ctx.getOrder().getStatus());
    }

    default void accept(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot accept order in state: " + ctx.getOrder().getStatus());
    }
    
    default void prepare(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot prepare order in state: " + ctx.getOrder().getStatus());
    }

    default void reject(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot reject order in state: " + ctx.getOrder().getStatus());
    }

    default void ready(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot ready order in state: " + ctx.getOrder().getStatus());
    }

    default void cancel(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot cancel order in state: " + ctx.getOrder().getStatus());
    }

    // Kafka Event Handlers
    default void handleOrderPaid(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot process ORDER_PAID in state: " + ctx.getOrder().getStatus());
    }

    default void handleOrderCancelled(RestaurantOrderContext ctx) {
        com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
        if (order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED || 
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.DELIVERED || 
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.REJECTED ||
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.DELIVERY_FAILED) {
            return;
        }
        order.setStatus(com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
    }

    default void handleOrderCancelledByCustomer(RestaurantOrderContext ctx) {
        com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
        if (order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED || 
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.DELIVERED || 
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.REJECTED ||
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.DELIVERY_FAILED) {
            return;
        }
        order.setStatus(com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
    }

    default void handleDelayApproved(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot process ORDER_DELAY_APPROVED in state: " + ctx.getOrder().getStatus());
    }

    default void handleDelayRejected(RestaurantOrderContext ctx) {
        throw new IllegalStateTransitionException("Cannot process ORDER_DELAY_REJECTED in state: " + ctx.getOrder().getStatus());
    }

    default void handleDriverAssigned(RestaurantOrderContext ctx) {
        com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
        if (order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED || 
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.DELIVERED || 
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.REJECTED ||
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.DELIVERY_FAILED ||
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.DISPATCHED) {
            return;
        }
        
        com.fasterxml.jackson.databind.JsonNode payload = ctx.getEventPayload();
        if (payload != null && payload.has("driverId")) {
            order.setDeliveryExecutiveId(java.util.UUID.fromString(payload.path("driverId").asText()));
            ctx.getActionService().saveOrder(order);
        }
    }

    default void handleDriverAtRestaurant(RestaurantOrderContext ctx) {
        com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
        if (order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED || 
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.DELIVERED || 
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.REJECTED ||
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.DELIVERY_FAILED ||
            order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.DISPATCHED) {
            return;
        }
        order.setDeliveryStatus(com.fooddelivery.common.enums.DeliveryStatus.AT_RESTAURANT);
        ctx.getActionService().saveOrder(order);
    }

    default void handleOrderStatusUpdated(RestaurantOrderContext ctx) {
        String newStatusStr = ctx.getEventPayload().path("status").asText("");
        if (com.fooddelivery.restaurant.entity.OrderStatus.DISPATCHED.name().equals(newStatusStr) || 
            "PICKED_UP".equals(newStatusStr) || 
            "OUT_FOR_DELIVERY".equals(newStatusStr)) {
            com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
            if (order.getStatus() != com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED && 
                order.getStatus() != com.fooddelivery.restaurant.entity.OrderStatus.DELIVERED &&
                order.getStatus() != com.fooddelivery.restaurant.entity.OrderStatus.REJECTED &&
                order.getStatus() != com.fooddelivery.restaurant.entity.OrderStatus.DELIVERY_FAILED &&
                order.getStatus() != com.fooddelivery.restaurant.entity.OrderStatus.DISPATCHED) {
                order.setStatus(com.fooddelivery.restaurant.entity.OrderStatus.DISPATCHED);
                order.setDeliveryStatus(com.fooddelivery.common.enums.DeliveryStatus.OUT_FOR_DELIVERY);
                ctx.getActionService().saveOrder(order);
            }
        }
    }

    default void handleOrderStatusSync(RestaurantOrderContext ctx) {
        String targetStatusStr = ctx.getEventPayload().path("status").asText(null);
        if (targetStatusStr != null) {
            try {
                if (OrderStatus.PENDING_ACCEPTANCE.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.PENDING_ACCEPTANCE.name();
                } else if (OrderStatus.CANCELLED.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED.name();
                } else if (OrderStatus.AWAITING_DELAY_APPROVAL.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.ON_HOLD.name();
                } else if (OrderStatus.READY_FOR_PICKUP.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.READY.name();
                } else if (com.fooddelivery.common.enums.DeliveryStatus.OUT_FOR_DELIVERY.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.DISPATCHED.name();
                } else if (OrderStatus.CANCELLED_BY_RESTAURANT.name().equals(targetStatusStr) || 
                           OrderStatus.CANCELLED.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED.name();
                }
                
                com.fooddelivery.restaurant.entity.OrderStatus targetStatus = com.fooddelivery.restaurant.entity.OrderStatus.valueOf(targetStatusStr);
                com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
                
                if (targetStatus.getSequence() < order.getStatus().getSequence()) {
                    // Gracefully ignore outdated backward state syncs
                    return;
                }
                
                order.setStatus(targetStatus);
                ctx.getActionService().saveOrder(order);
            } catch (IllegalArgumentException e) {
                // Invalid status string, ignore
            }
        }
    }

    default void handleDispatchFailed(RestaurantOrderContext ctx) {
        com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
        order.setStatus(com.fooddelivery.restaurant.entity.OrderStatus.DELIVERY_FAILED);
        ctx.getActionService().saveOrder(order);
    }

    default void handleDeliveryFailed(RestaurantOrderContext ctx) {
        com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
        order.setStatus(com.fooddelivery.restaurant.entity.OrderStatus.DELIVERY_FAILED);
        ctx.getActionService().saveOrder(order);
    }

    default void handleOrderDelivered(RestaurantOrderContext ctx) {
        com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
        order.setStatus(com.fooddelivery.restaurant.entity.OrderStatus.DELIVERED);
        ctx.getActionService().saveOrder(order);
    }
}
