package com.fooddelivery.restaurant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FulfillmentService {

    private final com.fooddelivery.restaurant.repository.OutletRepository outletRepository;
    private final com.fooddelivery.common.outbox.repository.OutboxEventRepository outboxEventRepository;
    private final com.fooddelivery.restaurant.repository.RestaurantOrderRepository restaurantOrderRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    public java.util.List<com.fooddelivery.restaurant.entity.RestaurantOrder> getOrdersByRestaurant(UUID restaurantId) {
        return restaurantOrderRepository.findByRestaurantId(restaurantId);
    }

    @Transactional
    public void acceptOrder(UUID restaurantId, UUID orderId, Integer additionalPrepTime, String delayReason) {
        log.info("Restaurant {} accepting order {} with additional prep time {} and reason {}", 
                restaurantId, orderId, additionalPrepTime, delayReason);
        
        com.fooddelivery.restaurant.entity.Outlet restaurant = outletRepository.findById(restaurantId)
            .orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
            
        double lat = restaurant.getLocation() != null ? restaurant.getLocation().getY() : 0.0;
        double lng = restaurant.getLocation() != null ? restaurant.getLocation().getX() : 0.0;
        
        com.fooddelivery.restaurant.entity.RestaurantOrder order = restaurantOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        if (!"CREATED".equals(order.getStatus())) {
            log.info("Order {} is {}. Ignoring accept request.", orderId, order.getStatus());
            return;
        }
        
        int prepTime = order.getPrepTime() != null ? order.getPrepTime() : 15;
        
        com.fooddelivery.restaurant.service.state.RestaurantOrderState state = com.fooddelivery.restaurant.service.state.RestaurantOrderStateFactory.getState(order.getStatus());
        
        try {
            if (additionalPrepTime != null && additionalPrepTime > 10) {
                // Need customer approval for delay > 10 mins
                state.requestDelay(order);
                order.setAdditionalPrepTime(additionalPrepTime);
                restaurantOrderRepository.save(order);
                
                com.fasterxml.jackson.databind.node.ObjectNode payloadNode = objectMapper.createObjectNode();
                payloadNode.put("eventType", com.fooddelivery.common.constants.EventType.ORDER_DELAY_APPROVAL_REQUESTED);
                payloadNode.put("orderId", orderId.toString());
                payloadNode.put("restaurantId", restaurantId.toString());
                payloadNode.put("additionalPrepTimeMinutes", additionalPrepTime);
                payloadNode.put("delayReason", delayReason != null ? delayReason : "");
                String payload = objectMapper.writeValueAsString(payloadNode);
                
                com.fooddelivery.common.outbox.entity.OutboxEventEntity outbox = com.fooddelivery.common.outbox.entity.OutboxEventEntity.builder()
                        .id(java.util.UUID.randomUUID())
                        .aggregateType(com.fooddelivery.common.constants.AppConstants.AGGREGATE_ORDER)
                        .aggregateId(orderId.toString())
                        .eventType(com.fooddelivery.common.constants.EventType.ORDER_DELAY_APPROVAL_REQUESTED)
                        .payload(payload)
                        .createdAt(java.time.LocalDateTime.now())
                        .status(com.fooddelivery.common.constants.AppConstants.OUTBOX_STATUS_UNPROCESSED)
                        .build();
                outboxEventRepository.save(outbox);
                log.info("Saved ORDER_DELAY_APPROVAL_REQUESTED outbox event for order {}", orderId);
            } else {
                // <= 10 mins can be auto-approved
                if (additionalPrepTime != null) {
                    order.setAdditionalPrepTime(additionalPrepTime);
                }
                
                int finalPrepTime = prepTime + (additionalPrepTime != null ? additionalPrepTime : 0);
                long estimatedCompletionTime = System.currentTimeMillis() + (finalPrepTime * 60 * 1000L);
                
                order.setEstimatedCompletionTime(estimatedCompletionTime);
                state.accept(order);
                restaurantOrderRepository.save(order);
                
                double deliveryLat = order.getDeliveryLat() != null ? order.getDeliveryLat() : 0.0;
                double deliveryLng = order.getDeliveryLng() != null ? order.getDeliveryLng() : 0.0;
                String deliveryAddress = order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "";
                
                com.fasterxml.jackson.databind.node.ObjectNode payloadNode = objectMapper.createObjectNode();
                payloadNode.put("eventType", com.fooddelivery.common.constants.EventType.ORDER_ACCEPTED);
                payloadNode.put("orderId", orderId.toString());
                payloadNode.put("restaurantId", restaurantId.toString());
                payloadNode.put("restaurantLat", lat);
                payloadNode.put("restaurantLng", lng);
                payloadNode.put("estimatedCompletionTime", estimatedCompletionTime);
                payloadNode.put("estimatedPrepTimeMinutes", finalPrepTime);
                payloadNode.put("deliveryLat", deliveryLat);
                payloadNode.put("deliveryLng", deliveryLng);
                payloadNode.put("deliveryAddress", deliveryAddress);
                String payload = objectMapper.writeValueAsString(payloadNode);
                
                com.fooddelivery.common.outbox.entity.OutboxEventEntity outbox = com.fooddelivery.common.outbox.entity.OutboxEventEntity.builder()
                        .id(java.util.UUID.randomUUID())
                        .aggregateType(com.fooddelivery.common.constants.AppConstants.AGGREGATE_ORDER)
                        .aggregateId(orderId.toString())
                        .eventType(com.fooddelivery.common.constants.EventType.ORDER_ACCEPTED)
                        .payload(payload)
                        .createdAt(java.time.LocalDateTime.now())
                        .status(com.fooddelivery.common.constants.AppConstants.OUTBOX_STATUS_UNPROCESSED)
                        .build();
                outboxEventRepository.save(outbox);
                log.info("Saved ORDER_ACCEPTED outbox event for order {} with estimatedCompletionTime {}", orderId, estimatedCompletionTime);
            }
        } catch (Exception e) {
            log.error("Failed to publish order acceptance event for order {}", orderId, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }

    @Transactional
    public void rejectOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} rejecting order {}", restaurantId, orderId);
        
        com.fooddelivery.restaurant.entity.RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            if (!"CREATED".equals(order.getStatus())) {
                log.info("Order {} is {}. Ignoring reject request.", orderId, order.getStatus());
                return;
            }
            com.fooddelivery.restaurant.service.state.RestaurantOrderState state = com.fooddelivery.restaurant.service.state.RestaurantOrderStateFactory.getState(order.getStatus());
            state.reject(order);
            restaurantOrderRepository.save(order);
        } else {
            return;
        }
        
        try {
            com.fasterxml.jackson.databind.node.ObjectNode payloadNode = objectMapper.createObjectNode();
            payloadNode.put("eventType", com.fooddelivery.common.constants.EventType.ORDER_REJECTED);
            payloadNode.put("orderId", orderId.toString());
            payloadNode.put("restaurantId", restaurantId.toString());
            String payload = objectMapper.writeValueAsString(payloadNode);
            
            com.fooddelivery.common.outbox.entity.OutboxEventEntity outbox = com.fooddelivery.common.outbox.entity.OutboxEventEntity.builder()
                    .id(java.util.UUID.randomUUID())
                    .aggregateType(com.fooddelivery.common.constants.AppConstants.AGGREGATE_ORDER)
                    .aggregateId(orderId.toString())
                    .eventType(com.fooddelivery.common.constants.EventType.ORDER_REJECTED)
                    .payload(payload)
                    .createdAt(java.time.LocalDateTime.now())
                    .status(com.fooddelivery.common.constants.AppConstants.OUTBOX_STATUS_UNPROCESSED)
                    .build();
            outboxEventRepository.save(outbox);
            log.info("Saved ORDER_REJECTED outbox event for order {}", orderId);
        } catch (Exception e) {
            log.error("Failed to publish ORDER_REJECTED event for order {}", orderId, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }

    @Transactional
    public void readyOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} marked order {} as ready", restaurantId, orderId);
        
        com.fooddelivery.restaurant.entity.RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            if (!"ACCEPTED".equals(order.getStatus())) {
                log.info("Order {} is {}. Ignoring ready request.", orderId, order.getStatus());
                return;
            }
            com.fooddelivery.restaurant.service.state.RestaurantOrderState state = com.fooddelivery.restaurant.service.state.RestaurantOrderStateFactory.getState(order.getStatus());
            state.ready(order);
            restaurantOrderRepository.save(order);
        } else {
            return;
        }
        
        try {
            com.fasterxml.jackson.databind.node.ObjectNode payloadNode = objectMapper.createObjectNode();
            payloadNode.put("eventType", com.fooddelivery.common.constants.EventType.ORDER_READY);
            payloadNode.put("orderId", orderId.toString());
            payloadNode.put("restaurantId", restaurantId.toString());
            String payload = objectMapper.writeValueAsString(payloadNode);
            
            com.fooddelivery.common.outbox.entity.OutboxEventEntity outbox = com.fooddelivery.common.outbox.entity.OutboxEventEntity.builder()
                    .id(java.util.UUID.randomUUID())
                    .aggregateType(com.fooddelivery.common.constants.AppConstants.AGGREGATE_ORDER)
                    .aggregateId(orderId.toString())
                    .eventType(com.fooddelivery.common.constants.EventType.ORDER_READY)
                    .payload(payload)
                    .createdAt(java.time.LocalDateTime.now())
                    .status(com.fooddelivery.common.constants.AppConstants.OUTBOX_STATUS_UNPROCESSED)
                    .build();
            outboxEventRepository.save(outbox);
            log.info("Saved ORDER_READY outbox event for order {}", orderId);
        } catch (Exception e) {
            log.error("Failed to publish ORDER_READY event for order {}", orderId, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }

    @Transactional
    public void cancelOrderAfterAccept(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} cancelling order {} after acceptance", restaurantId, orderId);
        
        com.fooddelivery.restaurant.entity.RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
        if (order != null) {
            if (!"ACCEPTED".equals(order.getStatus())) {
                log.info("Order {} is {}. Ignoring cancel request.", orderId, order.getStatus());
                return;
            }
            com.fooddelivery.restaurant.service.state.RestaurantOrderState state = com.fooddelivery.restaurant.service.state.RestaurantOrderStateFactory.getState(order.getStatus());
            state.cancel(order);
            restaurantOrderRepository.save(order);
        } else {
            return;
        }
        
        try {
            com.fasterxml.jackson.databind.node.ObjectNode payloadNode = objectMapper.createObjectNode();
            payloadNode.put("eventType", com.fooddelivery.common.constants.EventType.ORDER_CANCELLED_BY_RESTAURANT);
            payloadNode.put("orderId", orderId.toString());
            payloadNode.put("restaurantId", restaurantId.toString());
            String payload = objectMapper.writeValueAsString(payloadNode);
            
            com.fooddelivery.common.outbox.entity.OutboxEventEntity outbox = com.fooddelivery.common.outbox.entity.OutboxEventEntity.builder()
                    .id(java.util.UUID.randomUUID())
                    .aggregateType(com.fooddelivery.common.constants.AppConstants.AGGREGATE_ORDER)
                    .aggregateId(orderId.toString())
                    .eventType(com.fooddelivery.common.constants.EventType.ORDER_CANCELLED_BY_RESTAURANT)
                    .payload(payload)
                    .createdAt(java.time.LocalDateTime.now())
                    .status(com.fooddelivery.common.constants.AppConstants.OUTBOX_STATUS_UNPROCESSED)
                    .build();
            outboxEventRepository.save(outbox);
            log.info("Saved ORDER_CANCELLED_BY_RESTAURANT outbox event for order {}", orderId);
        } catch (Exception e) {
            log.error("Failed to publish ORDER_CANCELLED_BY_RESTAURANT event for order {}", orderId, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }
}
