package com.fooddelivery.restaurant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import java.util.UUID;
import java.util.Map;

@FeignClient(name = "customer-service", fallback = OrderClientFallback.class)
public interface OrderClient {

    @PostMapping("/api/v1/internal/orders/{orderId}/partial-refund")
    ResponseEntity<Map<String, String>> initiatePartialRefund(
            @PathVariable("orderId") UUID orderId,
            @RequestBody Map<String, String> payload
    );

    @GetMapping("/api/v1/internal/orders/{orderId}/invoice")
    ResponseEntity<Map<String, Object>> getOrderInvoice(
            @PathVariable("orderId") UUID orderId
    );
}
