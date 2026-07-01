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

@Entity
@Table(name = "outlet_menu_overrides")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutletMenuOverride {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID outletId;
    private UUID masterMenuItemId;
    
    private BigDecimal overriddenPrice;
    private Boolean isAvailable;
    private Integer overriddenPrepTimeMinutes;
}
