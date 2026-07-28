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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

@Entity
@Table(name = "master_menu_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterMenuItem {
    
    @Id
    @Column(name = "id")
    private UUID id;
    
    @Column(name = "brand_id")
    private UUID brandId;
    
    @Column(name = "category_id")
    private UUID categoryId;
    
    @NotBlank
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @NotNull
    @Positive
    @Column(name = "base_price")
    private BigDecimal basePrice;
    
    @NotNull
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "10.0", inclusive = false)
    @Builder.Default
    @Column(name = "packing_charge")
    private BigDecimal packingCharge = BigDecimal.ZERO;
    
    @Positive
    @Column(name = "default_prep_time_minutes")
    private Integer defaultPrepTimeMinutes;

    @jakarta.persistence.Version
    @Column(name = "version")
    private Integer version;
}
