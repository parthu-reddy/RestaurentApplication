package com.fooddelivery.restaurant.dto;

import jakarta.validation.constraints.NotNull;
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
public class SetBrandCategoryTimingRequest {
    
    @NotNull(message = "Category ID is required")
    private UUID categoryId;
    
    private List<TimingDTO> timings;
}
