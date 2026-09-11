package com.fooddelivery.restaurant.entity;

import java.util.List;

@lombok.Getter
public enum OrderStatus {
    CREATED(10),
    PENDING_ACCEPTANCE(20),
    AWAITING_DELAY_APPROVAL(30),
    ACCEPTED(40),
    PREPARING(50),
    READY_FOR_PICKUP(60),
    HANDED_OVER(70),
    CANCELLED(100),
    CANCELLED_BY_RESTAURANT(100),
    /** Mirrors com.fooddelivery.common.enums.OrderStatus: the platform could not deliver it. */
    CANCELLED_BY_PLATFORM(100),
    /** Mirrors the common enum: the rider had the food and it did not arrive. */
    DELIVERY_FAILED(100);

    private final int sequence;

    OrderStatus(int sequence) {
        this.sequence = sequence;
    }




    /**
     * Terminal <em>for the restaurant</em>, which is not the same question the common enum answers.
     *
     * <p>HANDED_OVER is included here and not there: once the food is with the rider the restaurant
     * has nothing further to do, while the order itself is still in flight.
     */
    public boolean isTerminal() {
        return sequence == 100 || this == HANDED_OVER;
    }
}
