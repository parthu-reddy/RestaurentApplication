package com.fooddelivery.restaurant.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class OverrideItemDto {
    private UUID id;
    private UUID outletId;
    private UUID masterMenuItemId;
    private BigDecimal overriddenPrice;
    private Boolean isAvailable;
    private Integer overriddenPrepTimeMinutes;
    private Integer version;
}
