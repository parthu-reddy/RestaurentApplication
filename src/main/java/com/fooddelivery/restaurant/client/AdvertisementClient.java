package com.fooddelivery.restaurant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.Map;
import java.util.UUID;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.dto.AdvertiserDto;
import com.fooddelivery.restaurant.dto.CampaignDto;
import com.fooddelivery.restaurant.dto.CampaignRequestDto;
import java.util.List;

@FeignClient(name = "campaign-service", fallback = AdvertisementClientFallback.class)
public interface AdvertisementClient {

    @GetMapping("/api/v1/advertisers")
    ApiResponse<AdvertiserDto> getAdvertiserByExternalRef(@RequestParam("externalRef") String externalRef);

    @PostMapping("/api/v1/advertisers/{advertiserId}/campaigns")
    ApiResponse<CampaignDto> createCampaign(@PathVariable("advertiserId") UUID advertiserId, @RequestBody CampaignRequestDto request);

    @GetMapping("/api/v1/advertisers/{advertiserId}/campaigns")
    ApiResponse<List<CampaignDto>> getCampaigns(@PathVariable("advertiserId") UUID advertiserId);

    @PostMapping("/api/v1/advertisers/{advertiserId}/campaigns/{campaignId}/pause")
    ApiResponse<CampaignDto> pauseCampaign(@PathVariable("advertiserId") UUID advertiserId, @PathVariable("campaignId") UUID campaignId);
}
