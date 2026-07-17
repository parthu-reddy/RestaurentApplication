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
public class DriverAssignedStrategy implements RestaurantEventStrategy {

    private final RestaurantOrderRepository restaurantOrderRepository;

    @Override
    public void process(JsonNode root) throws Exception {
        String orderId = root.path("orderId").asText();
        String driverName = root.path("driverName").asText(null);
        String driverPhone = root.path("driverPhone").asText(null);

        restaurantOrderRepository.findById(UUID.fromString(orderId)).ifPresent(order -> {
            if (driverName != null) order.setRiderName(driverName);
            if (driverPhone != null) order.setRiderPhone(driverPhone);
            restaurantOrderRepository.save(order);
            log.info("Restaurant order {} driver assigned: {} ({})", orderId, driverName, driverPhone);
        });
    }

    @Override
    public String getEventType() {
        return EventType.DRIVER_ASSIGNED;
    }
}
