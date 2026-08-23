package com.fooddelivery.restaurant.dto;

import java.math.BigDecimal;
import java.util.UUID;
import java.io.Serializable;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class MenuItemDTO implements Serializable {
    private UUID id;
    private UUID restaurantId; // outletId
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean isAvailable;
    private Integer prepTimeMinutes;
    private String imageUrl;
    private UUID categoryId;
    private String categoryName;



















public void setIsAvailable(final Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }









}
