package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Column;
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
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "outlet_id")
    private UUID outletId;
    @Column(name = "master_menu_item_id")
    private UUID masterMenuItemId;
    
    @Positive
    @Column(name = "overridden_price")
    private BigDecimal overriddenPrice;
    @NotNull
    @Column(name = "is_available")
    private Boolean isAvailable;
    @Positive
    @Column(name = "overridden_prep_time_minutes")
    private Integer overriddenPrepTimeMinutes;

    @jakarta.persistence.Version
    @Column(name = "version")
    private Integer version;
}
