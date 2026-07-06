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

@Entity
@Table(name = "brands")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Brand {
    
    @Id
    private UUID id;
    
    private UUID ownerId;
    
    private String name;
    private String gstin;
    private String pan;
    private String cin;
    private String bankAccountNumber;
    private String bankIfsc;
    
    private Boolean isGstinVerified;
    private Boolean isBankVerified;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
