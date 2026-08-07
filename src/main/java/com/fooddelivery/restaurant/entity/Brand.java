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
public class Brand {
    @Id
    @Column(name = "id")
    private UUID id;
    @Column(name = "owner_id")
    private UUID ownerId;
    @Column(name = "name")
    private String name;
    @Column(name = "gstin")
    private String gstin;
    @Column(name = "pan")
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


    @java.lang.SuppressWarnings("all")
    public static class BrandBuilder {
        @java.lang.SuppressWarnings("all")
        private UUID id;
        @java.lang.SuppressWarnings("all")
        private UUID ownerId;
        @java.lang.SuppressWarnings("all")
        private String name;
        @java.lang.SuppressWarnings("all")
        private String gstin;
        @java.lang.SuppressWarnings("all")
        private String pan;
        @java.lang.SuppressWarnings("all")
        private String cin;
        @java.lang.SuppressWarnings("all")
        private String bankAccountNumber;
        @java.lang.SuppressWarnings("all")
        private String bankIfsc;
        @java.lang.SuppressWarnings("all")
        private String logoUrl;
        @java.lang.SuppressWarnings("all")
        private String legalEntityName;
        @java.lang.SuppressWarnings("all")
        private VerificationStatus kycStatus;
        @java.lang.SuppressWarnings("all")
        private String bankBeneficiaryName;
        @java.lang.SuppressWarnings("all")
        private VerificationStatus pennyDropStatus;
        @java.lang.SuppressWarnings("all")
        private Boolean isGstinVerified;
        @java.lang.SuppressWarnings("all")
        private Boolean isBankVerified;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime createdAt;
        @java.lang.SuppressWarnings("all")
        private LocalDateTime updatedAt;
        @java.lang.SuppressWarnings("all")
        private Integer version;

