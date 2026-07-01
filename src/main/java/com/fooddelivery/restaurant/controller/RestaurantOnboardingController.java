package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.entity.Outlet;
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
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class RestaurantOnboardingController {

    private final RestaurantOnboardingService onboardingService;

    // Phase 1: Brand Onboarding
    @PostMapping("/api/v1/brands")
    public ResponseEntity<ApiResponse<Brand>> onboardBrand(@RequestBody BrandOnboardRequest request) {
        Brand brand = onboardingService.onboardBrand(
                request.getName(),
                request.getGstin(),
                request.getPan(),
                request.getCin(),
                request.getBankAccountNumber(),
                request.getIfscCode()
        );
        return ResponseEntity.ok(ApiResponse.success(brand, "Brand onboarded successfully"));
    }

    // Phase 2: Outlet Onboarding
    @PostMapping("/api/v1/brands/{brandId}/outlets")
    public ResponseEntity<ApiResponse<Outlet>> onboardOutlet(@PathVariable UUID brandId, @RequestBody OutletOnboardRequest request) {
        Outlet outlet = onboardingService.onboardOutlet(
                brandId,
                request.getName(),
                request.getFssaiLicenseNumber(),
                request.getLat(),
                request.getLng(),
                request.getOpeningTime(),
                request.getClosingTime()
        );
        return ResponseEntity.ok(ApiResponse.success(outlet, "Outlet onboarded successfully"));
    }
    
    @GetMapping("/api/v1/brands/{brandId}/outlets")
    public ResponseEntity<ApiResponse<List<Outlet>>> getOutletsByBrand(@PathVariable UUID brandId) {
        return ResponseEntity.ok(ApiResponse.success(onboardingService.getOutletsByBrand(brandId), "Fetched outlets"));
    }

    // Legacy backwards compatibility: CustomerApp uses /api/v1/restaurants/{id}
    @GetMapping("/api/v1/restaurants/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRestaurant(@PathVariable UUID id) {
        Outlet outlet = onboardingService.getOutletById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("id", outlet.getId());
        response.put("name", outlet.getName());
        response.put("isActive", outlet.getIsActive());
        if (outlet.getLocation() != null) {
            response.put("lat", outlet.getLocation().getY());
            response.put("lng", outlet.getLocation().getX());
        }
        return ResponseEntity.ok(ApiResponse.success(response, "Restaurant fetched successfully"));
    }

    @Data
    public static class BrandOnboardRequest {
        private String name;
        private String gstin;
        private String pan;
        private String cin;
        private String bankAccountNumber;
        private String ifscCode;
    }

    @Data
    public static class OutletOnboardRequest {
        private String name;
        private String fssaiLicenseNumber;
        private Double lat;
        private Double lng;
        private LocalTime openingTime;
        private LocalTime closingTime;
    }
}
