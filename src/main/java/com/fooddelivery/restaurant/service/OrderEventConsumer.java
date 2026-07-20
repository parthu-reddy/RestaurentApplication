package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurant.service.state.RestaurantActionService;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
import com.fooddelivery.restaurant.service.state.RestaurantOrderStateFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;
    private final RestaurantOrderRepository restaurantOrderRepository;
    private final RestaurantActionService actionService;

    @Transactional
    @KafkaListener(topics = com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS, groupId = com.fooddelivery.common.constants.KafkaConstants.GROUP_RESTAURANT_SERVICE)
    public void consumeOrderEvent(String message, @org.springframework.messaging.handler.annotation.Header(value = "eventType", required = false) String headerEventType) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String jsonEventType = root.path("eventType").asText(null);
            String eventType = headerEventType != null ? headerEventType : jsonEventType;
            
            if (eventType == null) {
                log.warn("Event type is missing in order event: {}", message);
                return;
            }
            
            String orderIdStr = root.path("orderId").asText(null);
            if (orderIdStr == null) {
                log.warn("Order ID is missing in order event: {}", message);
                return;
            }
            
            UUID orderId = UUID.fromString(orderIdStr);
            
            // Handle ORDER_PAID as a special case for creating the initial order entity
            if (EventType.ORDER_PAID.equals(eventType)) {
                handleOrderPaid(root, orderId);
                return;
            }
            
            RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
            if (order == null) {
                log.warn("Order {} not found for event type: {}", orderId, eventType);
                return;
            }
            
            RestaurantOrderContext ctx = RestaurantOrderContext.builder()
                    .order(order)
                    .eventPayload(root)
                    .actionService(actionService)
                    .build();
            
            RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
            
            try {
                switch (eventType) {
                    case EventType.ORDER_CANCELLED:
                        state.handleOrderCancelled(ctx);
                        break;
                    case EventType.ORDER_DELAY_APPROVED:
                        state.handleDelayApproved(ctx);
                        break;
                    case EventType.ORDER_DELAY_REJECTED:
                        state.handleDelayRejected(ctx);
                        break;
                    case EventType.DRIVER_ASSIGNED:
                        state.handleDriverAssigned(ctx);
                        break;
                    case EventType.ORDER_STATUS_UPDATED:
                        state.handleOrderStatusUpdated(ctx);
                        break;
                    case EventType.DISPATCH_FAILED:
                        state.handleDispatchFailed(ctx);
                        break;
                    case EventType.DELIVERY_FAILED:
                        state.handleDeliveryFailed(ctx);
                        break;
                    case EventType.ORDER_DELIVERED:
                        state.handleOrderDelivered(ctx);
                        break;
                    default:
                        log.info("Event {} not handled by state machine. Ignoring.", eventType);
                }
            } catch (com.fooddelivery.restaurant.exception.IllegalStateTransitionException e) {
                log.warn("Illegal state transition for event {} on order {}", eventType, orderId, e);
            }
            
        } catch (Exception e) {
            log.error("Failed to process order event in RestaurantApplication", e);
            throw new RuntimeException("Failed to process order event in RestaurantApplication", e);
        }
    }
    
    private void handleOrderPaid(JsonNode root, UUID orderId) {
        if (restaurantOrderRepository.existsById(orderId)) {
            log.info("Duplicate ORDER_PAID event received for order {}. Ignoring.", orderId);
            return;
        }

        String restaurantId = root.path("restaurantId").asText();
        int estimatedPrepTimeMinutes = root.path("estimatedPrepTimeMinutes").asInt(15);
        double deliveryLat = root.path("deliveryLat").asDouble(0.0);
        double deliveryLng = root.path("deliveryLng").asDouble(0.0);
        String deliveryAddress = root.path("deliveryAddress").asText("");
        String itemsJson = root.path("itemsJson").asText("[]");
        String pickupOtp = root.path("pickupOtp").asText("");
        String deliveryOtp = root.path("deliveryOtp").asText("");

        RestaurantOrder order = RestaurantOrder.builder()
                .orderId(orderId)
                .restaurantId(UUID.fromString(restaurantId))
                .status(OrderStatus.CREATED)
                .prepTime(estimatedPrepTimeMinutes)
                .additionalPrepTime(0)
                .deliveryLat(deliveryLat)
                .deliveryLng(deliveryLng)
                .deliveryAddress(deliveryAddress)
                .pickupOtp(pickupOtp)
                .deliveryOtp(deliveryOtp)
                .itemsJson(itemsJson)
                .build();
        
        actionService.saveOrder(order);
        
        log.info("Restaurant {} received new paid order {} with estimated prep time {}m. Awaiting restaurant staff to accept/reject.", 
                restaurantId, orderId, estimatedPrepTimeMinutes);
    }
}
