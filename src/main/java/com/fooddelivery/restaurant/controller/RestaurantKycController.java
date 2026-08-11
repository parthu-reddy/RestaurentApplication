package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.dto.VerificationCallbackRequest;
import com.fooddelivery.restaurant.service.RestaurantOnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.util.Map;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
public class RestaurantKycController {
    private final RestaurantOnboardingService onboardingService;
    private final com.fooddelivery.restaurant.client.GovernmentIdClient governmentIdClient;

    public RestaurantKycController(RestaurantOnboardingService onboardingService, com.fooddelivery.restaurant.client.GovernmentIdClient governmentIdClient) {
        this.onboardingService = onboardingService;
        this.governmentIdClient = governmentIdClient;
    }

    // Callback from GovernmentIDValidationService
    @PostMapping("/api/v1/internal/brands/{brandId}/verification-callback")
    public ResponseEntity<Void> updateVerificationStatus(@PathVariable UUID brandId, @RequestBody VerificationCallbackRequest request) {
        onboardingService.updateVerificationStatusFromCallback(brandId, request.getVerificationType(), request.getStatus(), request.getLegalEntityName(), request.getBankBeneficiaryName());
        return ResponseEntity.ok().build();
    }

    // Proxy endpoints for KYC
    @GetMapping("/api/v1/restaurants/verification/upload-url")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPresignedUploadUrl(@RequestParam("docType") String docType, @RequestParam("contentType") String contentType) {
        Map<String, String> response = governmentIdClient.getPresignedUploadUrl(docType, contentType);
        return ResponseEntity.ok(ApiResponse.success(response, "Upload URL generated"));
    }

    @PostMapping("/api/v1/restaurants/verification/brands/gstin")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<ApiResponse<Void>> verifyGstin(@Valid @RequestBody com.fooddelivery.restaurant.client.GovernmentIdClient.GstinRequest request) {
        governmentIdClient.verifyGstin(request);
        return ResponseEntity.ok(ApiResponse.success(null, "GSTIN verification initiated"));
    }

    @PostMapping("/api/v1/restaurants/verification/brands/bank-account")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<ApiResponse<Void>> verifyBankAccount(@Valid @RequestBody com.fooddelivery.restaurant.client.GovernmentIdClient.BankAccountRequest request) {
        governmentIdClient.verifyBankAccount(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Bank account verification initiated"));
    }
}
