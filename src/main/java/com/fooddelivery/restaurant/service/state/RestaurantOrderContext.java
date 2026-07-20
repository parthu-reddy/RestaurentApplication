package com.fooddelivery.restaurant.service.state;

import com.fasterxml.jackson.databind.JsonNode;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class RestaurantOrderContext {
    private RestaurantOrder order;
    private JsonNode eventPayload;
    private RestaurantActionService actionService;
    private UUID restaurantId;
    
    // Additional parameters for API requests
    private Integer additionalPrepTime;
    private String delayReason;
    
    // Additional contextual data
    private double restaurantLat;
    private double restaurantLng;
}
