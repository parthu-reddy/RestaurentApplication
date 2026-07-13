package com.fooddelivery.restaurant.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private UUID id;
    
    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;
    
    @Size(max = 255, message = "Description must not exceed 255 characters")
    private String description;

    private java.util.List<CategoryTimingDTO> timings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryTimingDTO implements Serializable {
        private static final long serialVersionUID = 1L;
        private java.time.LocalTime openingTime;
        private java.time.LocalTime closingTime;
    }
}
