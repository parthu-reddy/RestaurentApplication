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

@FeignClient(name = "campaign-service", fallback = AdvertisementClientFallback.class)
public interface AdvertisementClient {

    @GetMapping("/api/v1/advertisers")
    Map<String, Object> getAdvertiserByExternalRef(@RequestParam("externalRef") String externalRef);

    @PostMapping("/api/v1/advertisers/{advertiserId}/campaigns")
    Object createCampaign(@PathVariable("advertiserId") UUID advertiserId, @RequestBody Map<String, Object> request);

    @GetMapping("/api/v1/advertisers/{advertiserId}/campaigns")
    Object getCampaigns(@PathVariable("advertiserId") UUID advertiserId);

    @PostMapping("/api/v1/advertisers/{advertiserId}/campaigns/{campaignId}/pause")
    Object pauseCampaign(@PathVariable("advertiserId") UUID advertiserId, @PathVariable("campaignId") UUID campaignId);
}
