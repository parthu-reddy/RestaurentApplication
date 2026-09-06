package com.fooddelivery.restaurant.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class OutletTimingDto {
    private UUID id;
    private Integer dayOfWeek;
    private LocalTime openingTime;
    private LocalTime closingTime;
}
