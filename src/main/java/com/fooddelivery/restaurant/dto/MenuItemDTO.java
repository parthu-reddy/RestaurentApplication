package com.fooddelivery.restaurant.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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
}
