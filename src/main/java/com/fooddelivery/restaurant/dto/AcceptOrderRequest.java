package com.fooddelivery.restaurant.dto;

import lombok.Data;

@Data
public class AcceptOrderRequest {
    private Integer additionalPrepTime;
    private String delayReason;
}
