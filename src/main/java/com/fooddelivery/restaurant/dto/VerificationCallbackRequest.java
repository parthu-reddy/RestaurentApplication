package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@lombok.Data
public class VerificationCallbackRequest {
        @jakarta.validation.constraints.NotNull
        private String verificationType;
        @jakarta.validation.constraints.NotNull
        private String status;
        private String legalEntityName;
        private String bankBeneficiaryName;
        private Double matchScore;

    }
