package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "outlet_menu_overrides")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutletMenuOverride {
    
    @Id
    private UUID id;
    
    private UUID outletId;
    private UUID masterMenuItemId;
    
    @Positive
    private BigDecimal overriddenPrice;
    @NotNull
    private Boolean isAvailable;
    @Positive
    private Integer overriddenPrepTimeMinutes;

    @jakarta.persistence.Version
    private Integer version;
}
