package com.fooddelivery.restaurant.service.state.impl;

import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;

public class CreatedState implements RestaurantOrderState {
    @Override
    public void requestDelay(RestaurantOrder order) {
        // Status remains CREATED, just validating that it's allowed
    }

    @Override
    public void accept(RestaurantOrder order) {
        order.setStatus("ACCEPTED");
    }

    @Override
    public void reject(RestaurantOrder order) {
        order.setStatus("REJECTED");
    }
}
