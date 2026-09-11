package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@lombok.Data
public class OutletSettingsUpdateRequest {
        @NotNull
        private Integer defaultPrepTimeSeconds;

    }
