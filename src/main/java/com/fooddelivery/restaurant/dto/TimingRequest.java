package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@lombok.Data
public class TimingRequest {
        @NotNull
        private LocalTime openingTime;
        @NotNull
        private LocalTime closingTime;








    }
