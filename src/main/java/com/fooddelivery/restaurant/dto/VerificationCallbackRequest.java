package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@lombok.Data
public class VerificationCallbackRequest {
        private String verificationType;
        private String status;
        private String legalEntityName;
        private String bankBeneficiaryName;
        private Double matchScore;














    }
