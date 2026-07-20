package com.fooddelivery.restaurant.service.state;

import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.service.state.impl.AcceptedState;
import com.fooddelivery.restaurant.service.state.impl.CreatedState;
import com.fooddelivery.restaurant.service.state.impl.DispatchedState;
import com.fooddelivery.restaurant.service.state.impl.PendingDelayState;
import com.fooddelivery.restaurant.service.state.impl.ReadyState;
import com.fooddelivery.restaurant.service.state.impl.TerminalState;

import java.util.EnumMap;
import java.util.Map;

public class RestaurantOrderStateFactory {
    
    private static final Map<OrderStatus, RestaurantOrderState> stateMap = new EnumMap<>(OrderStatus.class);
    
    private static final CreatedState CREATED = new CreatedState();
    private static final PendingDelayState PENDING_DELAY = new PendingDelayState(CREATED);
    private static final AcceptedState ACCEPTED = new AcceptedState();
    private static final ReadyState READY = new ReadyState();
    private static final DispatchedState DISPATCHED = new DispatchedState();
    private static final TerminalState TERMINAL = new TerminalState();

    static {
        stateMap.put(OrderStatus.CREATED, CREATED);
        stateMap.put(OrderStatus.ON_HOLD, PENDING_DELAY);
        stateMap.put(OrderStatus.ACCEPTED, ACCEPTED);
        stateMap.put(OrderStatus.READY, READY);
        stateMap.put(OrderStatus.DISPATCHED, DISPATCHED);
        
        stateMap.put(OrderStatus.DELIVERED, TERMINAL);
        stateMap.put(OrderStatus.DELIVERY_FAILED, TERMINAL);
        stateMap.put(OrderStatus.CANCELLED, TERMINAL);
        stateMap.put(OrderStatus.REJECTED, TERMINAL);
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
