package com.fooddelivery.restaurant.service.strategy;

import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import org.springframework.stereotype.Component;

@Component
public class OrderDelayRejectedStrategy extends OrderCancelledStrategy {

    public OrderDelayRejectedStrategy(RestaurantOrderRepository restaurantOrderRepository) {
        super(restaurantOrderRepository);
    }

    @Override
    public String getEventType() {
        return EventType.ORDER_DELAY_REJECTED;
    }
}
