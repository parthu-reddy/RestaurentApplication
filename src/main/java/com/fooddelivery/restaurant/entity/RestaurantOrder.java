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
import jakarta.persistence.Version;
import jakarta.persistence.Column;

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

    @Version
    private Integer version;

    private Integer prepTime;
    
    private Integer additionalPrepTime;
    
    private Long estimatedCompletionTime;

    private Double deliveryLat;
    
    private Double deliveryLng;
    
    private String deliveryAddress;

    private String pickupOtp;

    private String riderName;
    
    private String riderPhone;

    @Column(columnDefinition = "TEXT")
    private String itemsJson;
    
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
