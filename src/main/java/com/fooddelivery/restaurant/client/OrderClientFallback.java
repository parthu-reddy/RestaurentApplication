package com.fooddelivery.restaurant.client;

import org.springframework.stereotype.Component;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.UUID;

@Component("restaurantOrderClientFallback")
public class OrderClientFallback implements OrderClient {
    @Override
    public ResponseEntity<Map<String, Object>> initiatePartialRefund(UUID orderId, Map<String, Object> payload) {
        throw new IllegalStateException("Customer service is currently unavailable. Failing fast to ensure financial integrity.");
    }

    @Override
    public ResponseEntity<com.fooddelivery.common.dto.order.RestaurantOrderEarnings> getOrderEarnings(UUID orderId) {
        throw new IllegalStateException("Customer service is currently unavailable. Failing fast to ensure financial integrity.");
    }


}
