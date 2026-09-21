package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.dto.CategoryDTO;
import com.fooddelivery.restaurant.service.CategoryService;
import com.fooddelivery.restaurant.service.OutletCategoryTimingService;
import com.fooddelivery.restaurant.service.BrandCategoryTimingService;
import com.fooddelivery.restaurant.dto.SetOutletCategoryTimingRequest;
import com.fooddelivery.restaurant.dto.SetBrandCategoryTimingRequest;
import com.fooddelivery.restaurant.dto.TimingDTO;
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
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class CategoryController {
private final CategoryService categoryService;
    private final OutletCategoryTimingService outletCategoryTimingService;
    private final BrandCategoryTimingService brandCategoryTimingService;

    @GetMapping("/api/v1/categories")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getCategories() {
        List<CategoryDTO> categories = categoryService.getActiveCategories(null);
        return ResponseEntity.ok(ApiResponse.success(categories, "Categories retrieved"));
    }

    @PostMapping("/api/v1/categories")
    @PreAuthorize("hasRole(\'ADMIN\')")
    public ResponseEntity<ApiResponse<CategoryDTO>> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO created = categoryService.createCategory(categoryDTO, null);
        return ResponseEntity.ok(ApiResponse.success(created, "Category created successfully"));
    }

    @org.springframework.web.bind.annotation.PutMapping("/api/v1/categories/{categoryId}")
    @PreAuthorize("hasRole(\'ADMIN\')")
    public ResponseEntity<ApiResponse<CategoryDTO>> updateCategory(@PathVariable java.util.UUID categoryId, @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updated = categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.ok(ApiResponse.success(updated, "Category updated successfully"));
    }

    @GetMapping("/api/v1/brands/{brandId}/categories")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<CategoryDTO>>> getBrandCategories(@PathVariable UUID brandId) {
        List<CategoryDTO> categories = categoryService.getActiveCategories(brandId);
        return ResponseEntity.ok(ApiResponse.success(categories, "Brand categories retrieved"));
    }

    @PostMapping("/api/v1/brands/{brandId}/categories")
    @PreAuthorize("hasRole(\'ADMIN\') or (hasRole(\'RESTAURANT\') and @restaurantSecurityHelper.isBrandOwner(#brandId, authentication.name))")
    public ResponseEntity<ApiResponse<CategoryDTO>> createBrandCategory(@PathVariable UUID brandId, @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO created = categoryService.createCategory(categoryDTO, brandId);
        return ResponseEntity.ok(ApiResponse.success(created, "Brand category created successfully"));
    }

    @GetMapping("/api/v1/outlets/{outletId}/categories/{categoryId}/timings")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<TimingDTO>>> getOutletCategoryTimings(@PathVariable UUID outletId, @PathVariable UUID categoryId) {
        List<TimingDTO> timings = outletCategoryTimingService.getTimings(outletId, categoryId);
        return ResponseEntity.ok(ApiResponse.success(timings, "Outlet category timings retrieved"));
    }

    @PostMapping("/api/v1/outlets/{outletId}/categories/timings")
    @PreAuthorize("hasRole(\'ADMIN\') or (hasRole(\'RESTAURANT\') and @restaurantSecurityHelper.isOutletOwner(#outletId, authentication.name))")
    public ResponseEntity<ApiResponse<List<TimingDTO>>> setOutletCategoryTimings(@PathVariable UUID outletId, @Valid @RequestBody SetOutletCategoryTimingRequest request) {
        List<TimingDTO> timings = outletCategoryTimingService.setTimings(outletId, request);
        return ResponseEntity.ok(ApiResponse.success(timings, "Outlet category timings set successfully"));
    }

    @GetMapping("/api/v1/brands/{brandId}/categories/{categoryId}/timings")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<TimingDTO>>> getBrandCategoryTimings(@PathVariable UUID brandId, @PathVariable UUID categoryId) {
        List<TimingDTO> timings = brandCategoryTimingService.getTimings(brandId, categoryId);
        return ResponseEntity.ok(ApiResponse.success(timings, "Brand category timings retrieved"));
    }

    @PostMapping("/api/v1/brands/{brandId}/categories/timings")
    @PreAuthorize("hasRole(\'ADMIN\') or (hasRole(\'RESTAURANT\') and @restaurantSecurityHelper.isBrandOwner(#brandId, authentication.name))")
    public ResponseEntity<ApiResponse<List<TimingDTO>>> setBrandCategoryTimings(@PathVariable UUID brandId, @Valid @RequestBody SetBrandCategoryTimingRequest request) {
        List<TimingDTO> timings = brandCategoryTimingService.setTimings(brandId, request);
        return ResponseEntity.ok(ApiResponse.success(timings, "Brand category timings set successfully"));
    }

}
// @Getter
