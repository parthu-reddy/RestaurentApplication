package com.fooddelivery.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.UUID;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CampaignDto {
    private UUID id;
    private UUID advertiserId;
    private String name;
    private Double budget;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
