package com.fooddelivery.restaurant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "campaign-service", fallback = AdvertisementClientFallback.class)
public interface AdvertisementClient {

    @PostMapping("/api/v1/advertisers/{restaurantId}/campaigns")
    Object createCampaign(@PathVariable UUID restaurantId, @RequestBody Map<String, Object> request);

    @GetMapping("/api/v1/advertisers/{restaurantId}/campaigns")
    Object getCampaigns(@PathVariable UUID restaurantId);

    @PostMapping("/api/v1/advertisers/{restaurantId}/campaigns/{campaignId}/pause")
    Object pauseCampaign(@PathVariable UUID restaurantId, @PathVariable UUID campaignId);
}
