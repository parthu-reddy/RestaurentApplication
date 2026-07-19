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

    @GetMapping("/api/v1/brands")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<ApiResponse<List<Brand>>> getBrands(java.security.Principal principal) {
        List<Brand> brands = onboardingService.getBrands(UUID.fromString(principal.getName()));
        return ResponseEntity.ok(ApiResponse.success(brands, "Brands retrieved successfully"));
    }

    @GetMapping("/api/v1/outlets")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<ApiResponse<List<Outlet>>> getOutlets(java.security.Principal principal) {
        List<Outlet> outlets = onboardingService.getOutletsByOwner(UUID.fromString(principal.getName()));
        return ResponseEntity.ok(ApiResponse.success(outlets, "Outlets retrieved successfully"));
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
                request.getTimings(),
                request.getBannerUrl(),
                request.getCuisine(),
                request.getRating(),
                request.getReviewsCount(),
                request.getDeliveryTime(),
                request.getDeliveryFee(),
                request.getTags()
        );
        return ResponseEntity.ok(ApiResponse.success(outlet, "Outlet onboarded successfully"));
    }
    
    @org.springframework.web.bind.annotation.PutMapping("/api/v1/outlets/{outletId}/timings")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isOutletOwner(#outletId, authentication.principal)")
    public ResponseEntity<ApiResponse<Void>> updateOutletTimings(
            @PathVariable UUID outletId,
            @Valid @RequestBody OutletTimingsUpdateRequest request) {
        
        onboardingService.updateOutletTimings(outletId, request.getTimings());
        return ResponseEntity.ok(ApiResponse.success(null, "Outlet timings updated successfully"));
    }
    
    @GetMapping("/api/v1/brands/{brandId}/outlets")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isBrandOwner(#brandId, authentication.principal)")
    public ResponseEntity<ApiResponse<List<Outlet>>> getOutletsByBrand(
            @PathVariable UUID brandId) {
        
        return ResponseEntity.ok(ApiResponse.success(onboardingService.getOutletsByBrand(brandId), "Fetched outlets"));
    }

    @org.springframework.web.bind.annotation.PutMapping("/api/v1/outlets/{outletId}/status")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isOutletOwner(#outletId, authentication.principal)")
    public ResponseEntity<ApiResponse<Void>> updateOutletStatus(
            @PathVariable UUID outletId,
            @Valid @RequestBody OutletStatusUpdateRequest request) {
        
        onboardingService.updateOutletStatus(outletId, request.getIsActive());
        return ResponseEntity.ok(ApiResponse.success(null, "Outlet status updated successfully"));
    }

    @org.springframework.web.bind.annotation.PutMapping("/api/v1/outlets/{outletId}/settings")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isOutletOwner(#outletId, authentication.principal)")
    public ResponseEntity<ApiResponse<Void>> updateOutletSettings(
            @PathVariable UUID outletId,
            @Valid @RequestBody OutletSettingsUpdateRequest request) {
        
        onboardingService.updateOutletSettings(outletId, request.getDefaultPrepTimeSeconds());
        return ResponseEntity.ok(ApiResponse.success(null, "Outlet settings updated successfully"));
    }

    // Legacy backwards compatibility: CustomerApp uses /api/v1/restaurants/{id}
    @GetMapping("/api/v1/restaurants/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRestaurant(@PathVariable UUID id) {
        Outlet outlet = onboardingService.getOutletById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("id", outlet.getId());
        response.put("name", outlet.getName());
        response.put("isActive", outlet.getIsActive());
        response.put("defaultPrepTimeSeconds", outlet.getDefaultPrepTimeSeconds() != null ? outlet.getDefaultPrepTimeSeconds() : 900);
        
        boolean isOpen = false;
        if (outlet.getTimings() != null && !outlet.getTimings().isEmpty()) {
            java.time.LocalTime now = java.time.LocalTime.now(java.time.ZoneId.of("Asia/Kolkata"));
            for (com.fooddelivery.restaurant.entity.OutletTiming timing : outlet.getTimings()) {
                java.time.LocalTime start = timing.getOpeningTime();
                java.time.LocalTime end = timing.getClosingTime();
                if (start.isBefore(end) || start.equals(end)) {
                    if (!now.isBefore(start) && !now.isAfter(end)) {
                        isOpen = true;
                        break;
                    }
                } else {
                    if (!now.isBefore(start) || !now.isAfter(end)) {
                        isOpen = true;
                        break;
                    }
                }
            }
        } else {
            isOpen = false; // default to closed if no specific timings are configured
        }
        response.put("isOpen", isOpen);
        if (outlet.getLocation() != null) {
            response.put("lat", outlet.getLocation().getY());
            response.put("lng", outlet.getLocation().getX());
        }
        response.put("bannerUrl", outlet.getBannerUrl());
        
        Brand brand = onboardingService.getBrandById(outlet.getBrandId());
        response.put("logoUrl", brand.getLogoUrl());
        return ResponseEntity.ok(ApiResponse.success(response, "Restaurant fetched successfully"));
    }

    @GetMapping("/api/v1/restaurants/nearby")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getNearbyRestaurants(
            @org.springframework.web.bind.annotation.RequestParam double lat, 
            @org.springframework.web.bind.annotation.RequestParam double lng,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "5.0") double radius) {
        
        List<Outlet> nearbyOutlets = onboardingService.getNearbyOutlets(lat, lng, radius);
        
        List<Map<String, Object>> responseList = nearbyOutlets.stream().map(outlet -> {
            Map<String, Object> response = new HashMap<>();
            response.put("id", outlet.getId());
            response.put("name", outlet.getName());
            response.put("isActive", outlet.getIsActive());
            response.put("defaultPrepTimeSeconds", outlet.getDefaultPrepTimeSeconds() != null ? outlet.getDefaultPrepTimeSeconds() : 900);
            response.put("isOpen", true); // Filtered by native query
            if (outlet.getLocation() != null) {
                response.put("lat", outlet.getLocation().getY());
                response.put("lng", outlet.getLocation().getX());
                
                // approximate distance
                double rEarth = 6371.0;
                double dLat = Math.toRadians(outlet.getLocation().getY() - lat);
                double dLon = Math.toRadians(outlet.getLocation().getX() - lng);
                double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                           Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(outlet.getLocation().getY())) *
                           Math.sin(dLon/2) * Math.sin(dLon/2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
                double distance = rEarth * c;
                response.put("distance", Math.round(distance * 10.0) / 10.0);
            } else {
                response.put("distance", 1.5);
            }
            
            // Use real DB values, fallback to dummies if null (for old records)
            response.put("image", outlet.getBannerUrl() != null ? outlet.getBannerUrl() : "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80");
            response.put("cuisine", outlet.getCuisine() != null ? outlet.getCuisine() : "Multi Cuisine");
            response.put("rating", outlet.getRating() != null ? outlet.getRating() : 0.0);
            response.put("reviewsCount", outlet.getReviewsCount() != null ? outlet.getReviewsCount() : 0);
            response.put("deliveryTime", outlet.getDeliveryTime() != null ? outlet.getDeliveryTime() : 30);
            response.put("deliveryFee", outlet.getDeliveryFee() != null ? outlet.getDeliveryFee() : 0.0);
            
            if (outlet.getTags() != null && !outlet.getTags().isEmpty()) {
                response.put("tags", java.util.Arrays.asList(outlet.getTags().split(",")));
            } else {
                response.put("tags", List.of());
            }
            
            try {
                Brand brand = onboardingService.getBrandById(outlet.getBrandId());
                response.put("logoUrl", brand.getLogoUrl());
                if (outlet.getBannerUrl() == null && brand.getLogoUrl() != null) {
                    response.put("image", brand.getLogoUrl());
                }
            } catch (Exception e) {
                // Ignore missing brand
            }
            return response;
        }).collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(responseList, "Nearby restaurants fetched"));
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
        private List<TimingRequest> timings;
        private String bannerUrl;
        
        private String cuisine;
        private Double rating;
        private Integer reviewsCount;
        private Integer deliveryTime;
        private Double deliveryFee;
        private String tags;
    }

    @Data
    public static class OutletStatusUpdateRequest {
        @NotNull
        @com.fasterxml.jackson.annotation.JsonProperty("isActive")
        private Boolean isActive;
    }
    
    @Data
    public static class OutletSettingsUpdateRequest {
        @NotNull
        private Integer defaultPrepTimeSeconds;
    }
    @Data
    public static class TimingRequest {
        @NotNull
        private LocalTime openingTime;
        @NotNull
        private LocalTime closingTime;
    }

    @Data
    public static class OutletTimingsUpdateRequest {
        @NotNull
        private List<TimingRequest> timings;
    }
}
