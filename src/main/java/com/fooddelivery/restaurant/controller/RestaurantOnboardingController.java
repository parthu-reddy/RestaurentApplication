package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.entity.Restaurant;
import com.fooddelivery.restaurant.service.RestaurantOnboardingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantOnboardingController {

    private final RestaurantOnboardingService onboardingService;

    @PostMapping("/onboard")
    public ResponseEntity<ApiResponse<Restaurant>> onboardRestaurant(@RequestBody OnboardRequest request) {
        Restaurant restaurant = onboardingService.startOnboarding(
                request.getName(),
                request.getFssaiLicenseNumber(),
                request.getGstin(),
                request.getPan(),
                request.getCin(),
                request.getBankAccountNumber(),
                request.getIfscCode(),
                request.getLat(),
                request.getLng()
        );
        return ResponseEntity.ok(ApiResponse.success(restaurant, "Restaurant onboarded successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRestaurant(@PathVariable UUID id) {
        Restaurant restaurant = onboardingService.getRestaurantById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("id", restaurant.getId());
        response.put("name", restaurant.getName());
        response.put("isActive", restaurant.getIsActive());
        if (restaurant.getLocation() != null) {
            response.put("lat", restaurant.getLocation().getY());
            response.put("lng", restaurant.getLocation().getX());
        }
        return ResponseEntity.ok(ApiResponse.success(response, "Restaurant fetched successfully"));
    }

    @Data
    public static class OnboardRequest {
        private String name;
        private String fssaiLicenseNumber;
        private String gstin;
        private String pan;
        private String cin;
        private String bankAccountNumber;
        private String ifscCode;
        private Double lat;
        private Double lng;
    }
}
