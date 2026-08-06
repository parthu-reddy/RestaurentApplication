package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/internal/restaurants/orders")
@RequiredArgsConstructor
@Slf4j
public class InternalOrderController {

    private final RestaurantOrderRepository orderRepository;

    @GetMapping("/{orderId}/status")
    public ResponseEntity<Map<String, String>> getOrderStatus(@PathVariable UUID orderId) {
        return orderRepository.findById(orderId)
                .map(order -> ResponseEntity.ok(Map.of(
                        "orderId", order.getOrderId().toString(),
                        "status", order.getStatus().name()
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}
