package com.fooddelivery.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetOutletCategoryTimingRequest {
    private UUID categoryId;
    private List<TimingDTO> timings;
}
