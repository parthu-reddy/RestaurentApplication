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
        /** IANA zone the timings are written in, e.g. Asia/Kolkata. Offsets such as +05:30 are refused: they have no DST rules. */
        @NotBlank
        @com.fooddelivery.common.time.IanaTimeZone
        private String timeZone;
        private String bannerUrl;
        private String cuisine;
        private Double rating;
        private Integer reviewsCount;
        private Integer deliveryTime;
        private java.math.BigDecimal deliveryFee;
        private String tags;

    }
