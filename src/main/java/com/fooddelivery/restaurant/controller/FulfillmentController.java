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

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/fulfillment")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isOutletOwner(#restaurantId, authentication.principal)")
public class FulfillmentController {

    private final FulfillmentService fulfillmentService;
    private final com.fooddelivery.restaurant.security.RestaurantSecurityHelper securityHelper;

    @org.springframework.web.bind.annotation.GetMapping("/orders")
    public ResponseEntity<ApiResponse<java.util.List<com.fooddelivery.restaurant.entity.RestaurantOrder>>> getRestaurantOrders(
            @PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(fulfillmentService.getOrdersByRestaurant(restaurantId), "Orders retrieved"));
    }

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
    public ResponseEntity<ApiResponse<Void>> rejectOrder(
            @PathVariable UUID restaurantId, 
            @PathVariable UUID orderId) {
        
        fulfillmentService.rejectOrder(restaurantId, orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Order rejected by restaurant"));
    }

    @PostMapping("/orders/{orderId}/ready")
    public ResponseEntity<ApiResponse<Void>> readyOrder(
            @PathVariable UUID restaurantId, 
            @PathVariable UUID orderId) {
        
        fulfillmentService.readyOrder(restaurantId, orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Order marked as ready for pickup"));
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @PathVariable UUID restaurantId, 
            @PathVariable UUID orderId,
            @org.springframework.web.bind.annotation.RequestBody(required = false) java.util.Map<String, String> request) {
        String reason = request != null ? request.get("reason") : null;
        fulfillmentService.cancelOrderAfterAccept(restaurantId, orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Order cancelled"));
    }

    @PostMapping("/orders/{orderId}/dispatch")
    public ResponseEntity<ApiResponse<Void>> dispatchOrder(
            @PathVariable UUID restaurantId,
            @PathVariable UUID orderId,
            @org.springframework.web.bind.annotation.RequestBody java.util.Map<String, String> request) {
        String deliveryOtp = request.get("deliveryOtp"); // UI uses deliveryOtp for pickupOtp
        fulfillmentService.dispatchOrder(restaurantId, orderId, deliveryOtp);
        return ResponseEntity.ok(ApiResponse.success(null, "Order dispatched"));
    }
}