        @java.lang.SuppressWarnings("all")
        BrandBuilder() {
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder id(final UUID id) {
            this.id = id;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder ownerId(final UUID ownerId) {
            this.ownerId = ownerId;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder name(final String name) {
            this.name = name;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder gstin(final String gstin) {
            this.gstin = gstin;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder pan(final String pan) {
            this.pan = pan;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder cin(final String cin) {
            this.cin = cin;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder bankAccountNumber(final String bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder bankIfsc(final String bankIfsc) {
            this.bankIfsc = bankIfsc;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder logoUrl(final String logoUrl) {
            this.logoUrl = logoUrl;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder legalEntityName(final String legalEntityName) {
            this.legalEntityName = legalEntityName;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder kycStatus(final VerificationStatus kycStatus) {
            this.kycStatus = kycStatus;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder bankBeneficiaryName(final String bankBeneficiaryName) {
            this.bankBeneficiaryName = bankBeneficiaryName;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder pennyDropStatus(final VerificationStatus pennyDropStatus) {
            this.pennyDropStatus = pennyDropStatus;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder isGstinVerified(final Boolean isGstinVerified) {
            this.isGstinVerified = isGstinVerified;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder isBankVerified(final Boolean isBankVerified) {
            this.isBankVerified = isBankVerified;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder createdAt(final LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder updatedAt(final LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        /**
         * @return {@code this}.
         */
        @java.lang.SuppressWarnings("all")
        public Brand.BrandBuilder version(final Integer version) {
            this.version = version;
            return this;
        }

        @java.lang.SuppressWarnings("all")
        public Brand build() {
            return new Brand(this.id, this.ownerId, this.name, this.gstin, this.pan, this.cin, this.bankAccountNumber, this.bankIfsc, this.logoUrl, this.legalEntityName, this.kycStatus, this.bankBeneficiaryName, this.pennyDropStatus, this.isGstinVerified, this.isBankVerified, this.createdAt, this.updatedAt, this.version);
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "Brand.BrandBuilder(id=" + this.id + ", ownerId=" + this.ownerId + ", name=" + this.name + ", gstin=" + this.gstin + ", pan=" + this.pan + ", cin=" + this.cin + ", bankAccountNumber=" + this.bankAccountNumber + ", bankIfsc=" + this.bankIfsc + ", logoUrl=" + this.logoUrl + ", legalEntityName=" + this.legalEntityName + ", kycStatus=" + this.kycStatus + ", bankBeneficiaryName=" + this.bankBeneficiaryName + ", pennyDropStatus=" + this.pennyDropStatus + ", isGstinVerified=" + this.isGstinVerified + ", isBankVerified=" + this.isBankVerified + ", createdAt=" + this.createdAt + ", updatedAt=" + this.updatedAt + ", version=" + this.version + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public static Brand.BrandBuilder builder() {
        return new Brand.BrandBuilder();
    }

    @java.lang.SuppressWarnings("all")
    public UUID getId() {
        return this.id;
    }

    @java.lang.SuppressWarnings("all")
    public UUID getOwnerId() {
        return this.ownerId;
    }

    @java.lang.SuppressWarnings("all")
    public String getName() {
        return this.name;
    }

    @java.lang.SuppressWarnings("all")
    public String getGstin() {
        return this.gstin;
    }

    @java.lang.SuppressWarnings("all")
    public String getPan() {
        return this.pan;
    }

    @java.lang.SuppressWarnings("all")
    public String getCin() {
        return this.cin;
    }

    @java.lang.SuppressWarnings("all")
    public String getBankAccountNumber() {
        return this.bankAccountNumber;
    }

    @java.lang.SuppressWarnings("all")
    public String getBankIfsc() {
        return this.bankIfsc;
    }

    @java.lang.SuppressWarnings("all")
    public String getLogoUrl() {
        return this.logoUrl;
    }

    @java.lang.SuppressWarnings("all")
    public String getLegalEntityName() {
        return this.legalEntityName;
    }

    @java.lang.SuppressWarnings("all")
    public VerificationStatus getKycStatus() {
        return this.kycStatus;
    }

    @java.lang.SuppressWarnings("all")
    public String getBankBeneficiaryName() {
        return this.bankBeneficiaryName;
    }

    @java.lang.SuppressWarnings("all")
    public VerificationStatus getPennyDropStatus() {
        return this.pennyDropStatus;
    }

    @java.lang.SuppressWarnings("all")
    public Boolean getIsGstinVerified() {
        return this.isGstinVerified;
    }

    @java.lang.SuppressWarnings("all")
    public Boolean getIsBankVerified() {
        return this.isBankVerified;
    }

    @java.lang.SuppressWarnings("all")
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }

    @java.lang.SuppressWarnings("all")
    public Integer getVersion() {
        return this.version;
    }

    @java.lang.SuppressWarnings("all")
    public void setId(final UUID id) {
        this.id = id;
    }

    @java.lang.SuppressWarnings("all")
    public void setOwnerId(final UUID ownerId) {
        this.ownerId = ownerId;
    }

    @java.lang.SuppressWarnings("all")
    public void setName(final String name) {
        this.name = name;
    }

    @java.lang.SuppressWarnings("all")
    public void setGstin(final String gstin) {
        this.gstin = gstin;
    }

    @java.lang.SuppressWarnings("all")
    public void setPan(final String pan) {
        this.pan = pan;
    }

    @java.lang.SuppressWarnings("all")
    public void setCin(final String cin) {
        this.cin = cin;
    }

    @java.lang.SuppressWarnings("all")
    public void setBankAccountNumber(final String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    @java.lang.SuppressWarnings("all")
    public void setBankIfsc(final String bankIfsc) {
        this.bankIfsc = bankIfsc;
    }

    @java.lang.SuppressWarnings("all")
    public void setLogoUrl(final String logoUrl) {
        this.logoUrl = logoUrl;
    }

    @java.lang.SuppressWarnings("all")
    public void setLegalEntityName(final String legalEntityName) {
        this.legalEntityName = legalEntityName;
    }

    @java.lang.SuppressWarnings("all")
    public void setKycStatus(final VerificationStatus kycStatus) {
        this.kycStatus = kycStatus;
    }

    @java.lang.SuppressWarnings("all")
    public void setBankBeneficiaryName(final String bankBeneficiaryName) {
        this.bankBeneficiaryName = bankBeneficiaryName;
    }

    @java.lang.SuppressWarnings("all")
    public void setPennyDropStatus(final VerificationStatus pennyDropStatus) {
        this.pennyDropStatus = pennyDropStatus;
    }

    @java.lang.SuppressWarnings("all")
    public void setIsGstinVerified(final Boolean isGstinVerified) {
        this.isGstinVerified = isGstinVerified;
    }

    @java.lang.SuppressWarnings("all")
    public void setIsBankVerified(final Boolean isBankVerified) {
        this.isBankVerified = isBankVerified;
    }

    @java.lang.SuppressWarnings("all")
    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setUpdatedAt(final LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @java.lang.SuppressWarnings("all")
    public void setVersion(final Integer version) {
        this.version = version;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public boolean equals(final java.lang.Object o) {
        if (o == this) return true;
        if (!(o instanceof Brand)) return false;
        final Brand other = (Brand) o;
        if (!other.canEqual((java.lang.Object) this)) return false;
        final java.lang.Object this$isGstinVerified = this.getIsGstinVerified();
        final java.lang.Object other$isGstinVerified = other.getIsGstinVerified();
        if (this$isGstinVerified == null ? other$isGstinVerified != null : !this$isGstinVerified.equals(other$isGstinVerified)) return false;
        final java.lang.Object this$isBankVerified = this.getIsBankVerified();
        final java.lang.Object other$isBankVerified = other.getIsBankVerified();
        if (this$isBankVerified == null ? other$isBankVerified != null : !this$isBankVerified.equals(other$isBankVerified)) return false;
        final java.lang.Object this$version = this.getVersion();
        final java.lang.Object other$version = other.getVersion();
        if (this$version == null ? other$version != null : !this$version.equals(other$version)) return false;
        final java.lang.Object this$id = this.getId();
        final java.lang.Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final java.lang.Object this$ownerId = this.getOwnerId();
        final java.lang.Object other$ownerId = other.getOwnerId();
        if (this$ownerId == null ? other$ownerId != null : !this$ownerId.equals(other$ownerId)) return false;
        final java.lang.Object this$name = this.getName();
        final java.lang.Object other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
        final java.lang.Object this$gstin = this.getGstin();
        final java.lang.Object other$gstin = other.getGstin();
        if (this$gstin == null ? other$gstin != null : !this$gstin.equals(other$gstin)) return false;
        final java.lang.Object this$pan = this.getPan();
        final java.lang.Object other$pan = other.getPan();
        if (this$pan == null ? other$pan != null : !this$pan.equals(other$pan)) return false;
        final java.lang.Object this$cin = this.getCin();
        final java.lang.Object other$cin = other.getCin();
        if (this$cin == null ? other$cin != null : !this$cin.equals(other$cin)) return false;
        final java.lang.Object this$bankAccountNumber = this.getBankAccountNumber();
        final java.lang.Object other$bankAccountNumber = other.getBankAccountNumber();
        if (this$bankAccountNumber == null ? other$bankAccountNumber != null : !this$bankAccountNumber.equals(other$bankAccountNumber)) return false;
        final java.lang.Object this$bankIfsc = this.getBankIfsc();
        final java.lang.Object other$bankIfsc = other.getBankIfsc();
        if (this$bankIfsc == null ? other$bankIfsc != null : !this$bankIfsc.equals(other$bankIfsc)) return false;
        final java.lang.Object this$logoUrl = this.getLogoUrl();
        final java.lang.Object other$logoUrl = other.getLogoUrl();
        if (this$logoUrl == null ? other$logoUrl != null : !this$logoUrl.equals(other$logoUrl)) return false;
        final java.lang.Object this$legalEntityName = this.getLegalEntityName();
        final java.lang.Object other$legalEntityName = other.getLegalEntityName();
        if (this$legalEntityName == null ? other$legalEntityName != null : !this$legalEntityName.equals(other$legalEntityName)) return false;
        final java.lang.Object this$kycStatus = this.getKycStatus();
        final java.lang.Object other$kycStatus = other.getKycStatus();
        if (this$kycStatus == null ? other$kycStatus != null : !this$kycStatus.equals(other$kycStatus)) return false;
        final java.lang.Object this$bankBeneficiaryName = this.getBankBeneficiaryName();
        final java.lang.Object other$bankBeneficiaryName = other.getBankBeneficiaryName();
        if (this$bankBeneficiaryName == null ? other$bankBeneficiaryName != null : !this$bankBeneficiaryName.equals(other$bankBeneficiaryName)) return false;
        final java.lang.Object this$pennyDropStatus = this.getPennyDropStatus();
        final java.lang.Object other$pennyDropStatus = other.getPennyDropStatus();
        if (this$pennyDropStatus == null ? other$pennyDropStatus != null : !this$pennyDropStatus.equals(other$pennyDropStatus)) return false;
        final java.lang.Object this$createdAt = this.getCreatedAt();
        final java.lang.Object other$createdAt = other.getCreatedAt();
        if (this$createdAt == null ? other$createdAt != null : !this$createdAt.equals(other$createdAt)) return false;
        final java.lang.Object this$updatedAt = this.getUpdatedAt();
        final java.lang.Object other$updatedAt = other.getUpdatedAt();
        if (this$updatedAt == null ? other$updatedAt != null : !this$updatedAt.equals(other$updatedAt)) return false;
        return true;
    }

    @java.lang.SuppressWarnings("all")
    protected boolean canEqual(final java.lang.Object other) {
        return other instanceof Brand;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final java.lang.Object $isGstinVerified = this.getIsGstinVerified();
        result = result * PRIME + ($isGstinVerified == null ? 43 : $isGstinVerified.hashCode());
        final java.lang.Object $isBankVerified = this.getIsBankVerified();
        result = result * PRIME + ($isBankVerified == null ? 43 : $isBankVerified.hashCode());
        final java.lang.Object $version = this.getVersion();
        result = result * PRIME + ($version == null ? 43 : $version.hashCode());
        final java.lang.Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final java.lang.Object $ownerId = this.getOwnerId();
        result = result * PRIME + ($ownerId == null ? 43 : $ownerId.hashCode());
        final java.lang.Object $name = this.getName();
        result = result * PRIME + ($name == null ? 43 : $name.hashCode());
        final java.lang.Object $gstin = this.getGstin();
        result = result * PRIME + ($gstin == null ? 43 : $gstin.hashCode());
        final java.lang.Object $pan = this.getPan();
        result = result * PRIME + ($pan == null ? 43 : $pan.hashCode());
        final java.lang.Object $cin = this.getCin();
        result = result * PRIME + ($cin == null ? 43 : $cin.hashCode());
        final java.lang.Object $bankAccountNumber = this.getBankAccountNumber();
        result = result * PRIME + ($bankAccountNumber == null ? 43 : $bankAccountNumber.hashCode());
        final java.lang.Object $bankIfsc = this.getBankIfsc();
        result = result * PRIME + ($bankIfsc == null ? 43 : $bankIfsc.hashCode());
        final java.lang.Object $logoUrl = this.getLogoUrl();
        result = result * PRIME + ($logoUrl == null ? 43 : $logoUrl.hashCode());
        final java.lang.Object $legalEntityName = this.getLegalEntityName();
        result = result * PRIME + ($legalEntityName == null ? 43 : $legalEntityName.hashCode());
        final java.lang.Object $kycStatus = this.getKycStatus();
        result = result * PRIME + ($kycStatus == null ? 43 : $kycStatus.hashCode());
        final java.lang.Object $bankBeneficiaryName = this.getBankBeneficiaryName();
        result = result * PRIME + ($bankBeneficiaryName == null ? 43 : $bankBeneficiaryName.hashCode());
        final java.lang.Object $pennyDropStatus = this.getPennyDropStatus();
        result = result * PRIME + ($pennyDropStatus == null ? 43 : $pennyDropStatus.hashCode());
        final java.lang.Object $createdAt = this.getCreatedAt();
        result = result * PRIME + ($createdAt == null ? 43 : $createdAt.hashCode());
        final java.lang.Object $updatedAt = this.getUpdatedAt();
        result = result * PRIME + ($updatedAt == null ? 43 : $updatedAt.hashCode());
        return result;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("all")
    public java.lang.String toString() {
        return "Brand(id=" + this.getId() + ", ownerId=" + this.getOwnerId() + ", name=" + this.getName() + ", gstin=" + this.getGstin() + ", pan=" + this.getPan() + ", cin=" + this.getCin() + ", bankAccountNumber=" + this.getBankAccountNumber() + ", bankIfsc=" + this.getBankIfsc() + ", logoUrl=" + this.getLogoUrl() + ", legalEntityName=" + this.getLegalEntityName() + ", kycStatus=" + this.getKycStatus() + ", bankBeneficiaryName=" + this.getBankBeneficiaryName() + ", pennyDropStatus=" + this.getPennyDropStatus() + ", isGstinVerified=" + this.getIsGstinVerified() + ", isBankVerified=" + this.getIsBankVerified() + ", createdAt=" + this.getCreatedAt() + ", updatedAt=" + this.getUpdatedAt() + ", version=" + this.getVersion() + ")";
    }

    @java.lang.SuppressWarnings("all")
    public Brand() {
    }

    @java.lang.SuppressWarnings("all")
    public Brand(final UUID id, final UUID ownerId, final String name, final String gstin, final String pan, final String cin, final String bankAccountNumber, final String bankIfsc, final String logoUrl, final String legalEntityName, final VerificationStatus kycStatus, final String bankBeneficiaryName, final VerificationStatus pennyDropStatus, final Boolean isGstinVerified, final Boolean isBankVerified, final LocalDateTime createdAt, final LocalDateTime updatedAt, final Integer version) {
        this.id = id;
        this.ownerId = ownerId;
        this.name = name;
        this.gstin = gstin;
        this.pan = pan;
        this.cin = cin;
        this.bankAccountNumber = bankAccountNumber;
        this.bankIfsc = bankIfsc;
        this.logoUrl = logoUrl;
        this.legalEntityName = legalEntityName;
        this.kycStatus = kycStatus;
        this.bankBeneficiaryName = bankBeneficiaryName;
        this.pennyDropStatus = pennyDropStatus;
        this.isGstinVerified = isGstinVerified;
        this.isBankVerified = isBankVerified;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.version = version;
    }
}
