package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "outlets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Outlet {
    
    @Id
    private UUID id;
    
    private UUID brandId;
    
    private String name;
    private String fssaiLicenseNumber;
    
    @JsonIgnore
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;
    
    private String bannerUrl;
    
    @jakarta.persistence.OneToMany(mappedBy = "outlet", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    private java.util.List<OutletTiming> timings = new java.util.ArrayList<>();
    
    private Boolean isActive;
    
    private String cuisine;
    private Double rating;
    private Integer reviewsCount;
    private Integer deliveryTime;
    private Double deliveryFee;
    private String tags;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @jakarta.persistence.Version
    private Integer version;
}
