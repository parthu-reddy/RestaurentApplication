package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;
    private final org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;
    private final com.fooddelivery.restaurant.repository.OutletRepository outletRepository;

    @KafkaListener(topics = com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS, groupId = com.fooddelivery.common.constants.KafkaConstants.GROUP_RESTAURANT_SERVICE)
    public void consumeOrderEvent(String message, @org.springframework.messaging.handler.annotation.Header(value = "eventType", required = false) String headerEventType) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String jsonEventType = root.path("eventType").asText(null);
            String eventType = headerEventType != null ? headerEventType : jsonEventType;
            
            if (com.fooddelivery.common.constants.EventType.ORDER_PAID.equals(eventType)) {
                String orderId = root.path("orderId").asText();
                String restaurantId = root.path("restaurantId").asText();
                int estimatedPrepTimeMinutes = root.path("estimatedPrepTimeMinutes").asInt(15);
                double deliveryLat = root.path("deliveryLat").asDouble(0.0);
                double deliveryLng = root.path("deliveryLng").asDouble(0.0);
                String deliveryAddress = root.path("deliveryAddress").asText("");
                
                // Store the estimated prep time in Redis for FulfillmentService to use when accepting
                redisTemplate.opsForValue().set("order:prepTime:" + orderId, String.valueOf(estimatedPrepTimeMinutes));
                redisTemplate.opsForValue().set("order:deliveryLat:" + orderId, String.valueOf(deliveryLat));
                redisTemplate.opsForValue().set("order:deliveryLng:" + orderId, String.valueOf(deliveryLng));
                redisTemplate.opsForValue().set("order:deliveryAddress:" + orderId, deliveryAddress);
                
                log.info("Restaurant {} received new paid order {} with estimated prep time {}m. Awaiting restaurant staff to accept/reject.", 
                        restaurantId, orderId, estimatedPrepTimeMinutes);
                // In a real application, we would save this to a RestaurantOrder table 
                // so the restaurant UI can fetch and display pending orders.
            } else if (com.fooddelivery.common.constants.EventType.ORDER_CANCELLED.equals(eventType) || com.fooddelivery.common.constants.EventType.ORDER_DELAY_REJECTED.equals(eventType)) {
                String orderId = root.path("orderId").asText();
                String restaurantId = root.path("restaurantId").asText();
                log.info("Restaurant {} received {} for order {}. Stop preparation.", restaurantId, eventType, orderId);
            } else if (com.fooddelivery.common.constants.EventType.ORDER_DELAY_APPROVED.equals(eventType)) {
                String orderId = root.path("orderId").asText();
                String restaurantId = root.path("restaurantId").asText();
                
                String prepTimeStr = redisTemplate.opsForValue().get("order:prepTime:" + orderId);
                int basePrepTime = prepTimeStr != null ? Integer.parseInt(prepTimeStr) : 15;
                
                String additionalStr = redisTemplate.opsForValue().get("order:additionalPrepTime:" + orderId);
                int additionalPrepTime = additionalStr != null ? Integer.parseInt(additionalStr) : 0;
                
                int finalPrepTime = basePrepTime + additionalPrepTime;
                long estimatedCompletionTime = System.currentTimeMillis() + (finalPrepTime * 60 * 1000L);
                
                // Fetch restaurant to get Lat/Lng
                com.fooddelivery.restaurant.entity.Outlet restaurant = outletRepository.findById(UUID.fromString(restaurantId))
                        .orElse(null);
                double lat = 0.0;
                double lng = 0.0;
                if (restaurant != null && restaurant.getLocation() != null) {
                    lat = restaurant.getLocation().getY();
                    lng = restaurant.getLocation().getX();
                }
                
                String dLatStr = redisTemplate.opsForValue().get("order:deliveryLat:" + orderId);
                String dLngStr = redisTemplate.opsForValue().get("order:deliveryLng:" + orderId);
                double deliveryLat = dLatStr != null ? Double.parseDouble(dLatStr) : 0.0;
                double deliveryLng = dLngStr != null ? Double.parseDouble(dLngStr) : 0.0;
                String deliveryAddress = redisTemplate.opsForValue().get("order:deliveryAddress:" + orderId);
                if (deliveryAddress == null) deliveryAddress = "";
                
                com.fasterxml.jackson.databind.node.ObjectNode payloadNode = objectMapper.createObjectNode();
                payloadNode.put("eventType", "ORDER_ACCEPTED");
                payloadNode.put("orderId", orderId);
                payloadNode.put("restaurantId", restaurantId);
                payloadNode.put("restaurantLat", lat);
                payloadNode.put("restaurantLng", lng);
                payloadNode.put("estimatedCompletionTime", estimatedCompletionTime);
                payloadNode.put("estimatedPrepTimeMinutes", finalPrepTime);
                payloadNode.put("deliveryLat", deliveryLat);
                payloadNode.put("deliveryLng", deliveryLng);
                payloadNode.put("deliveryAddress", deliveryAddress);
                String payload = objectMapper.writeValueAsString(payloadNode);
                
                kafkaTemplate.send(com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS, orderId, payload).get(3, java.util.concurrent.TimeUnit.SECONDS);
                log.info("Published ORDER_ACCEPTED for order {} after delay approval with estimatedCompletionTime {}", orderId, estimatedCompletionTime);
            }
        } catch (Exception e) {
            log.error("Failed to process order event in RestaurantApplication", e);
            throw new RuntimeException("Failed to process order event in RestaurantApplication", e);
        }
    }
}
