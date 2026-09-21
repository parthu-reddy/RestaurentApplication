package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.dto.MenuItemDTO;
import com.fooddelivery.restaurant.entity.MasterMenuItem;
import com.fooddelivery.restaurant.entity.OutletMenuOverride;
import com.fooddelivery.restaurant.dto.MasterMenuItemDto;
import com.fooddelivery.restaurant.dto.OverrideItemDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.UUID;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.validation.Valid;

@RestController
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class CatalogController {
    private MasterMenuItemDto toMasterMenuItemDto(MasterMenuItem item) {
        return MasterMenuItemDto.builder().id(item.getId()).brandId(item.getBrandId()).categoryId(item.getCategoryId()).name(item.getName()).description(item.getDescription()).imageUrl(item.getImageUrl()).isVeg(item.getIsVeg()).basePrice(item.getBasePrice()).packingCharge(item.getPackingCharge()).defaultPrepTimeMinutes(item.getDefaultPrepTimeMinutes()).version(item.getVersion()).build();
    }
    
    private OverrideItemDto toOverrideItemDto(OutletMenuOverride item) {
        return OverrideItemDto.builder().id(item.getId()).outletId(item.getOutletId()).masterMenuItemId(item.getMasterMenuItemId()).overriddenPrice(item.getOverriddenPrice()).isAvailable(item.getIsAvailable()).overriddenPrepTimeMinutes(item.getOverriddenPrepTimeMinutes()).version(item.getVersion()).build();
    }
private final com.fooddelivery.restaurant.service.CatalogService catalogService;
    private final com.fooddelivery.restaurant.security.RestaurantSecurityHelper securityHelper;

    // Phase 3: Brand uploads Master Menu
    @PostMapping("/api/v1/brands/{brandId}/master-menu")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isBrandOwner(#brandId, authentication.name)")
    public ResponseEntity<ApiResponse<MasterMenuItemDto>> addMasterMenuItem(@PathVariable UUID brandId, @Valid @RequestBody MasterMenuItem item) {
        MasterMenuItem savedItem = catalogService.addMasterMenuItem(brandId, item);
        return ResponseEntity.ok(ApiResponse.success(toMasterMenuItemDto(savedItem), "Master Menu item added successfully"));
    }

    @GetMapping("/api/v1/brands/{brandId}/master-menu")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isBrandOwner(#brandId, authentication.name)")
    public ResponseEntity<ApiResponse<List<MasterMenuItemDto>>> getMasterMenuItems(@PathVariable UUID brandId) {
        List<MasterMenuItem> items = catalogService.getMasterMenuItems(brandId);
        List<MasterMenuItemDto> dtos = items.stream().map(this::toMasterMenuItemDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos, "Master Menu retrieved"));
    }

    @PutMapping("/api/v1/brands/{brandId}/master-menu/{itemId}")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isBrandOwner(#brandId, authentication.name)")
    public ResponseEntity<ApiResponse<MasterMenuItemDto>> editMasterMenuItem(@PathVariable UUID brandId, @PathVariable UUID itemId, @Valid @RequestBody MasterMenuItem item) {
        MasterMenuItem updated = catalogService.editMasterMenuItem(brandId, itemId, item);
        return ResponseEntity.ok(ApiResponse.success(toMasterMenuItemDto(updated), "Master Menu item updated successfully"));
    }

    // Phase 3: Outlet overrides Price or Availability
    @PostMapping("/api/v1/outlets/{outletId}/menu-overrides/{masterMenuItemId}")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isOutletOwner(#outletId, authentication.name)")
    public ResponseEntity<ApiResponse<OverrideItemDto>> overrideMenuItem(@PathVariable UUID outletId, @PathVariable UUID masterMenuItemId, @Valid @RequestBody OutletMenuOverride override) {
        OutletMenuOverride saved = catalogService.addOrUpdateOverride(outletId, masterMenuItemId, override);
        return ResponseEntity.ok(ApiResponse.success(toOverrideItemDto(saved), "Menu override saved"));
    }

    @GetMapping("/api/v1/outlets/{outletId}/menu-overrides")
    @PreAuthorize("hasRole('RESTAURANT') and @restaurantSecurityHelper.isOutletOwner(#outletId, authentication.name)")
    public ResponseEntity<ApiResponse<List<OverrideItemDto>>> getOverrides(@PathVariable UUID outletId) {
        List<OverrideItemDto> dtos = catalogService.getOverrides(outletId).stream().map(this::toOverrideItemDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(dtos, "Menu overrides retrieved"));
    }

    // Customer fetching the effective menu for an Outlet
    @GetMapping("/api/v1/restaurants/{restaurantId}/catalog/items")
    @PreAuthorize("permitAll()")
    public ResponseEntity<ApiResponse<List<MenuItemDTO>>> getEffectiveMenu(@PathVariable UUID restaurantId) {
        List<MenuItemDTO> items = catalogService.getEffectiveMenuForOutlet(restaurantId);
        return ResponseEntity.ok(ApiResponse.success(items, "Menu items retrieved"));
    }

    // Batch endpoint used by CustomerOrderService to validate and fetch prices
    @GetMapping("/api/v1/restaurants/{restaurantId}/menu/batch")
    @PreAuthorize("permitAll()")
    public ResponseEntity<List<MenuItemDTO>> getEffectiveMenuBatch(@PathVariable UUID restaurantId, @RequestParam("ids") String idsStr) {
        if (idsStr == null || idsStr.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        List<UUID> ids = Arrays.stream(idsStr.split(",")).map(String::trim).map(UUID::fromString).collect(Collectors.toList());
        List<MenuItemDTO> items = catalogService.getEffectiveMenuBatch(restaurantId, ids);
        return ResponseEntity.ok(items);
    }

}
