package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

@Entity
@Table(name = "restaurant_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantOrder {

    @Id
    private UUID orderId;

    private UUID restaurantId;

    private String status;

    private Integer prepTime;
    
    private Integer additionalPrepTime;

    private Double deliveryLat;
    
    private Double deliveryLng;
    
    private String deliveryAddress;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
