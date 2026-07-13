package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "outlet_timings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@lombok.EqualsAndHashCode(exclude = "outlet")
@lombok.ToString(exclude = "outlet")
public class OutletTiming {
    
    @Id
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "outlet_id", nullable = false)
    @JsonIgnore
    private Outlet outlet;
    
    private LocalTime openingTime;
    private LocalTime closingTime;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @jakarta.persistence.Version
    private Integer version;
}
