package com.fooddelivery.restaurant.entity;

public enum OrderStatus {
    CREATED(10),
    PAID(20),
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
}
