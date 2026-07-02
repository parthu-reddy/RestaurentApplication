package com.fooddelivery.restaurant.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;

public interface RestaurantEventStrategy {
    void process(JsonNode rootNode) throws Exception;
    String getEventType();
}
