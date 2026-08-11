package com.fooddelivery.restaurant.client;

import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.UUID;
import java.util.Map;
import java.util.List;

@Component
public class DeliveryClientFallback implements DeliveryClient {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(DeliveryClientFallback.class);

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
