package com.fooddelivery.restaurant.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NearbyRestaurantDTO {
    private UUID id;
    private String name;
    private Boolean isActive;
    private Integer defaultPrepTimeSeconds;
    private Boolean isOpen;
    private Double lat;
    private Double lng;
    private Double distance;
    private String image;
    private String cuisine;
    private Double rating;
    private Integer reviewsCount;
    private Integer deliveryTime;
    private Double deliveryFee;
    private java.util.List<String> tags;
    private UUID brandId;
    private String brandName;
    private Boolean isSponsored;
    private String logoUrl;
}
