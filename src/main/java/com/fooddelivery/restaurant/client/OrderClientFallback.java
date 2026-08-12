package com.fooddelivery.restaurant.client;

import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.UUID;

@Component("restaurantOrderClientFallback")
public class OrderClientFallback implements OrderClient {
    @Override
    public ResponseEntity<Map<String, String>> initiatePartialRefund(UUID orderId, Map<String, String> payload) {
        throw new IllegalStateException("Customer service is currently unavailable. Failing fast to ensure financial integrity.");
    }
}
