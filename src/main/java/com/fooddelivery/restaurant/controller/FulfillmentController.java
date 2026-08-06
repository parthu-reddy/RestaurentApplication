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
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/restaurants/{restaurantId}/fulfillment")
@RequiredArgsConstructor
@PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isOutletOwner(#restaurantId, authentication.principal)")
@Slf4j
public class FulfillmentController {

    private final FulfillmentService fulfillmentService;
    private final com.fooddelivery.restaurant.security.RestaurantSecurityHelper securityHelper;

    @org.springframework.web.bind.annotation.GetMapping("/orders")
    public ResponseEntity<ApiResponse<java.util.List<com.fooddelivery.restaurant.entity.RestaurantOrder>>> getRestaurantOrders(
            @PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(fulfillmentService.getOrdersByRestaurant(restaurantId), "Orders retrieved"));
    }

    @org.springframework.web.bind.annotation.GetMapping("/orders/active")
    public ResponseEntity<ApiResponse<java.util.List<com.fooddelivery.restaurant.entity.RestaurantOrder>>> getActiveRestaurantOrders(
            @PathVariable UUID restaurantId) {
        return ResponseEntity.ok(ApiResponse.success(fulfillmentService.getActiveOrdersByRestaurant(restaurantId), "Active orders retrieved"));
    }

    @org.springframework.web.bind.annotation.GetMapping("/orders/history")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<com.fooddelivery.restaurant.entity.RestaurantOrder>>> getHistoricalRestaurantOrders(
            @PathVariable UUID restaurantId,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String date,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(ApiResponse.success(fulfillmentService.getHistoricalOrdersByRestaurant(restaurantId, date, page, size), "Historical orders retrieved"));
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
            @PathVariable UUID orderId,
            @org.springframework.web.bind.annotation.RequestBody(required = false) java.util.Map<String, String> request) {
        String reason = request != null ? request.get("reason") : null;
        fulfillmentService.rejectOrder(restaurantId, orderId, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "Order rejected by restaurant"));
    }

    @PostMapping("/orders/{orderId}/prepare")
    public ResponseEntity<ApiResponse<Void>> prepareOrder(
            @PathVariable UUID restaurantId, 
            @PathVariable UUID orderId) {
        
        fulfillmentService.prepareOrder(restaurantId, orderId);
        return ResponseEntity.ok(ApiResponse.success(null, "Order preparation started"));
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
        fulfillmentService.cancelOrderAfterAccept(restaurantId, orderId, reason);
        return ResponseEntity.ok(ApiResponse.success(null, "Order cancelled"));
    }

}
