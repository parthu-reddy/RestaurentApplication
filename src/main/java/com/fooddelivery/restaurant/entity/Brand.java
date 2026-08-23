package com.fooddelivery.restaurant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import com.fooddelivery.common.enums.VerificationStatus;

@Entity
@Table(name = "brands")
@lombok.Getter
@lombok.Setter
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class Brand {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "owner_id")
    private UUID ownerId;
    @Column(name = "name")
    private String name;
    @Column(name = "gstin", unique = true)
    private String gstin;
    @Column(name = "pan", unique = true)
    private String pan;
    @Column(name = "cin")
    private String cin;
    @Column(name = "bank_account_number")
    private String bankAccountNumber;
    @Column(name = "bank_ifsc")
    private String bankIfsc;
    @Column(name = "logo_url")
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
    @Column(name = "is_gstin_verified")
    private Boolean isGstinVerified;
    @Column(name = "is_bank_verified")
    private Boolean isBankVerified;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @jakarta.persistence.Version
    @Column(name = "version")
    private Integer version;



































public void setIsGstinVerified(final Boolean isGstinVerified) {
        this.isGstinVerified = isGstinVerified;
    }

public void setIsBankVerified(final Boolean isBankVerified) {
        this.isBankVerified = isBankVerified;
    }








}
