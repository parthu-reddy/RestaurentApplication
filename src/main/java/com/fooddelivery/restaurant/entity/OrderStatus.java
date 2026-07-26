package com.fooddelivery.restaurant.entity;

import java.util.List;

public enum OrderStatus {
    CREATED(10),
    PENDING_ACCEPTANCE(20),
    ON_HOLD(30),
    ACCEPTED(40),
    PREPARING(50),
    READY(60),
    DISPATCHED(70),
    DELIVERED(80),
    CANCELLED(100),
    REJECTED(110),
    DELIVERY_FAILED(120);

    private final int sequence;

    OrderStatus(int sequence) {
        this.sequence = sequence;
    }

    public int getSequence() {
        return sequence;
    }

    public static final List<OrderStatus> ACTIVE_STATUSES = List.of(
        CREATED, PENDING_ACCEPTANCE, ON_HOLD, ACCEPTED, PREPARING, READY, DISPATCHED
    );

    public boolean isTerminal() {
        return this == CANCELLED || this == DELIVERED || this == REJECTED || this == DELIVERY_FAILED;
    }
}
