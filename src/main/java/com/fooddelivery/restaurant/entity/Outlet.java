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
    
    private LocalTime openingTime;
    private LocalTime closingTime;
    
    private Boolean isActive;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @jakarta.persistence.Version
    private Integer version;
}
