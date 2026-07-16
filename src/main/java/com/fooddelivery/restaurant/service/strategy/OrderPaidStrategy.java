package com.fooddelivery.restaurant.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderPaidStrategy implements RestaurantEventStrategy {

    private final RestaurantOrderRepository restaurantOrderRepository;

    @Override
    public void process(JsonNode root) throws Exception {
        String orderId = root.path("orderId").asText();
        String restaurantId = root.path("restaurantId").asText();
        int estimatedPrepTimeMinutes = root.path("estimatedPrepTimeMinutes").asInt(15);
        double deliveryLat = root.path("deliveryLat").asDouble(0.0);
        double deliveryLng = root.path("deliveryLng").asDouble(0.0);
        String deliveryAddress = root.path("deliveryAddress").asText("");
        String itemsJson = root.path("itemsJson").asText("[]");
        
        if (restaurantOrderRepository.existsById(UUID.fromString(orderId))) {
            log.info("Duplicate ORDER_PAID event received for order {}. Ignoring.", orderId);
            return;
        }

        RestaurantOrder order = RestaurantOrder.builder()
                .orderId(UUID.fromString(orderId))
                .restaurantId(UUID.fromString(restaurantId))
                .status("CREATED")
                .prepTime(estimatedPrepTimeMinutes)
                .additionalPrepTime(0)
                .deliveryLat(deliveryLat)
                .deliveryLng(deliveryLng)
                .deliveryAddress(deliveryAddress)
                .itemsJson(itemsJson)
                .build();
        restaurantOrderRepository.save(order);
        
        log.info("Restaurant {} received new paid order {} with estimated prep time {}m. Awaiting restaurant staff to accept/reject.", 
                restaurantId, orderId, estimatedPrepTimeMinutes);
    }

    @Override
    public String getEventType() {
        return EventType.ORDER_PAID;
    }
}
