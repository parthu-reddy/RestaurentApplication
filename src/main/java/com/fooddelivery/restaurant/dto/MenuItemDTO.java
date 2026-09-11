package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;
import java.io.Serializable;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class MenuItemDTO implements Serializable {
    @NotNull
    private UUID id;
    @NotNull
    private UUID restaurantId; // outletId
    @NotNull
    private String name;
    private String description;
    @NotNull
    private BigDecimal price;
    @NotNull
    private Boolean isAvailable;
    private Integer prepTimeMinutes;
    private String imageUrl;
    private UUID categoryId;
    private String categoryName;
    private Boolean isVeg;

public void setIsAvailable(final Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
}
