package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.dto.*;
import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.service.RestaurantOnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class RestaurantOutletController {
    private final RestaurantOnboardingService onboardingService;

    public RestaurantOutletController(RestaurantOnboardingService onboardingService) {
        this.onboardingService = onboardingService;
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
    public ResponseEntity<ApiResponse<Outlet>> onboardOutlet(@PathVariable UUID brandId, @Valid @RequestBody OutletOnboardRequest request) {
        Outlet outlet = onboardingService.onboardOutlet(brandId, request.getName(), request.getFssaiLicenseNumber(), request.getLat(), request.getLng(), request.getTimings(), request.getBannerUrl(), request.getCuisine(), request.getRating(), request.getReviewsCount(), request.getDeliveryTime(), request.getDeliveryFee(), request.getTags());
        return ResponseEntity.ok(ApiResponse.success(outlet, "Outlet onboarded successfully"));
    }

    @PutMapping("/api/v1/outlets/{outletId}/timings")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isOutletOwner(#outletId, authentication.principal)")
    public ResponseEntity<ApiResponse<Void>> updateOutletTimings(@PathVariable UUID outletId, @Valid @RequestBody OutletTimingsUpdateRequest request) {
        onboardingService.updateOutletTimings(outletId, request.getTimings());
        return ResponseEntity.ok(ApiResponse.success(null, "Outlet timings updated successfully"));
    }

    @GetMapping("/api/v1/brands/{brandId}/outlets")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isBrandOwner(#brandId, authentication.principal)")
    public ResponseEntity<ApiResponse<List<Outlet>>> getOutletsByBrand(@PathVariable UUID brandId) {
        return ResponseEntity.ok(ApiResponse.success(onboardingService.getOutletsByBrand(brandId), "Fetched outlets"));
    }

    @PutMapping("/api/v1/outlets/{outletId}/status")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isOutletOwner(#outletId, authentication.principal)")
    public ResponseEntity<ApiResponse<Void>> updateOutletStatus(@PathVariable UUID outletId, @Valid @RequestBody OutletStatusUpdateRequest request) {
        onboardingService.updateOutletStatus(outletId, request.getIsActive());
        return ResponseEntity.ok(ApiResponse.success(null, "Outlet status updated successfully"));
    }

    @PutMapping("/api/v1/outlets/{outletId}/settings")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isOutletOwner(#outletId, authentication.principal)")
    public ResponseEntity<ApiResponse<Void>> updateOutletSettings(@PathVariable UUID outletId, @Valid @RequestBody OutletSettingsUpdateRequest request) {
        onboardingService.updateOutletSettings(outletId, request.getDefaultPrepTimeSeconds());
        return ResponseEntity.ok(ApiResponse.success(null, "Outlet settings updated successfully"));
    }

    // Legacy backwards compatibility: CustomerApp uses /api/v1/restaurants/{id}
    @GetMapping("/api/v1/restaurants/{id}")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<NearbyRestaurantDTO>> getRestaurant(@PathVariable UUID id) {
        Outlet outlet = onboardingService.getOutletById(id);
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
        }
        Brand brand = onboardingService.getBrandById(outlet.getBrandId());
        NearbyRestaurantDTO dto = NearbyRestaurantDTO.builder()
                .id(outlet.getId())
                .name(outlet.getName())
                .isActive(outlet.getIsActive())
                .defaultPrepTimeSeconds(outlet.getDefaultPrepTimeSeconds() != null ? outlet.getDefaultPrepTimeSeconds() : 900)
                .isOpen(isOpen)
                .lat(outlet.getLocation() != null ? outlet.getLocation().getY() : null)
                .lng(outlet.getLocation() != null ? outlet.getLocation().getX() : null)
                .image(outlet.getBannerUrl())
                .deliveryFee(outlet.getDeliveryFee() != null ? outlet.getDeliveryFee().doubleValue() : 0.0)
                .logoUrl(brand.getLogoUrl())
                .build();
        return ResponseEntity.ok(ApiResponse.success(dto, "Restaurant fetched successfully"));
    }

    @GetMapping("/api/v1/restaurants/nearby")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<NearbyRestaurantDTO>>> getNearbyRestaurants(@RequestParam double lat, @RequestParam double lng, @RequestParam(defaultValue = "5.0") double radius) {
        List<Outlet> nearbyOutlets = onboardingService.getNearbyOutlets(lat, lng, radius);
        List<NearbyRestaurantDTO> responseList = new java.util.ArrayList<>();
        Map<UUID, Brand> brandCache = new HashMap<>();
        for (Outlet outlet : nearbyOutlets) {
            NearbyRestaurantDTO dto = NearbyRestaurantDTO.builder()
                .id(outlet.getId())
                .name(outlet.getName())
                .isActive(outlet.getIsActive())
                .defaultPrepTimeSeconds(outlet.getDefaultPrepTimeSeconds() != null ? outlet.getDefaultPrepTimeSeconds() : 900)
                .isOpen(true)
                .build();
            double distance = 1.5;
            if (outlet.getLocation() != null) {
                dto.setLat(outlet.getLocation().getY());
                dto.setLng(outlet.getLocation().getX());
                // approximate distance
                double rEarth = 6371.0;
                double dLat = Math.toRadians(outlet.getLocation().getY() - lat);
                double dLon = Math.toRadians(outlet.getLocation().getX() - lng);
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(outlet.getLocation().getY())) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                distance = rEarth * c;
                dto.setDistance(Math.round(distance * 10.0) / 10.0);
            } else {
                dto.setDistance(1.5);
            }
            // Use real DB values, fallback to dummies if null (for old records)
            dto.setImage(outlet.getBannerUrl() != null ? outlet.getBannerUrl() : "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80");
            dto.setCuisine(outlet.getCuisine() != null ? outlet.getCuisine() : "Multi Cuisine");
            dto.setRating(outlet.getRating() != null ? outlet.getRating() : 0.0);
            dto.setReviewsCount(outlet.getReviewsCount() != null ? outlet.getReviewsCount() : 0);
            dto.setDeliveryTime(outlet.getDeliveryTime() != null ? outlet.getDeliveryTime() : 30);
            dto.setDeliveryFee(outlet.getDeliveryFee() != null ? outlet.getDeliveryFee().doubleValue() : 0.0);
            if (outlet.getTags() != null && !outlet.getTags().isEmpty()) {
                dto.setTags(java.util.Arrays.asList(outlet.getTags().split(",")));
            } else {
                dto.setTags(List.of());
            }
            try {
                Brand brand = brandCache.computeIfAbsent(outlet.getBrandId(), id -> onboardingService.getBrandById(id));
                dto.setLogoUrl(brand.getLogoUrl());
                dto.setBrandId(brand.getId());
                dto.setBrandName(brand.getName());
                if (outlet.getBannerUrl() == null && brand.getLogoUrl() != null) {
                    dto.setImage(brand.getLogoUrl());
                }
            } catch (Exception e) {
                log.debug("Brand not found for outlet: {}", outlet.getId());
            }
            // Ignore missing brand
            responseList.add(dto);
        }
        // Sort by distance (since DB groups by brand, we sort the final list by distance)
        responseList.sort(java.util.Comparator.comparingDouble(NearbyRestaurantDTO::getDistance));
        return ResponseEntity.ok(ApiResponse.success(responseList, "Nearby restaurants fetched"));
    }

    @GetMapping("/api/v1/restaurants/brands/{brandId}/outlets")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<NearbyRestaurantDTO>>> getBrandOutlets(@PathVariable UUID brandId, @RequestParam double lat, @RequestParam double lng, @RequestParam(defaultValue = "5.0") double radius) {
        List<Outlet> nearbyOutlets = onboardingService.getNearbyOutletsByBrand(brandId, lat, lng, radius);
        List<NearbyRestaurantDTO> responseList = 
        nearbyOutlets.stream().map(outlet -> {
            NearbyRestaurantDTO dto = NearbyRestaurantDTO.builder()
                .id(outlet.getId())
                .name(outlet.getName())
                .isActive(outlet.getIsActive())
                .defaultPrepTimeSeconds(outlet.getDefaultPrepTimeSeconds() != null ? outlet.getDefaultPrepTimeSeconds() : 900)
                .isOpen(true)
                .build();
            double distance = 1.5;
            if (outlet.getLocation() != null) {
                dto.setLat(outlet.getLocation().getY());
                dto.setLng(outlet.getLocation().getX());
                double rEarth = 6371.0;
                double dLat = Math.toRadians(outlet.getLocation().getY() - lat);
                double dLon = Math.toRadians(outlet.getLocation().getX() - lng);
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(outlet.getLocation().getY())) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                distance = rEarth * c;
                dto.setDistance(Math.round(distance * 10.0) / 10.0);
            } else {
                dto.setDistance(1.5);
            }
            dto.setImage(outlet.getBannerUrl() != null ? outlet.getBannerUrl() : "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80");
            dto.setCuisine(outlet.getCuisine() != null ? outlet.getCuisine() : "Multi Cuisine");
            dto.setRating(outlet.getRating() != null ? outlet.getRating() : 0.0);
            dto.setReviewsCount(outlet.getReviewsCount() != null ? outlet.getReviewsCount() : 0);
            dto.setDeliveryTime(outlet.getDeliveryTime() != null ? outlet.getDeliveryTime() : 30);
            dto.setDeliveryFee(outlet.getDeliveryFee() != null ? outlet.getDeliveryFee().doubleValue() : 0.0);
            if (outlet.getTags() != null && !outlet.getTags().isEmpty()) {
                dto.setTags(java.util.Arrays.asList(outlet.getTags().split(",")));
            } else {
                dto.setTags(List.of());
            }
            try {
                Brand brand = onboardingService.getBrandById(outlet.getBrandId());
                dto.setLogoUrl(brand.getLogoUrl());
                dto.setBrandId(brand.getId());
                dto.setBrandName(brand.getName());
                if (outlet.getBannerUrl() == null && brand.getLogoUrl() != null) {
                    dto.setImage(brand.getLogoUrl());
                }
            } catch (Exception e) {
                log.debug("Brand not found for outlet: {}", outlet.getId());
            }
            return dto;
        }).sorted(java.util.Comparator.comparingDouble(NearbyRestaurantDTO::getDistance)).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responseList, "Brand outlets fetched"));
    }

    @GetMapping("/api/v1/internal/admin/restaurants/all-with-location")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<NearbyRestaurantDTO>>> getAllOutletsWithLocation(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<Outlet> outlets = onboardingService.getAllOutlets(pageable);
        
        org.springframework.data.domain.Page<NearbyRestaurantDTO> responsePage = outlets.map(outlet -> {
            NearbyRestaurantDTO dto = NearbyRestaurantDTO.builder()
                .id(outlet.getId())
                .name(outlet.getName())
                .isActive(outlet.getIsActive())
                .build();
            if (outlet.getLocation() != null) {
                dto.setLat(outlet.getLocation().getY());
                dto.setLng(outlet.getLocation().getX());
            } else {
                dto.setLat(0.0);
                dto.setLng(0.0);
            }
            return dto;
        });
        return ResponseEntity.ok(ApiResponse.success(responsePage, "All restaurants with locations fetched"));
    }
}
// @Getter
