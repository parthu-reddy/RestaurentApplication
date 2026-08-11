package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.entity.MasterMenuItem;
import com.fooddelivery.restaurant.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/internal/admin/restaurants/{restaurantId}/catalog")
public class CatalogAdminController {

    private final CatalogService catalogService;

    public CatalogAdminController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostMapping("/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<MasterMenuItem>>> batchSyncCatalog(
            @PathVariable UUID restaurantId,
            @Valid @RequestBody List<MasterMenuItem> items) {
        
        List<MasterMenuItem> savedItems = items.stream().map(item -> {
            try {
                return catalogService.addMasterMenuItem(restaurantId, item);
            } catch (Exception e) {
                return catalogService.editMasterMenuItem(restaurantId, item.getId(), item);
            }
        }).toList();
        
        return ResponseEntity.ok(ApiResponse.success(savedItems, "Catalog batch synced successfully"));
    }
}
