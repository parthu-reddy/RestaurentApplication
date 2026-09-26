package com.fooddelivery.restaurant.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

@Data
@Builder
public class OutletDto {
    private UUID id;
    private UUID brandId;
    private String name;
    private String fssaiLicenseNumber;
    private Double lat;
    private Double lng;
    private String bannerUrl;
    private Boolean isActive;
    private Integer defaultPrepTimeSeconds;
    private String cuisine;
    private Double rating;
    private Integer reviewsCount;
    private Integer deliveryTime;
    private BigDecimal deliveryFee;
    private String tags;
    private List<OutletTimingDto> timings;
    /** IANA zone the timings are in. */
    private String timeZone;
}
