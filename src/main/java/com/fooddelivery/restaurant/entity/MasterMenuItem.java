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
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity
@Table(name = "master_menu_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterMenuItem {
    
    @Id
    private UUID id;
    
    private UUID brandId;
    
    private UUID categoryId;
    
    @NotBlank
    private String name;
    private String description;
    
    private String imageUrl;
    
    @NotNull
    @Positive
    private BigDecimal basePrice;
    @NotNull
    @Positive
    private Integer defaultPrepTimeMinutes;

    @jakarta.persistence.Version
    private Integer version;
}
