package com.fooddelivery.restaurant.client;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;

@Component("restaurantAdvertisementClientFallback")
public class AdvertisementClientFallback implements AdvertisementClient {

    @Override
    public Map<String, Object> getAdvertiserByExternalRef(String externalRef) {
        throw new IllegalStateException("Campaign service is currently unavailable.");
    }

    @Override
    public Object createCampaign(UUID advertiserId, Map<String, Object> request) {
        throw new IllegalStateException("Campaign service is currently unavailable.");
    }

    @Override
    public Object getCampaigns(UUID advertiserId) {
        throw new IllegalStateException("Campaign service is currently unavailable.");
    }

    @Override
    public Object pauseCampaign(UUID advertiserId, UUID campaignId) {
        throw new IllegalStateException("Campaign service is currently unavailable.");
    }
}
