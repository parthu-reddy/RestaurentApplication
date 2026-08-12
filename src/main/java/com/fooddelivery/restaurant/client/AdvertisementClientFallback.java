package com.fooddelivery.restaurant.client;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.UUID;

@Component("restaurantAdvertisementClientFallback")
public class AdvertisementClientFallback implements AdvertisementClient {
    @Override
    public Object createCampaign(Map<String, Object> request) {
        throw new IllegalStateException("Campaign service is currently unavailable.");
    }

    @Override
    public Object getCampaigns(UUID restaurantId) {
        throw new IllegalStateException("Campaign service is currently unavailable.");
    }

    @Override
    public Object pauseCampaign(UUID campaignId) {
        throw new IllegalStateException("Campaign service is currently unavailable.");
    }
}
