package com.fooddelivery.restaurant.dto;

import com.fooddelivery.common.enums.VerificationStatus;
import com.fooddelivery.restaurant.entity.Brand;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.UUID;

/** Display metadata available to organisation members, without application or banking identifiers. */
public record BrandSummaryDto(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID id,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) UUID organisationId,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name,
        String logoUrl,
        VerificationStatus kycStatus,
        VerificationStatus pennyDropStatus,
        Boolean isGstinVerified,
        Boolean isBankVerified,
        Instant createdAt,
        Instant updatedAt,
        Integer version) {
    public static BrandSummaryDto from(Brand brand) {
        return new BrandSummaryDto(brand.getId(), brand.getOrganisationId(), brand.getName(),
                brand.getLogoUrl(), brand.getKycStatus(), brand.getPennyDropStatus(),
                brand.getIsGstinVerified(), brand.getIsBankVerified(), brand.getCreatedAt(),
                brand.getUpdatedAt(), brand.getVersion());
    }
}
