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

    @KafkaListener(topics = "order-events", groupId = "restaurant-service-group")
    public void consumeOrderEvent(String message, @org.springframework.messaging.handler.annotation.Header(value = "eventType", required = false) String headerEventType) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String jsonEventType = root.path("eventType").asText(null);
            String eventType = headerEventType != null ? headerEventType : jsonEventType;
            
            if ("ORDER_PAID".equals(eventType)) {
                String orderId = root.path("orderId").asText();
                String restaurantId = root.path("restaurantId").asText();
                log.info("Restaurant {} received new paid order {}. Awaiting restaurant staff to accept/reject.", restaurantId, orderId);
                // In a real application, we would save this to a RestaurantOrder table 
                // so the restaurant UI can fetch and display pending orders.
            } else if ("ORDER_CANCELLED".equals(eventType)) {
                String orderId = root.path("orderId").asText();
                String restaurantId = root.path("restaurantId").asText();
                log.info("Restaurant {} received ORDER_CANCELLED for order {}. Stop preparation.", restaurantId, orderId);
            }
        } catch (Exception e) {
            log.error("Failed to process order event in RestaurantApplication", e);
        }
    }
}
