package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;

import com.fooddelivery.restaurant.service.FulfillmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/fulfillment")
@RequiredArgsConstructor
public class FulfillmentController {

    private final FulfillmentService fulfillmentService;

    @PostMapping("/orders/{orderId}/accept")
    public ResponseEntity<ApiResponse<Void>> acceptOrder(
            @PathVariable UUID restaurantId, 
            @PathVariable UUID orderId,
            @org.springframework.web.bind.annotation.RequestBody(required = false) com.fooddelivery.restaurant.dto.AcceptOrderRequest request) {
        
        Integer additionalPrepTime = request != null ? request.getAdditionalPrepTime() : null;
        String delayReason = request != null ? request.getDelayReason() : null;
        
        fulfillmentService.acceptOrder(restaurantId, orderId, additionalPrepTime, delayReason);
        return ResponseEntity.ok(ApiResponse.success(null, "Order accept processed"));
    }

    @PostMapping("/orders/{orderId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectOrder(@PathVariable UUID restaurantId, @PathVariable UUID orderId) {
        fulfillmentService.rejectOrder(restaurantId, orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Order rejected by restaurant"));
    }

    @PostMapping("/orders/{orderId}/ready")
    public ResponseEntity<ApiResponse<Void>> readyOrder(@PathVariable UUID restaurantId, @PathVariable UUID orderId) {
        fulfillmentService.readyOrder(restaurantId, orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Order marked as ready for pickup"));
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@PathVariable UUID restaurantId, @PathVariable UUID orderId) {
        fulfillmentService.cancelOrderAfterAccept(restaurantId, orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Order cancelled by restaurant after acceptance"));
    }
}
