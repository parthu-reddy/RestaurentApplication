package com.fooddelivery.restaurant.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class FulfillmentService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final com.fooddelivery.restaurant.repository.OutletRepository outletRepository;
    private final com.fasterxml.jackson.databind.ObjectMapper objectMapper;
    private static final String TOPIC = com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS;

    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    public void acceptOrder(UUID restaurantId, UUID orderId, Integer additionalPrepTime, String delayReason) {
        log.info("Restaurant {} accepting order {} with additional prep time {} and reason {}", 
                restaurantId, orderId, additionalPrepTime, delayReason);
        
        com.fooddelivery.restaurant.entity.Outlet restaurant = outletRepository.findById(restaurantId)
            .orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
            
        double lat = restaurant.getLocation() != null ? restaurant.getLocation().getY() : 0.0;
        double lng = restaurant.getLocation() != null ? restaurant.getLocation().getX() : 0.0;
        
        // Fetch estimatedPrepTimeMinutes stored when ORDER_PAID was received
        String prepTimeStr = redisTemplate.opsForValue().get("order:prepTime:" + orderId);
        int prepTime = prepTimeStr != null ? Integer.parseInt(prepTimeStr) : 15; // default 15
        
        try {
            if (additionalPrepTime != null && additionalPrepTime > 10) {
                // Need customer approval for delay > 10 mins
                // Store the requested extra time temporarily
                redisTemplate.opsForValue().set("order:additionalPrepTime:" + orderId, String.valueOf(additionalPrepTime));
                
                com.fasterxml.jackson.databind.node.ObjectNode payloadNode = objectMapper.createObjectNode();
                payloadNode.put("eventType", "ORDER_DELAY_APPROVAL_REQUESTED");
                payloadNode.put("orderId", orderId.toString());
                payloadNode.put("restaurantId", restaurantId.toString());
                payloadNode.put("additionalPrepTimeMinutes", additionalPrepTime);
                payloadNode.put("delayReason", delayReason != null ? delayReason : "");
                String payload = objectMapper.writeValueAsString(payloadNode);
                kafkaTemplate.send(TOPIC, orderId.toString(), payload).get(3, java.util.concurrent.TimeUnit.SECONDS);
                log.info("Published ORDER_DELAY_APPROVAL_REQUESTED for order {}", orderId);
            } else {
                // <= 10 mins can be auto-approved
                int finalPrepTime = prepTime + (additionalPrepTime != null ? additionalPrepTime : 0);
                long estimatedCompletionTime = System.currentTimeMillis() + (finalPrepTime * 60 * 1000L);
                
                String dLatStr = redisTemplate.opsForValue().get("order:deliveryLat:" + orderId);
                String dLngStr = redisTemplate.opsForValue().get("order:deliveryLng:" + orderId);
                double deliveryLat = dLatStr != null ? Double.parseDouble(dLatStr) : 0.0;
                double deliveryLng = dLngStr != null ? Double.parseDouble(dLngStr) : 0.0;
                String deliveryAddress = redisTemplate.opsForValue().get("order:deliveryAddress:" + orderId);
                if (deliveryAddress == null) deliveryAddress = "";
                
                com.fasterxml.jackson.databind.node.ObjectNode payloadNode = objectMapper.createObjectNode();
                payloadNode.put("eventType", "ORDER_ACCEPTED");
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
                
                kafkaTemplate.send(TOPIC, orderId.toString(), payload).get(3, java.util.concurrent.TimeUnit.SECONDS);
                log.info("Published ORDER_ACCEPTED for order {} with estimatedCompletionTime {}", orderId, estimatedCompletionTime);
            }
        } catch (Exception e) {
            log.error("Failed to publish order acceptance event for order {}", orderId, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }

    public void rejectOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} rejecting order {}", restaurantId, orderId);
        
        try {
            com.fasterxml.jackson.databind.node.ObjectNode payloadNode = objectMapper.createObjectNode();
            payloadNode.put("eventType", "ORDER_REJECTED");
            payloadNode.put("orderId", orderId.toString());
            payloadNode.put("restaurantId", restaurantId.toString());
            String payload = objectMapper.writeValueAsString(payloadNode);
            
            kafkaTemplate.send(TOPIC, orderId.toString(), payload).get(3, java.util.concurrent.TimeUnit.SECONDS);
            log.info("Published ORDER_REJECTED for order {}", orderId);
        } catch (Exception e) {
            log.error("Failed to publish ORDER_REJECTED event for order {}", orderId, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }

    public void readyOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} marked order {} as ready", restaurantId, orderId);
        
        try {
            com.fasterxml.jackson.databind.node.ObjectNode payloadNode = objectMapper.createObjectNode();
            payloadNode.put("eventType", "ORDER_READY");
            payloadNode.put("orderId", orderId.toString());
            payloadNode.put("restaurantId", restaurantId.toString());
            String payload = objectMapper.writeValueAsString(payloadNode);
            
            kafkaTemplate.send(TOPIC, orderId.toString(), payload).get(3, java.util.concurrent.TimeUnit.SECONDS);
            log.info("Published ORDER_READY for order {}", orderId);
        } catch (Exception e) {
            log.error("Failed to publish ORDER_READY event for order {}", orderId, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }

    public void cancelOrderAfterAccept(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} cancelling order {} after acceptance", restaurantId, orderId);
        
        try {
            com.fasterxml.jackson.databind.node.ObjectNode payloadNode = objectMapper.createObjectNode();
            payloadNode.put("eventType", "ORDER_CANCELLED_BY_RESTAURANT");
            payloadNode.put("orderId", orderId.toString());
            payloadNode.put("restaurantId", restaurantId.toString());
            String payload = objectMapper.writeValueAsString(payloadNode);
            
            kafkaTemplate.send(TOPIC, orderId.toString(), payload).get(3, java.util.concurrent.TimeUnit.SECONDS);
            log.info("Published ORDER_CANCELLED_BY_RESTAURANT for order {}", orderId);
        } catch (Exception e) {
            log.error("Failed to publish ORDER_CANCELLED_BY_RESTAURANT event for order {}", orderId, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }
}
