package com.fooddelivery.restaurant.dto;

import java.time.LocalTime;
import io.swagger.v3.oas.annotations.media.Schema;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class TimingDTO {
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(type = "string", example = "10:00:00")
    private LocalTime openingTime;
    @com.fasterxml.jackson.annotation.JsonFormat(shape = com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    @Schema(type = "string", example = "22:00:00")
    private LocalTime closingTime;
}
