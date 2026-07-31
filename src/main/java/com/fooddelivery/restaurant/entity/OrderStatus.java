package com.fooddelivery.restaurant.entity;

import java.util.List;

public enum OrderStatus {
    CREATED(10),
    PENDING_ACCEPTANCE(20),
    AWAITING_DELAY_APPROVAL(30),
    ACCEPTED(40),
    PREPARING(50),
    READY_FOR_PICKUP(60),
    HANDED_OVER(70),
    CANCELLED(100),
    CANCELLED_BY_RESTAURANT(100);

    private final int sequence;

    OrderStatus(int sequence) {
        this.sequence = sequence;
    }

    public int getSequence() {
        return sequence;
    }



    public boolean isTerminal() {
        return this == CANCELLED || this == CANCELLED_BY_RESTAURANT || this == HANDED_OVER;
    }
}
