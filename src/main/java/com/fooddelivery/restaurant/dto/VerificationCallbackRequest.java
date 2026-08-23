package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

public class VerificationCallbackRequest {
        private String verificationType;
        private String status;
        private String legalEntityName;
        private String bankBeneficiaryName;
        private Double matchScore;

public VerificationCallbackRequest() {
        }

public String getVerificationType() {
            return this.verificationType;
        }

public String getStatus() {
            return this.status;
        }

public String getLegalEntityName() {
            return this.legalEntityName;
        }

public String getBankBeneficiaryName() {
            return this.bankBeneficiaryName;
        }

public Double getMatchScore() {
            return this.matchScore;
        }

public void setVerificationType(final String verificationType) {
            this.verificationType = verificationType;
        }

public void setStatus(final String status) {
            this.status = status;
        }

public void setLegalEntityName(final String legalEntityName) {
            this.legalEntityName = legalEntityName;
        }

public void setBankBeneficiaryName(final String bankBeneficiaryName) {
            this.bankBeneficiaryName = bankBeneficiaryName;
        }

public void setMatchScore(final Double matchScore) {
            this.matchScore = matchScore;
        }

        @java.lang.Override
public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof VerificationCallbackRequest)) return false;
            final VerificationCallbackRequest other = (VerificationCallbackRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$matchScore = this.getMatchScore();
            final java.lang.Object other$matchScore = other.getMatchScore();
            if (this$matchScore == null ? other$matchScore != null : !this$matchScore.equals(other$matchScore)) return false;
            final java.lang.Object this$verificationType = this.getVerificationType();
            final java.lang.Object other$verificationType = other.getVerificationType();
            if (this$verificationType == null ? other$verificationType != null : !this$verificationType.equals(other$verificationType)) return false;
            final java.lang.Object this$status = this.getStatus();
            final java.lang.Object other$status = other.getStatus();
            if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
            final java.lang.Object this$legalEntityName = this.getLegalEntityName();
            final java.lang.Object other$legalEntityName = other.getLegalEntityName();
            if (this$legalEntityName == null ? other$legalEntityName != null : !this$legalEntityName.equals(other$legalEntityName)) return false;
            final java.lang.Object this$bankBeneficiaryName = this.getBankBeneficiaryName();
            final java.lang.Object other$bankBeneficiaryName = other.getBankBeneficiaryName();
            if (this$bankBeneficiaryName == null ? other$bankBeneficiaryName != null : !this$bankBeneficiaryName.equals(other$bankBeneficiaryName)) return false;
            return true;
        }

protected boolean canEqual(final java.lang.Object other) {
            return other instanceof VerificationCallbackRequest;
        }

        @java.lang.Override
public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $matchScore = this.getMatchScore();
            result = result * PRIME + ($matchScore == null ? 43 : $matchScore.hashCode());
            final java.lang.Object $verificationType = this.getVerificationType();
            result = result * PRIME + ($verificationType == null ? 43 : $verificationType.hashCode());
            final java.lang.Object $status = this.getStatus();
            result = result * PRIME + ($status == null ? 43 : $status.hashCode());
            final java.lang.Object $legalEntityName = this.getLegalEntityName();
            result = result * PRIME + ($legalEntityName == null ? 43 : $legalEntityName.hashCode());
            final java.lang.Object $bankBeneficiaryName = this.getBankBeneficiaryName();
            result = result * PRIME + ($bankBeneficiaryName == null ? 43 : $bankBeneficiaryName.hashCode());
            return result;
        }

        @java.lang.Override
public java.lang.String toString() {
            return "VerificationCallbackRequest(verificationType=" + this.getVerificationType() + ", status=" + this.getStatus() + ", legalEntityName=" + this.getLegalEntityName() + ", bankBeneficiaryName=" + this.getBankBeneficiaryName() + ", matchScore=" + this.getMatchScore() + ")";
        }
    }
