package com.fooddelivery.restaurant.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "campaign-service")
public interface AdvertisementClient {

    @PostMapping("/api/v1/campaigns")
    Object createCampaign(@RequestBody Map<String, Object> request);

    @GetMapping("/api/v1/campaigns/restaurant/{restaurantId}")
    Object getCampaigns(@PathVariable UUID restaurantId);

    @PutMapping("/api/v1/campaigns/{campaignId}/pause")
    Object pauseCampaign(@PathVariable UUID campaignId);
}
