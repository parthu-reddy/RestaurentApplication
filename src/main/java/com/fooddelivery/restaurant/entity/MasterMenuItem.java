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
@Table(name = "master_menu_items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterMenuItem {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    
    private UUID brandId;
    
    private String name;
    private String description;
    
    private BigDecimal basePrice;
    private Integer defaultPrepTimeMinutes;
}
