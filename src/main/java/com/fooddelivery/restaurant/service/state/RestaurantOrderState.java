package com.fooddelivery.restaurant.service.state;

import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.common.exception.IllegalStateTransitionException;
import com.fooddelivery.common.enums.OrderStatus;

public interface RestaurantOrderState {
    
    String PAYLOAD_FIELD_DRIVER_ID = "driverId";
    String PAYLOAD_FIELD_STATUS = "status";
    
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
        if (order.getStatus().isTerminal()) {
            return;
        }
        order.setStatus(com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
    }

    default void handleOrderCancelledByCustomer(RestaurantOrderContext ctx) {
        com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
        if (order.getStatus().isTerminal()) {
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
        if (order.getStatus().isTerminal() || order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.HANDED_OVER) {
            return;
        }
        
        com.fasterxml.jackson.databind.JsonNode payload = ctx.getEventPayload();
        if (payload != null && payload.has(PAYLOAD_FIELD_DRIVER_ID)) {
            order.setDeliveryExecutiveId(java.util.UUID.fromString(payload.path(PAYLOAD_FIELD_DRIVER_ID).asText()));
            if (payload.has("driverName")) {
                order.setRiderName(payload.path("driverName").asText());
            }
            ctx.getActionService().saveOrder(order);
        }
    }

    default void handleDriverAtRestaurant(RestaurantOrderContext ctx) {
        com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
        if (order.getStatus().isTerminal() || order.getStatus() == com.fooddelivery.restaurant.entity.OrderStatus.HANDED_OVER) {
            return;
        }
        order.setDeliveryStatus(com.fooddelivery.common.enums.DeliveryStatus.AT_RESTAURANT);
        ctx.getActionService().saveOrder(order);
    }

    default void handleOrderStatusUpdated(RestaurantOrderContext ctx) {
        String newStatusStr = ctx.getEventPayload().path(PAYLOAD_FIELD_STATUS).asText("");
        if (com.fooddelivery.restaurant.entity.OrderStatus.HANDED_OVER.name().equals(newStatusStr) || 
            com.fooddelivery.common.enums.OrderStatus.HANDED_OVER.name().equals(newStatusStr) || 
            "OUT_FOR_DELIVERY".equals(newStatusStr) ||
            "DELIVERED".equals(newStatusStr)) {
            com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
            if (!order.getStatus().isTerminal() && order.getStatus() != com.fooddelivery.restaurant.entity.OrderStatus.HANDED_OVER) {
                order.setStatus(com.fooddelivery.restaurant.entity.OrderStatus.HANDED_OVER);
                order.setDeliveryStatus(com.fooddelivery.common.enums.DeliveryStatus.OUT_FOR_DELIVERY);
                ctx.getActionService().saveOrder(order);
            }
        }
    }

    default void handleOrderStatusSync(RestaurantOrderContext ctx) {
        String targetStatusStr = ctx.getEventPayload().path(PAYLOAD_FIELD_STATUS).asText(null);
        if (targetStatusStr != null) {
            try {
                if (OrderStatus.PENDING_ACCEPTANCE.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.PENDING_ACCEPTANCE.name();
                } else if (OrderStatus.CANCELLED.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED.name();
                } else if (OrderStatus.AWAITING_DELAY_APPROVAL.name().equals(targetStatusStr)) {
                } else if (OrderStatus.AWAITING_DELAY_APPROVAL.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.AWAITING_DELAY_APPROVAL.name();
                } else if (OrderStatus.READY_FOR_PICKUP.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.READY_FOR_PICKUP.name();
                } else if (com.fooddelivery.common.enums.DeliveryStatus.OUT_FOR_DELIVERY.name().equals(targetStatusStr)) {
                    targetStatusStr = com.fooddelivery.restaurant.entity.OrderStatus.HANDED_OVER.name();
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

    default void handleManualInterventionRequired(RestaurantOrderContext ctx) {
        // Ignored by default
    }

    default void handleDeliveryFailed(RestaurantOrderContext ctx) {
        com.fooddelivery.restaurant.entity.RestaurantOrder order = ctx.getOrder();
        if (order.getStatus().isTerminal()) {
            return;
        }
        order.setStatus(com.fooddelivery.restaurant.entity.OrderStatus.CANCELLED);
        ctx.getActionService().saveOrder(order);
    }

    default void handleOrderDelivered(RestaurantOrderContext ctx) {
        // Ignored by default
    }
}
