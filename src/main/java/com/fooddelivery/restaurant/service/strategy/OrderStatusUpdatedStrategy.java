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
public class OrderStatusUpdatedStrategy implements RestaurantEventStrategy {

    private final RestaurantOrderRepository restaurantOrderRepository;

    @Override
    public void process(JsonNode root) throws Exception {
        String orderId = root.path("orderId").asText();
        String status = root.path("status").asText();

        restaurantOrderRepository.findById(UUID.fromString(orderId)).ifPresent(order -> {
            order.setStatus(status);
            restaurantOrderRepository.save(order);
            log.info("Restaurant order {} status updated to {}", orderId, status);
        });
    }

    @Override
    public String getEventType() {
        return EventType.ORDER_STATUS_UPDATED;
    }
}
