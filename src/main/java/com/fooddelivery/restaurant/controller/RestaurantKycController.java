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
    private final com.fooddelivery.common.client.GovernmentIdServiceClient governmentIdClient;
    private final com.fooddelivery.common.service.RateLimitingService rateLimitingService;

    public RestaurantKycController(RestaurantOnboardingService onboardingService, com.fooddelivery.common.client.GovernmentIdServiceClient governmentIdClient, com.fooddelivery.common.service.RateLimitingService rateLimitingService) {
        this.onboardingService = onboardingService;
        this.governmentIdClient = governmentIdClient;
        this.rateLimitingService = rateLimitingService;
    }

    // Callback from GovernmentIDValidationService
    @PostMapping("/api/v1/internal/brands/{brandId}/verification-callback")
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<Void> updateVerificationStatus(@PathVariable UUID brandId, @RequestBody VerificationCallbackRequest request) {
        onboardingService.updateVerificationStatusFromCallback(brandId, request.getVerificationType(), request.getStatus(), request.getLegalEntityName(), request.getBankBeneficiaryName());
        return ResponseEntity.ok().build();
    }

    // Proxy endpoints for KYC
    @GetMapping("/api/v1/restaurants/verification/upload-url")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPresignedUploadUrl(@RequestParam("docType") String docType, @RequestParam("contentType") String contentType, java.security.Principal principal) {
        io.github.bucket4j.Bucket bucket = rateLimitingService.resolveBucket("kyc_upload:" + (principal != null ? principal.getName() : "anonymous"), 5, 5, java.time.Duration.ofHours(1));
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS).build();
        }
        Map<String, String> response = governmentIdClient.getPresignedUploadUrl(docType, contentType);
        return ResponseEntity.ok(ApiResponse.success(response, "Upload URL generated"));
    }

    @PostMapping("/api/v1/restaurants/verification/brands/gstin")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<ApiResponse<Void>> verifyGstin(@Valid @RequestBody com.fooddelivery.common.dto.governmentid.GstinRequest request, java.security.Principal principal) {
        io.github.bucket4j.Bucket bucket = rateLimitingService.resolveBucket("kyc_gstin:" + (principal != null ? principal.getName() : "anonymous"), 5, 5, java.time.Duration.ofHours(1));
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS).build();
        }
        governmentIdClient.verifyGstin(request);
        return ResponseEntity.ok(ApiResponse.success(null, "GSTIN verification initiated"));
    }

    @PostMapping("/api/v1/restaurants/verification/brands/bank-account")
    @PreAuthorize("hasRole('RESTAURANT')")
    public ResponseEntity<ApiResponse<Void>> verifyBankAccount(@Valid @RequestBody com.fooddelivery.common.dto.governmentid.BankAccountRequest request, java.security.Principal principal) {
        io.github.bucket4j.Bucket bucket = rateLimitingService.resolveBucket("kyc_bank:" + (principal != null ? principal.getName() : "anonymous"), 5, 5, java.time.Duration.ofHours(1));
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.TOO_MANY_REQUESTS).build();
        }
        governmentIdClient.verifyBrandBankAccount(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Bank account verification initiated"));
    }
}
