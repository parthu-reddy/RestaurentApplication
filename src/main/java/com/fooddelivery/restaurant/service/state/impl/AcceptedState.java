package com.fooddelivery.restaurant.service.state.impl;

import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;

public class AcceptedState implements RestaurantOrderState {
    @Override
    public void ready(RestaurantOrder order) {
        order.setStatus("READY");
    }

    @Override
    public void cancel(RestaurantOrder order) {
        order.setStatus("CANCELLED");
    }
}
