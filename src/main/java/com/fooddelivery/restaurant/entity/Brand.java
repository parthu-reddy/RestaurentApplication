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
import com.fooddelivery.common.enums.VerificationStatus;

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
    
    private String logoUrl;
    
    @jakarta.persistence.Column(name = "legal_entity_name")
    private String legalEntityName;
    
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @jakarta.persistence.Column(name = "kyc_status")
    private VerificationStatus kycStatus = VerificationStatus.PENDING;
    
    @jakarta.persistence.Column(name = "bank_beneficiary_name")
    private String bankBeneficiaryName;
    
    @jakarta.persistence.Enumerated(jakarta.persistence.EnumType.STRING)
    @jakarta.persistence.Column(name = "penny_drop_status")
    private VerificationStatus pennyDropStatus = VerificationStatus.PENDING;
    private Boolean isGstinVerified;
    private Boolean isBankVerified;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @jakarta.persistence.Version
    private Integer version;
}
