package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.dto.CategoryDTO;
import com.fooddelivery.restaurant.service.CategoryService;
import com.fooddelivery.restaurant.service.OutletCategoryTimingService;
import com.fooddelivery.restaurant.dto.SetOutletCategoryTimingRequest;
import com.fooddelivery.restaurant.dto.TimingDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;
    private final OutletCategoryTimingService outletCategoryTimingService;

    @GetMapping("/api/v1/categories")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategories() {
        List<CategoryDTO> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved"));
    }

    @PostMapping("/api/v1/categories")
    @PreAuthorize("hasRole('RESTAURANT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO created = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(ApiResponse.success(created, "Category created successfully"));
    }

    @GetMapping("/api/v1/outlets/{outletId}/categories/{categoryId}/timings")
    public ResponseEntity<ApiResponse<List<TimingDTO>>> getOutletCategoryTimings(
            @PathVariable UUID outletId, @PathVariable UUID categoryId) {
        List<TimingDTO> timings = outletCategoryTimingService.getTimings(outletId, categoryId);
        return ResponseEntity.ok(ApiResponse.success(timings, "Outlet category timings retrieved"));
    }

    @PostMapping("/api/v1/outlets/{outletId}/categories/timings")
    @PreAuthorize("hasRole('RESTAURANT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<TimingDTO>>> setOutletCategoryTimings(
            @PathVariable UUID outletId, @Valid @RequestBody SetOutletCategoryTimingRequest request) {
        List<TimingDTO> timings = outletCategoryTimingService.setTimings(outletId, request);
        return ResponseEntity.ok(ApiResponse.success(timings, "Outlet category timings set successfully"));
    }
}
