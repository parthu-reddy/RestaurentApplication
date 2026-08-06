package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.client.AdvertisementClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/campaigns")
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class CampaignController {

    private final AdvertisementClient advertisementClient;
    private final com.fooddelivery.restaurant.repository.BrandRepository brandRepository;

    public CampaignController(AdvertisementClient advertisementClient, com.fooddelivery.restaurant.repository.BrandRepository brandRepository) {
        this.advertisementClient = advertisementClient;
        this.brandRepository = brandRepository;
    }

    private void verifyOwnership(UUID restaurantId, Principal principal) {
        com.fooddelivery.restaurant.entity.Brand brand = brandRepository.findById(restaurantId)
            .orElseThrow(() -> new IllegalArgumentException("Restaurant/Brand not found"));
        
        if (principal == null || principal.getName() == null || !brand.getOwnerId().toString().equals(principal.getName())) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to manage campaigns for this restaurant.");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createCampaign(@RequestBody Map<String, Object> request, Principal principal) {
        if (!request.containsKey("restaurantId")) {
            throw new IllegalArgumentException("restaurantId is required");
        }
        UUID restaurantId = UUID.fromString(request.get("restaurantId").toString());
        verifyOwnership(restaurantId, principal);
        
        Object response = advertisementClient.createCampaign(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Campaign created successfully"));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<Object>> getCampaigns(@PathVariable UUID restaurantId, Principal principal) {
        verifyOwnership(restaurantId, principal);
        
        Object response = advertisementClient.getCampaigns(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(response, "Campaigns fetched successfully"));
    }

    @PutMapping("/{campaignId}/pause")
    public ResponseEntity<ApiResponse<Object>> pauseCampaign(
            @PathVariable UUID campaignId, 
            @RequestParam UUID restaurantId, 
            Principal principal) {
        verifyOwnership(restaurantId, principal);
        
        Object response = advertisementClient.pauseCampaign(campaignId);
        return ResponseEntity.ok(ApiResponse.success(response, "Campaign paused successfully"));
    }
}
