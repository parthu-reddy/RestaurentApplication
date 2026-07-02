package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

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

}
