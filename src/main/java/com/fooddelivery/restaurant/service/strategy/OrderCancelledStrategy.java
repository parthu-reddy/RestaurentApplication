package com.fooddelivery.restaurant.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderCancelledStrategy implements RestaurantEventStrategy {

    private final RestaurantOrderRepository restaurantOrderRepository;

    @Override
    public void process(JsonNode root) throws Exception {
        String orderId = root.path("orderId").asText();
        String restaurantId = root.path("restaurantId").asText();
        String eventType = root.path("eventType").asText(); // get from payload for logging
        
        restaurantOrderRepository.findById(UUID.fromString(orderId)).ifPresent(order -> {
            order.setStatus("CANCELLED");
            restaurantOrderRepository.save(order);
        });
        
        log.info("Restaurant {} received {} for order {}. Stop preparation.", restaurantId, eventType, orderId);
    }

    @Override
    public String getEventType() {
        return EventType.ORDER_CANCELLED;
    }
}
