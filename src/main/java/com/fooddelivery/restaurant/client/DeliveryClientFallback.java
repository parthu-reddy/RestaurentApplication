package com.fooddelivery.restaurant.client;

import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.UUID;
import java.util.Map;
import java.util.List;

@Component("restaurantDeliveryClientFallback")
@lombok.extern.slf4j.Slf4j
public class DeliveryClientFallback implements DeliveryClient {

    @Override
    public ResponseEntity<Map<String, Object>> getDriverById(UUID driverId) {
        log.error("Delivery service is down. Fallback triggered for getDriverById for {}", driverId);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }

    @Override
    public ResponseEntity<List<Map<String, Object>>> getDriversByIds(List<UUID> driverIds) {
        log.error("Delivery service is down. Fallback triggered for getDriversByIds");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
}
