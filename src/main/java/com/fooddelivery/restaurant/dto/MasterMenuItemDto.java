package com.fooddelivery.restaurant.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class MasterMenuItemDto {
    private UUID id;
    private UUID brandId;
    private UUID categoryId;
    private String name;
    private String description;
    private String imageUrl;
    private Boolean isVeg;
    private BigDecimal basePrice;
    private BigDecimal packingCharge;
    private Integer defaultPrepTimeMinutes;
    private Integer version;
}
