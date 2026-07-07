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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequiredArgsConstructor
public class RestaurantOnboardingController {

    private final RestaurantOnboardingService onboardingService;
    private final com.fooddelivery.restaurant.security.RestaurantSecurityHelper securityHelper;

    // Phase 1: Brand Onboarding
    @PostMapping("/api/v1/brands")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<ApiResponse<Brand>> onboardBrand(java.security.Principal principal, @Valid @RequestBody BrandOnboardRequest request) {
        Brand brand = onboardingService.onboardBrand(
                UUID.fromString(principal.getName()),
                request.getName(),
                request.getGstin(),
                request.getPan(),
                request.getCin(),
                request.getBankAccountNumber(),
                request.getIfscCode(),
                request.getLogoUrl()
        );
        return ResponseEntity.ok(ApiResponse.success(brand, "Brand onboarded successfully"));
    }

    // Phase 2: Outlet Onboarding
    @PostMapping("/api/v1/brands/{brandId}/outlets")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isBrandOwner(#brandId, authentication.principal)")
    public ResponseEntity<ApiResponse<Outlet>> onboardOutlet(
            @PathVariable UUID brandId, 
            @Valid @RequestBody OutletOnboardRequest request) {
        
        Outlet outlet = onboardingService.onboardOutlet(
                brandId,
                request.getName(),
                request.getFssaiLicenseNumber(),
                request.getLat(),
                request.getLng(),
                request.getOpeningTime(),
                request.getClosingTime(),
                request.getBannerUrl()
        );
        return ResponseEntity.ok(ApiResponse.success(outlet, "Outlet onboarded successfully"));
    }
    
    @GetMapping("/api/v1/brands/{brandId}/outlets")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isBrandOwner(#brandId, authentication.principal)")
    public ResponseEntity<ApiResponse<List<Outlet>>> getOutletsByBrand(
            @PathVariable UUID brandId) {
        
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
        response.put("bannerUrl", outlet.getBannerUrl());
        
        Brand brand = onboardingService.getBrandById(outlet.getBrandId());
        response.put("logoUrl", brand.getLogoUrl());
        return ResponseEntity.ok(ApiResponse.success(response, "Restaurant fetched successfully"));
    }

    @Data
    public static class BrandOnboardRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String gstin;
        @NotBlank
        private String pan;
        private String cin;
        @NotBlank
        private String bankAccountNumber;
        @NotBlank
        private String ifscCode;
        private String logoUrl;
    }

    @Data
    public static class OutletOnboardRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String fssaiLicenseNumber;
        @NotNull
        private Double lat;
        @NotNull
        private Double lng;
        @NotNull
        private LocalTime openingTime;
        @NotNull
        private LocalTime closingTime;
        private String bannerUrl;
    }
}
