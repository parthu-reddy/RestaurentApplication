package com.fooddelivery.restaurant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.Map;

@FeignClient(name = "delivery-service")
public interface DeliveryClient {

    @GetMapping("/api/v1/internal/admin/delivery/drivers/{driverId}")
    ResponseEntity<Map<String, Object>> getDriverById(@PathVariable("driverId") UUID driverId);

    @org.springframework.web.bind.annotation.PostMapping("/api/v1/internal/admin/delivery/drivers/batch")
    ResponseEntity<java.util.List<Map<String, Object>>> getDriversByIds(@org.springframework.web.bind.annotation.RequestBody java.util.List<UUID> driverIds);
}
