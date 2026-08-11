package com.fooddelivery.restaurant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@FeignClient(name = "government-id-validation-service", fallback = GovernmentIdClientFallback.class)
public interface GovernmentIdClient {

    @GetMapping("/api/v1/verification/upload-url")
    Map<String, String> getPresignedUploadUrl(
            @RequestParam("docType") String docType,
            @RequestParam("contentType") String contentType);

    @GetMapping("/api/v1/verification/download-url")
    Map<String, String> getPresignedDownloadUrl(
            @RequestParam("objectKey") String objectKey);

    @PostMapping("/api/v1/verification/brands/gstin")
    void verifyGstin(@RequestBody GstinRequest request);

    @PostMapping("/api/v1/verification/brands/bank-account")
    void verifyBankAccount(@RequestBody BankAccountRequest request);

    record GstinRequest(UUID brandId, String gstin, String brandName) {}
    record BankAccountRequest(UUID brandId, String accountNumber, String ifscCode, String brandName) {}
}
