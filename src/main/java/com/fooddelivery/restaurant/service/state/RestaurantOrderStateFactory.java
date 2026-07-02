package com.fooddelivery.restaurant.service.state;

import com.fooddelivery.restaurant.service.state.impl.AcceptedState;
import com.fooddelivery.restaurant.service.state.impl.CreatedState;
import com.fooddelivery.restaurant.service.state.impl.TerminalState;

public class RestaurantOrderStateFactory {
    
    private static final RestaurantOrderState CREATED = new CreatedState();
    private static final RestaurantOrderState ACCEPTED = new AcceptedState();
    private static final RestaurantOrderState TERMINAL = new TerminalState();

    public static RestaurantOrderState getState(String status) {
        if (status == null) {
            return CREATED;
        }
        
        switch (status.toUpperCase()) {
            case "CREATED":
                return CREATED;
            case "ACCEPTED":
                return ACCEPTED;
            case "READY":
            case "REJECTED":
            case "CANCELLED":
                return TERMINAL;
            default:
                return TERMINAL; // Unknown states act as terminal to prevent unwanted modifications
        }
    }
}
