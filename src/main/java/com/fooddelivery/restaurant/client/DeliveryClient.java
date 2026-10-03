package com.fooddelivery.restaurant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.Map;

@FeignClient(name = "delivery-service", fallback = DeliveryClientFallback.class)
public interface DeliveryClient {

    // Fulfilment needs a display name, not an admin driver profile. Internal calls are signed
    // with SERVICE identity and use the existing service-only summaries.
    @GetMapping("/api/v1/internal/drivers/{driverId}")
    ResponseEntity<Map<String, String>> getDriverById(@PathVariable("driverId") UUID driverId);

    @org.springframework.web.bind.annotation.PostMapping("/api/v1/internal/drivers/summaries")
    ResponseEntity<java.util.List<Map<String, String>>> getDriversByIds(@org.springframework.web.bind.annotation.RequestBody java.util.List<UUID> driverIds);
}
