package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class SetBrandCategoryTimingRequest {
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
    private List<TimingDTO> timings;
}
