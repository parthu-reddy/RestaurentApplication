package com.fooddelivery.restaurant.dto;

import java.util.List;
import java.util.UUID;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class SetOutletCategoryTimingRequest {
    @jakarta.validation.constraints.NotNull
    private UUID categoryId;
    @jakarta.validation.constraints.NotNull
    private List<TimingDTO> timings;
}
