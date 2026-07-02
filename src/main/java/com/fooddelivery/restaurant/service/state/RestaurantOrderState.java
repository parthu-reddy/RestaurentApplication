package com.fooddelivery.restaurant.service.state;

import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.exception.IllegalStateTransitionException;

public interface RestaurantOrderState {
    default void requestDelay(RestaurantOrder order) {
        throw new IllegalStateTransitionException("Cannot request delay in state: " + order.getStatus());
    }

    default void accept(RestaurantOrder order) {
        throw new IllegalStateTransitionException("Cannot accept order in state: " + order.getStatus());
    }

    default void reject(RestaurantOrder order) {
        throw new IllegalStateTransitionException("Cannot reject order in state: " + order.getStatus());
    }

    default void ready(RestaurantOrder order) {
        throw new IllegalStateTransitionException("Cannot ready order in state: " + order.getStatus());
    }

    default void cancel(RestaurantOrder order) {
        throw new IllegalStateTransitionException("Cannot cancel order in state: " + order.getStatus());
    }
}
