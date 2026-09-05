package com.fooddelivery.restaurant.client;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.dto.AdvertiserDto;
import com.fooddelivery.restaurant.dto.CampaignDto;
import com.fooddelivery.restaurant.dto.CampaignRequestDto;
import java.util.List;

@Component("restaurantAdvertisementClientFallback")
public class AdvertisementClientFallback implements AdvertisementClient {

    @Override
    public ApiResponse<AdvertiserDto> getAdvertiserByExternalRef(String externalRef) {
        throw new IllegalStateException("Campaign service is currently unavailable.");
    }

    @Override
    public ApiResponse<CampaignDto> createCampaign(UUID advertiserId, CampaignRequestDto request) {
        throw new IllegalStateException("Campaign service is currently unavailable.");
    }

    @Override
    public ApiResponse<List<CampaignDto>> getCampaigns(UUID advertiserId) {
        throw new IllegalStateException("Campaign service is currently unavailable.");
    }

    @Override
    public ApiResponse<CampaignDto> pauseCampaign(UUID advertiserId, UUID campaignId) {
        throw new IllegalStateException("Campaign service is currently unavailable.");
    }
}
