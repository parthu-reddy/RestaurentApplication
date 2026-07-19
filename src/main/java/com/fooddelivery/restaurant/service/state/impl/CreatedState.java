package com.fooddelivery.restaurant.service.state.impl;

import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;

public class CreatedState implements RestaurantOrderState {
    @Override
    public void requestDelay(RestaurantOrder order) {
        order.setStatus("ON_HOLD");
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
