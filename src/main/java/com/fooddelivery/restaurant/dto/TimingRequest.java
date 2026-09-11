package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@lombok.Data
public class TimingRequest {
        @NotNull
        @io.swagger.v3.oas.annotations.media.Schema(type = "string", example = "10:00:00")
        private LocalTime openingTime;
        @NotNull
        @io.swagger.v3.oas.annotations.media.Schema(type = "string", example = "22:00:00")
        private LocalTime closingTime;

    }
