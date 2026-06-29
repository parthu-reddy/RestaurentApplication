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
    private static final String TOPIC = "order-events";

    public void acceptOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} accepting order {}", restaurantId, orderId);
        
        com.fooddelivery.restaurant.entity.Restaurant restaurant = restaurantRepository.findById(restaurantId)
            .orElseThrow(() -> new IllegalArgumentException("Restaurant not found"));
            
        double lat = restaurant.getLocation() != null ? restaurant.getLocation().getY() : 0.0;
        double lng = restaurant.getLocation() != null ? restaurant.getLocation().getX() : 0.0;
        
        String payload = String.format("{\"eventType\":\"ORDER_ACCEPTED\", \"orderId\":\"%s\", \"restaurantId\":\"%s\", \"restaurantLat\":%f, \"restaurantLng\":%f}", 
                orderId, restaurantId, lat, lng);
        
        kafkaTemplate.send(TOPIC, orderId.toString(), payload);
        log.info("Published ORDER_ACCEPTED for order {}", orderId);
    }

    public void rejectOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} rejecting order {}", restaurantId, orderId);
        
        String payload = "{\"eventType\":\"ORDER_REJECTED\", \"orderId\":\"" + orderId + "\", \"restaurantId\":\"" + restaurantId + "\"}";
        
        kafkaTemplate.send(TOPIC, orderId.toString(), payload);
        log.info("Published ORDER_REJECTED for order {}", orderId);
    }

    public void readyOrder(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} marked order {} as ready", restaurantId, orderId);
        
        String payload = "{\"eventType\":\"ORDER_READY\", \"orderId\":\"" + orderId + "\", \"restaurantId\":\"" + restaurantId + "\"}";
        
        kafkaTemplate.send(TOPIC, orderId.toString(), payload);
        log.info("Published ORDER_READY for order {}", orderId);
    }

    public void cancelOrderAfterAccept(UUID restaurantId, UUID orderId) {
        log.info("Restaurant {} cancelling order {} after acceptance", restaurantId, orderId);
        
        String payload = "{\"eventType\":\"ORDER_CANCELLED_BY_RESTAURANT\", \"orderId\":\"" + orderId + "\", \"restaurantId\":\"" + restaurantId + "\"}";
        
        kafkaTemplate.send(TOPIC, orderId.toString(), payload);
        log.info("Published ORDER_CANCELLED_BY_RESTAURANT for order {}", orderId);
    }
}
