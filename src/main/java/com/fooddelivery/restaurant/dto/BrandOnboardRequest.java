package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public class BrandOnboardRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String gstin;
        @NotBlank
        private String pan;
        private String cin;
        @NotBlank
        private String bankAccountNumber;
        @NotBlank
        private String ifscCode;
        private String logoUrl;

public BrandOnboardRequest() {
        }

public String getName() {
            return this.name;
        }

public String getGstin() {
            return this.gstin;
        }

public String getPan() {
            return this.pan;
        }

public String getCin() {
            return this.cin;
        }

public String getBankAccountNumber() {
            return this.bankAccountNumber;
        }

public String getIfscCode() {
            return this.ifscCode;
        }

public String getLogoUrl() {
            return this.logoUrl;
        }

public void setName(final String name) {
            this.name = name;
        }

public void setGstin(final String gstin) {
            this.gstin = gstin;
        }

public void setPan(final String pan) {
            this.pan = pan;
        }

public void setCin(final String cin) {
            this.cin = cin;
        }

public void setBankAccountNumber(final String bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
        }

public void setIfscCode(final String ifscCode) {
            this.ifscCode = ifscCode;
        }

public void setLogoUrl(final String logoUrl) {
            this.logoUrl = logoUrl;
        }

        @java.lang.Override
public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof BrandOnboardRequest)) return false;
            final BrandOnboardRequest other = (BrandOnboardRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
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
            final java.lang.Object this$ifscCode = this.getIfscCode();
            final java.lang.Object other$ifscCode = other.getIfscCode();
            if (this$ifscCode == null ? other$ifscCode != null : !this$ifscCode.equals(other$ifscCode)) return false;
            final java.lang.Object this$logoUrl = this.getLogoUrl();
            final java.lang.Object other$logoUrl = other.getLogoUrl();
            if (this$logoUrl == null ? other$logoUrl != null : !this$logoUrl.equals(other$logoUrl)) return false;
            return true;
        }

protected boolean canEqual(final java.lang.Object other) {
            return other instanceof BrandOnboardRequest;
        }

        @java.lang.Override
public int hashCode() {
            final int PRIME = 59;
            int result = 1;
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
            final java.lang.Object $ifscCode = this.getIfscCode();
            result = result * PRIME + ($ifscCode == null ? 43 : $ifscCode.hashCode());
            final java.lang.Object $logoUrl = this.getLogoUrl();
            result = result * PRIME + ($logoUrl == null ? 43 : $logoUrl.hashCode());
            return result;
        }

        @java.lang.Override
public java.lang.String toString() {
            return "BrandOnboardRequest(name=" + this.getName() + ", gstin=" + this.getGstin() + ", pan=" + this.getPan() + ", cin=" + this.getCin() + ", bankAccountNumber=" + this.getBankAccountNumber() + ", ifscCode=" + this.getIfscCode() + ", logoUrl=" + this.getLogoUrl() + ")";
        }
    }
