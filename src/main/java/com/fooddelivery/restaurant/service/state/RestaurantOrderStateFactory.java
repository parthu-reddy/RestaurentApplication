package com.fooddelivery.restaurant.service.state;

import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.service.state.impl.AcceptedState;
import com.fooddelivery.restaurant.service.state.impl.CreatedState;
import com.fooddelivery.restaurant.service.state.impl.PendingDelayState;
import com.fooddelivery.restaurant.service.state.impl.PreparingState;
import com.fooddelivery.restaurant.service.state.impl.ReadyState;
import com.fooddelivery.restaurant.service.state.impl.TerminalState;

import java.util.EnumMap;
import java.util.Map;

public class RestaurantOrderStateFactory {
    
    private static final Map<OrderStatus, RestaurantOrderState> stateMap = new EnumMap<>(OrderStatus.class);
    
    private static final CreatedState CREATED = new CreatedState();
    private static final PendingDelayState PENDING_DELAY = new PendingDelayState(CREATED);
    private static final AcceptedState ACCEPTED = new AcceptedState();
    private static final PreparingState PREPARING = new PreparingState();
    private static final ReadyState READY = new ReadyState();
    private static final TerminalState TERMINAL = new TerminalState();

    static {
        stateMap.put(OrderStatus.CREATED, CREATED);
        stateMap.put(OrderStatus.PENDING_ACCEPTANCE, CREATED); // Pending acceptance maps to CreatedState in Restaurant App
        stateMap.put(OrderStatus.AWAITING_DELAY_APPROVAL, PENDING_DELAY);
        stateMap.put(OrderStatus.ACCEPTED, ACCEPTED);
        stateMap.put(OrderStatus.PREPARING, PREPARING);
        stateMap.put(OrderStatus.READY_FOR_PICKUP, READY);
        stateMap.put(OrderStatus.HANDED_OVER, TERMINAL);
        stateMap.put(OrderStatus.CANCELLED, TERMINAL);
        stateMap.put(OrderStatus.CANCELLED_BY_RESTAURANT, TERMINAL);

    }

    public static RestaurantOrderState getState(OrderStatus status) {
        if (status == null) {
            return CREATED;
        }
        
        RestaurantOrderState state = stateMap.get(status);
        if (state == null) {
            return TERMINAL;
        }
        return state;
    }
}
