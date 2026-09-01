package com.fooddelivery.restaurant.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.locationtech.jts.geom.Point;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "outlets")
@lombok.Getter
@lombok.Setter
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Outlet {
    @Id
    @Column(name = "id")
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private UUID id;
    @Column(name = "brand_id")
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private UUID brandId;
    @Column(name = "name")
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private String name;
    @Column(name = "fssai_license_number", unique = true)
    private String fssaiLicenseNumber;
    @JsonIgnore
    @Column(name = "location", columnDefinition = "geometry(Point, 4326)")
    private org.locationtech.jts.geom.Point location;
    @Column(name = "banner_url")
    private String bannerUrl;
    @jakarta.persistence.OneToMany(mappedBy = "outlet", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @lombok.Builder.Default
    private java.util.List<OutletTiming> timings = new java.util.ArrayList<>();
    @Column(name = "is_active")
    @Schema(requiredMode = RequiredMode.REQUIRED)
    private Boolean isActive;
    @Column(name = "default_prep_time_seconds")
    private Integer defaultPrepTimeSeconds;
    @Column(name = "cuisine")
    private String cuisine;
    @Column(name = "rating")
    private Double rating;
    @Column(name = "reviews_count")
    private Integer reviewsCount;
    @Column(name = "delivery_time")
    private Integer deliveryTime;
    @Column(name = "delivery_fee")
    private java.math.BigDecimal deliveryFee;
    @Column(name = "tags")
    private String tags;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @jakarta.persistence.Version
    @Column(name = "version")
    private Integer version;


























    @JsonIgnore



public void setIsActive(final Boolean isActive) {
        this.isActive = isActive;
    }















}
