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
    private final com.fooddelivery.restaurant.repository.IRestaurantRepository restaurantRepository;

    @KafkaListener(topics = "order-events", groupId = "restaurant-service-group")
    public void consumeOrderEvent(String message, @org.springframework.messaging.handler.annotation.Header(value = "eventType", required = false) String headerEventType) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String jsonEventType = root.path("eventType").asText(null);
            String eventType = headerEventType != null ? headerEventType : jsonEventType;
            
            if ("ORDER_PAID".equals(eventType)) {
                String orderId = root.path("orderId").asText();
                String restaurantId = root.path("restaurantId").asText();
                int estimatedPrepTimeMinutes = root.path("estimatedPrepTimeMinutes").asInt(15);
                
                // Store the estimated prep time in Redis for FulfillmentService to use when accepting
                redisTemplate.opsForValue().set("order:prepTime:" + orderId, String.valueOf(estimatedPrepTimeMinutes));
                
                log.info("Restaurant {} received new paid order {} with estimated prep time {}m. Awaiting restaurant staff to accept/reject.", 
                        restaurantId, orderId, estimatedPrepTimeMinutes);
                // In a real application, we would save this to a RestaurantOrder table 
                // so the restaurant UI can fetch and display pending orders.
            } else if ("ORDER_CANCELLED".equals(eventType) || "ORDER_DELAY_REJECTED".equals(eventType)) {
                String orderId = root.path("orderId").asText();
                String restaurantId = root.path("restaurantId").asText();
                log.info("Restaurant {} received {} for order {}. Stop preparation.", restaurantId, eventType, orderId);
            } else if ("ORDER_DELAY_APPROVED".equals(eventType)) {
                String orderId = root.path("orderId").asText();
                String restaurantId = root.path("restaurantId").asText();
                
                String prepTimeStr = redisTemplate.opsForValue().get("order:prepTime:" + orderId);
                int basePrepTime = prepTimeStr != null ? Integer.parseInt(prepTimeStr) : 15;
                
                String additionalStr = redisTemplate.opsForValue().get("order:additionalPrepTime:" + orderId);
                int additionalPrepTime = additionalStr != null ? Integer.parseInt(additionalStr) : 0;
                
                int finalPrepTime = basePrepTime + additionalPrepTime;
                long estimatedCompletionTime = System.currentTimeMillis() + (finalPrepTime * 60 * 1000L);
                
                // Fetch restaurant to get Lat/Lng
                com.fooddelivery.restaurant.entity.Restaurant restaurant = restaurantRepository.findById(UUID.fromString(restaurantId))
                        .orElse(null);
                double lat = 0.0;
                double lng = 0.0;
                if (restaurant != null && restaurant.getLocation() != null) {
                    lat = restaurant.getLocation().getY();
                    lng = restaurant.getLocation().getX();
                }
                
                String payload = String.format("{\"eventType\":\"ORDER_ACCEPTED\", \"orderId\":\"%s\", \"restaurantId\":\"%s\", \"restaurantLat\":%f, \"restaurantLng\":%f, \"estimatedCompletionTime\":%d, \"estimatedPrepTimeMinutes\":%d}", 
                        orderId, restaurantId, lat, lng, estimatedCompletionTime, finalPrepTime);
                
                kafkaTemplate.send("order-events", orderId, payload);
                log.info("Published ORDER_ACCEPTED for order {} after delay approval with estimatedCompletionTime {}", orderId, estimatedCompletionTime);
            }
        } catch (Exception e) {
            log.error("Failed to process order event in RestaurantApplication", e);
        }
    }
}
