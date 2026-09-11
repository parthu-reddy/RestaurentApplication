package com.fooddelivery.restaurant.dto;

import java.util.UUID;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@lombok.Data
@lombok.Builder
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    private UUID brandId;
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;
    private java.util.List<CategoryTimingDTO> timings;


    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    public static class CategoryTimingDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        @io.swagger.v3.oas.annotations.media.Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
        private java.time.LocalTime openingTime;
        @io.swagger.v3.oas.annotations.media.Schema(requiredMode = io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED)
        private java.time.LocalTime closingTime;

        public CategoryTimingDTO(final java.time.LocalTime openingTime, final java.time.LocalTime closingTime) {
            this.openingTime = openingTime;
            this.closingTime = closingTime;
        }
    }
}
