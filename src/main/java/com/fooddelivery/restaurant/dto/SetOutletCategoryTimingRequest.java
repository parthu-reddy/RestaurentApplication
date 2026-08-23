package com.fooddelivery.restaurant.dto;

import java.util.List;
import java.util.UUID;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class SetOutletCategoryTimingRequest {
    private UUID categoryId;
    private List<TimingDTO> timings;












}
