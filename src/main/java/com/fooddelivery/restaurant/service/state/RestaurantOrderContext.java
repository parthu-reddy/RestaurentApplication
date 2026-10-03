package com.fooddelivery.restaurant.service.state;

import com.fooddelivery.common.event.OrderScopedEvent;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RestaurantOrderContext {
    private RestaurantOrder order;
    private OrderScopedEvent eventPayload;
    private RestaurantActionService actionService;
    private UUID restaurantId;
    private Integer additionalPrepTime;
    private String delayReason;
    private String cancelReason;
    private String rejectReason;
    private double restaurantLat;
    private double restaurantLng;
}
