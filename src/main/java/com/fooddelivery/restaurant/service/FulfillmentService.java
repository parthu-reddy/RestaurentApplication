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
    private final com.fooddelivery.restaurant.repository.IRestaurantRepository restaurantRepository;
    private static final String TOPIC = com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS;

    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;

    public void acceptOrder(UUID restaurantId, UUID orderId, Integer additionalPrepTime, String delayReason) {
        log.info("Restaurant {} accepting order {} with additional prep time {} and reason {}", 
                restaurantId, orderId, additionalPrepTime, delayReason);
        
        com.fooddelivery.restaurant.entity.Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
            
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
                
                String payload = String.format("{\"eventType\":\"ORDER_DELAY_APPROVAL_REQUESTED\", \"orderId\":\"%s\", \"restaurantId\":\"%s\", \"additionalPrepTimeMinutes\":%d, \"delayReason\":\"%s\"}", 
                        orderId, restaurantId, additionalPrepTime, delayReason != null ? delayReason : "");
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
                
                String payload = String.format("{\"eventType\":\"ORDER_ACCEPTED\", \"orderId\":\"%s\", \"restaurantId\":\"%s\", \"restaurantLat\":%f, \"restaurantLng\":%f, \"estimatedCompletionTime\":%d, \"estimatedPrepTimeMinutes\":%d, \"deliveryLat\":%f, \"deliveryLng\":%f, \"deliveryAddress\":\"%s\"}", 
                        orderId, restaurantId, lat, lng, estimatedCompletionTime, finalPrepTime, deliveryLat, deliveryLng, deliveryAddress.replace("\"", "\\\""));
                
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
        
        String payload = "{\"eventType\":\"ORDER_REJECTED\", \"orderId\":\"" + orderId + "\", \"restaurantId\":\"" + restaurantId + "\"}";
        
        try {
            kafkaTemplate.send(TOPIC, orderId.toString(), payload).get(3, java.util.concurrent.TimeUnit.SECONDS);
            log.info("Published ORDER_REJECTED for order {}", orderId);
        } catch (Exception e) {
            log.error("Failed to publish ORDER_REJECTED event for order {}", orderId, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }

    public void readyOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} marked order {} as ready", restaurantId, orderId);
        
        String payload = "{\"eventType\":\"ORDER_READY\", \"orderId\":\"" + orderId + "\", \"restaurantId\":\"" + restaurantId + "\"}";
        
        try {
            kafkaTemplate.send(TOPIC, orderId.toString(), payload).get(3, java.util.concurrent.TimeUnit.SECONDS);
            log.info("Published ORDER_READY for order {}", orderId);
        } catch (Exception e) {
            log.error("Failed to publish ORDER_READY event for order {}", orderId, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }

    public void cancelOrderAfterAccept(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} cancelling order {} after acceptance", restaurantId, orderId);
        
        String payload = "{\"eventType\":\"ORDER_CANCELLED_BY_RESTAURANT\", \"orderId\":\"" + orderId + "\", \"restaurantId\":\"" + restaurantId + "\"}";
        
        try {
            kafkaTemplate.send(TOPIC, orderId.toString(), payload).get(3, java.util.concurrent.TimeUnit.SECONDS);
            log.info("Published ORDER_CANCELLED_BY_RESTAURANT for order {}", orderId);
        } catch (Exception e) {
            log.error("Failed to publish ORDER_CANCELLED_BY_RESTAURANT event for order {}", orderId, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }
}
