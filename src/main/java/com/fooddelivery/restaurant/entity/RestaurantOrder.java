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

import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "restaurant_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class RestaurantOrder {

    @Id
    private UUID orderId;

    private UUID restaurantId;
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    private OrderStatus status;

    public void setStatus(OrderStatus status) {
        if (this.status != null && status != null) {
            if (status.getSequence() < this.status.getSequence()) {
                log.error("Invalid state transition: Attempted to move restaurant order {} backward from {} to {}", this.orderId, this.status, status);
                throw new IllegalStateException("Cannot move order status backward from " + this.status + " to " + status);
            }
        }
        if (this.status != status) {
            log.info("Restaurant order {} status changing from {} to {}", this.orderId, this.status, status);
        }
        this.status = status;
    }

    @Version
    private Integer version;

    private Integer prepTime;
    
    private Integer additionalPrepTime;
    
    private Long estimatedCompletionTime;

    private Double deliveryLat;
    
    private Double deliveryLng;
    
    private String deliveryAddress;

    private String pickupOtp;
    
    private String deliveryOtp;

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
