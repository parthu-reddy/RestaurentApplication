package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;

@lombok.Data
public class OutletOnboardRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String fssaiLicenseNumber;
        @NotNull
        private Double lat;
        @NotNull
        private Double lng;
        @NotNull
        private List<TimingRequest> timings;
        private String bannerUrl;
        private String cuisine;
        private Double rating;
        private Integer reviewsCount;
        private Integer deliveryTime;
        private java.math.BigDecimal deliveryFee;
        private String tags;

    }
