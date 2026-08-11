package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.dto.*;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.service.RestaurantOnboardingService;
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
public class RestaurantOnboardingController {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestaurantOnboardingController.class);
    private final RestaurantOnboardingService onboardingService;
    private final com.fooddelivery.restaurant.security.RestaurantSecurityHelper securityHelper;
    private final com.fooddelivery.restaurant.client.GovernmentIdClient governmentIdClient;
    private final java.util.concurrent.ScheduledExecutorService scheduler = java.util.concurrent.Executors.newScheduledThreadPool(4);

    // Phase 1: Brand Onboarding
    @PostMapping("/api/v1/brands")
    @PreAuthorize("hasRole(\'RESTAURANT\')")
    public ResponseEntity<ApiResponse<Brand>> onboardBrand(java.security.Principal principal, @Valid @RequestBody BrandOnboardRequest request) {
        Brand brand = onboardingService.onboardBrand(UUID.fromString(principal.getName()), request.getName(), request.getGstin(), request.getPan(), request.getCin(), request.getBankAccountNumber(), request.getIfscCode(), request.getLogoUrl());
        // KYC is triggered async via Outbox/Kafka in the onboardingService
        return ResponseEntity.ok(ApiResponse.success(brand, "Brand onboarded successfully. KYC pending."));
    }



    @GetMapping("/api/v1/brands")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<ApiResponse<List<Brand>>> getBrands(java.security.Principal principal) {
        List<Brand> brands = onboardingService.getBrands(UUID.fromString(principal.getName()));
        return ResponseEntity.ok(ApiResponse.success(brands, "Brands retrieved successfully"));
    }



    @java.lang.SuppressWarnings("all")
    public RestaurantOnboardingController(final RestaurantOnboardingService onboardingService, final com.fooddelivery.restaurant.security.RestaurantSecurityHelper securityHelper, final com.fooddelivery.restaurant.client.GovernmentIdClient governmentIdClient) {
        this.onboardingService = onboardingService;
        this.securityHelper = securityHelper;
        this.governmentIdClient = governmentIdClient;
    }
}
