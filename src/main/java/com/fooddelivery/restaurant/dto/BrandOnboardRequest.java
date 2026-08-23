package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@lombok.Data
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


















    }
