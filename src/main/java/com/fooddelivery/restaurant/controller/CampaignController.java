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
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class CampaignController {
private final AdvertisementClient advertisementClient;
    private final com.fooddelivery.restaurant.repository.BrandRepository brandRepository;


    private void verifyOwnership(UUID restaurantId, Principal principal) {
        com.fooddelivery.restaurant.entity.Brand brand = brandRepository.findById(restaurantId).orElseThrow(() -> new IllegalArgumentException("Restaurant/Brand not found"));
        if (principal == null || principal.getName() == null || !brand.getOwnerId().toString().equals(principal.getName())) {
            throw new org.springframework.security.access.AccessDeniedException("You do not have permission to manage campaigns for this restaurant.");
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<com.fooddelivery.restaurant.dto.CampaignDto>> createCampaign(@RequestBody com.fooddelivery.restaurant.dto.CampaignRequestDto request, Principal principal) {
        if (request.getRestaurantId() == null) {
            throw new IllegalArgumentException("restaurantId is required");
        }
        UUID restaurantId = request.getRestaurantId();
        verifyOwnership(restaurantId, principal);
        UUID advertiserId = getAdvertiserId(restaurantId);
        ApiResponse<com.fooddelivery.restaurant.dto.CampaignDto> response = advertisementClient.createCampaign(advertiserId, request);
        return ResponseEntity.ok(ApiResponse.success(response.getData(), "Campaign created successfully"));
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<java.util.List<com.fooddelivery.restaurant.dto.CampaignDto>>> getCampaigns(@PathVariable UUID restaurantId, Principal principal) {
        verifyOwnership(restaurantId, principal);
        UUID advertiserId = getAdvertiserId(restaurantId);
        ApiResponse<java.util.List<com.fooddelivery.restaurant.dto.CampaignDto>> response = advertisementClient.getCampaigns(advertiserId);
        return ResponseEntity.ok(ApiResponse.success(response.getData(), "Campaigns fetched successfully"));
    }

    @PutMapping("/{campaignId}/pause")
    public ResponseEntity<ApiResponse<com.fooddelivery.restaurant.dto.CampaignDto>> pauseCampaign(@PathVariable UUID campaignId, @RequestParam UUID restaurantId, Principal principal) {
        verifyOwnership(restaurantId, principal);
        UUID advertiserId = getAdvertiserId(restaurantId);
        ApiResponse<com.fooddelivery.restaurant.dto.CampaignDto> response = advertisementClient.pauseCampaign(advertiserId, campaignId);
        return ResponseEntity.ok(ApiResponse.success(response.getData(), "Campaign paused successfully"));
    }

    private UUID getAdvertiserId(UUID restaurantId) {
        ApiResponse<com.fooddelivery.restaurant.dto.AdvertiserDto> apiResponse = advertisementClient.getAdvertiserByExternalRef(restaurantId.toString());
        if (apiResponse != null && apiResponse.getData() != null) {
            return apiResponse.getData().getId();
        }
        throw new IllegalStateException("Could not resolve advertiser for the given restaurant");
    }
}
