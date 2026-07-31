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
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
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

    private final org.springframework.transaction.support.TransactionTemplate transactionTemplate;

    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 100, multiplier = 2.0, maxDelay = 2000),
            include = {org.springframework.orm.ObjectOptimisticLockingFailureException.class, RuntimeException.class}
    )
    @KafkaListener(topics = com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS, groupId = com.fooddelivery.common.constants.KafkaConstants.GROUP_RESTAURANT_SERVICE)
    public void consumeOrderEvent(String message, @org.springframework.messaging.handler.annotation.Headers java.util.Map<String, Object> headers) {
        log.info("Consumed event from {}: {}", com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS, message);
        
        transactionTemplate.execute(status -> {
            try {
                JsonNode root = objectMapper.readTree(message);
                
                String eventType = com.fooddelivery.common.util.KafkaHeaderUtils.extractEventType(headers, root);
                
                if (eventType == null) {
                    log.warn("Event type is missing in order event: {}", message);
                    return null;
                }
                
                String orderIdStr = root.path("orderId").asText(null);
                if (orderIdStr == null) {
                    log.warn("Order ID is missing in order event: {}", message);
                    return null;
                }
                
                UUID orderId = UUID.fromString(orderIdStr);
                
                // Handle ORDER_PAID as a special case for creating the initial order entity
                if (EventType.ORDER_PAID.name().equals(eventType)) {
                    handleOrderPaid(root, orderId);
                    return null;
                }
                
                RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
                if (order == null) {
                    log.warn("Order {} not found for event type: {}", orderId, eventType);
                    return null;
                }
                
                RestaurantOrderContext ctx = RestaurantOrderContext.builder()
                        .order(order)
                        .eventPayload(root)
                        .actionService(actionService)
                        .build();
                RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
                
                try {
                    switch (EventType.valueOf(eventType)) {
                        case ORDER_CANCELLED:
                        case ORDER_CANCELLED_BY_ADMIN:
                            state.handleOrderCancelled(ctx);
                            break;
                        case ORDER_CANCELLED_BY_CUSTOMER:
                            state.handleOrderCancelledByCustomer(ctx);
                            break;
                        case ORDER_DELAY_APPROVED:
                            state.handleDelayApproved(ctx);
                            break;
                        case ORDER_DELAY_REJECTED:
                            state.handleDelayRejected(ctx);
                            break;
                        case DRIVER_ASSIGNED:
                            state.handleDriverAssigned(ctx);
                            break;
                        case ORDER_AT_RESTAURANT:
                            state.handleDriverAtRestaurant(ctx);
                            break;
                        case ORDER_STATUS_UPDATED:
                            state.handleOrderStatusUpdated(ctx);
                            break;
                        case ORDER_STATUS_SYNC:
                            state.handleOrderStatusSync(ctx);
                            break;
                        case MANUAL_INTERVENTION_REQUIRED:
                            state.handleManualInterventionRequired(ctx);
                            break;
                        case DELIVERY_FAILED:
                            state.handleDeliveryFailed(ctx);
                            break;
                        case ORDER_DELIVERED:
                            state.handleOrderDelivered(ctx);
                            break;
                        default:
                            log.info("Event {} not handled by state machine. Ignoring.", eventType);
                    }
                } catch (com.fooddelivery.common.exception.IllegalStateTransitionException e) {
                    log.warn("Illegal state transition for event {} on order {}", eventType, orderId, e);
                }
                return null;
            } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
                log.warn("Optimistic locking failure in consumeOrderEvent. Propagating for @RetryableTopic retry.");
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Error executing order event logic", e);
            }
        });
    }

    @DltHandler
    public void handleDlt(String message, @org.springframework.messaging.handler.annotation.Headers java.util.Map<String, Object> headers) {
        log.error("DLT processing: Message exhausted all retries in RestaurantApplication. Message: {}, Headers: {}", message, headers);
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
        String customerName = root.path("customerName").asText("");

        RestaurantOrder order = RestaurantOrder.builder()
                .orderId(orderId)
                .restaurantId(UUID.fromString(restaurantId))
                .customerName(customerName)
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
