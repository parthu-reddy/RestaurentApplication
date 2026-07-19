package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.dto.MenuItemDTO;
import com.fooddelivery.restaurant.entity.MasterMenuItem;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.entity.OutletMenuOverride;
import com.fooddelivery.restaurant.repository.MasterMenuItemRepository;
import com.fooddelivery.restaurant.repository.OutletMenuOverrideRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import com.fooddelivery.restaurant.repository.CategoryRepository;
import com.fooddelivery.restaurant.repository.OutletCategoryTimingRepository;
import com.fooddelivery.restaurant.repository.BrandCategoryTimingRepository;
import com.fooddelivery.restaurant.entity.Category;
import com.fooddelivery.restaurant.entity.OutletCategoryTiming;
import com.fooddelivery.restaurant.entity.BrandCategoryTiming;
import java.time.LocalTime;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogService {

    private final MasterMenuItemRepository masterMenuItemRepository;
    private final OutletMenuOverrideRepository outletMenuOverrideRepository;
    private final OutletRepository outletRepository;
    private final CategoryRepository categoryRepository;
    private final OutletCategoryTimingRepository outletCategoryTimingRepository;
    private final BrandCategoryTimingRepository brandCategoryTimingRepository;

    @Transactional
    public MasterMenuItem addMasterMenuItem(UUID brandId, MasterMenuItem item) {
        if (item.getCategoryId() != null) {
            Category cat = categoryRepository.findById(item.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid categoryId provided"));
        }
        item.setBrandId(brandId);
        if (item.getId() == null) {
            item.setId(UUID.randomUUID());
        }
        if (item.getDefaultPrepTimeMinutes() == null) {
            item.setDefaultPrepTimeMinutes(15);
        }
        return masterMenuItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<MasterMenuItem> getMasterMenuItems(UUID brandId) {
        return masterMenuItemRepository.findByBrandId(brandId);
    }

    @Transactional
    public MasterMenuItem editMasterMenuItem(UUID brandId, UUID itemId, MasterMenuItem updatedItem) {
        MasterMenuItem existingItem = masterMenuItemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Menu item not found"));
            
        if (!existingItem.getBrandId().equals(brandId)) {
            throw new IllegalArgumentException("Menu item does not belong to this brand");
        }
        
        if (updatedItem.getCategoryId() != null) {
            Category cat = categoryRepository.findById(updatedItem.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid categoryId provided"));
            existingItem.setCategoryId(updatedItem.getCategoryId());
        } else {
            existingItem.setCategoryId(null);
        }
        
        if (updatedItem.getName() != null) existingItem.setName(updatedItem.getName());
        if (updatedItem.getDescription() != null) existingItem.setDescription(updatedItem.getDescription());
        if (updatedItem.getImageUrl() != null) existingItem.setImageUrl(updatedItem.getImageUrl());
        if (updatedItem.getBasePrice() != null) existingItem.setBasePrice(updatedItem.getBasePrice());
        if (updatedItem.getDefaultPrepTimeMinutes() != null) existingItem.setDefaultPrepTimeMinutes(updatedItem.getDefaultPrepTimeMinutes());
        
        return masterMenuItemRepository.save(existingItem);
    }

    @Transactional
    public OutletMenuOverride addOrUpdateOverride(UUID outletId, UUID masterMenuItemId, OutletMenuOverride override) {
        Optional<OutletMenuOverride> existing = outletMenuOverrideRepository.findByOutletIdAndMasterMenuItemId(outletId, masterMenuItemId);
        OutletMenuOverride target = existing.orElse(new OutletMenuOverride());
        if (target.getId() == null) {
            target.setId(UUID.randomUUID());
        }
        
        target.setOutletId(outletId);
        target.setMasterMenuItemId(masterMenuItemId);
        
        target.setOverriddenPrice(override.getOverriddenPrice());
        
        if (override.getIsAvailable() != null) target.setIsAvailable(override.getIsAvailable());
        if (override.getOverriddenPrepTimeMinutes() != null) target.setOverriddenPrepTimeMinutes(override.getOverriddenPrepTimeMinutes());
        
        return outletMenuOverrideRepository.save(target);
    }

    @Transactional(readOnly = true)
    public List<OutletMenuOverride> getOverrides(UUID outletId) {
        return outletMenuOverrideRepository.findByOutletId(outletId);
    }

    // Resolves the effective menu for an outlet (Master items + Overrides)
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getEffectiveMenuForOutlet(UUID outletId) {
        Outlet outlet = outletRepository.findById(outletId)
                .orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
                
        List<MasterMenuItem> masterItems = masterMenuItemRepository.findByBrandId(outlet.getBrandId());
        List<OutletMenuOverride> overrides = outletMenuOverrideRepository.findByOutletId(outletId);
        List<OutletCategoryTiming> categoryTimings = outletCategoryTimingRepository.findByOutletId(outletId);
        List<BrandCategoryTiming> brandTimings = brandCategoryTimingRepository.findByBrandId(outlet.getBrandId());
        List<Category> allCategories = categoryRepository.findActiveCategoriesForBrand(outlet.getBrandId());
        
        Map<UUID, String> categoryNames = allCategories.stream()
            .collect(Collectors.toMap(Category::getId, Category::getName));
            
        Map<UUID, OutletMenuOverride> overrideMap = overrides.stream()
            .collect(Collectors.toMap(OutletMenuOverride::getMasterMenuItemId, o -> o, (o1, o2) -> o1));
            
        Map<UUID, List<OutletCategoryTiming>> timingsByCategory = categoryTimings.stream()
            .collect(Collectors.groupingBy(t -> t.getCategory().getId()));
            
        Map<UUID, List<BrandCategoryTiming>> brandTimingsByCategory = brandTimings.stream()
            .collect(Collectors.groupingBy(t -> t.getCategory().getId()));
            
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));
            
        return masterItems.stream().map(master -> {
            OutletMenuOverride override = overrideMap.get(master.getId());
            boolean isAvail = override != null && override.getIsAvailable() != null ? override.getIsAvailable() : true;
            
            // Evaluate category timings
            if (isAvail && master.getCategoryId() != null) {
                List<OutletCategoryTiming> timings = timingsByCategory.get(master.getCategoryId());
                
                boolean hasOutletTimings = timings != null && !timings.isEmpty();
                boolean hasBrandTimings = brandTimingsByCategory.containsKey(master.getCategoryId()) && !brandTimingsByCategory.get(master.getCategoryId()).isEmpty();
                
                if (hasOutletTimings || hasBrandTimings) {
                    boolean categoryOpen = false;
                    
                    if (hasOutletTimings) {
                        for (OutletCategoryTiming timing : timings) {
                            LocalTime start = timing.getOpeningTime();
                            LocalTime end = timing.getClosingTime();
                            if (start.isBefore(end) || start.equals(end)) {
                                if (!now.isBefore(start) && !now.isAfter(end)) {
                                    categoryOpen = true;
                                    break;
                                }
                            } else { // cross-midnight
                                if (!now.isBefore(start) || !now.isAfter(end)) {
                                    categoryOpen = true;
                                    break;
                                }
                            }
                        }
                    } else if (hasBrandTimings) {
                        for (BrandCategoryTiming timing : brandTimingsByCategory.get(master.getCategoryId())) {
                            LocalTime start = timing.getOpeningTime();
                            LocalTime end = timing.getClosingTime();
                            if (start.isBefore(end) || start.equals(end)) {
                                if (!now.isBefore(start) && !now.isAfter(end)) {
                                    categoryOpen = true;
                                    break;
                                }
                            } else { // cross-midnight
                                if (!now.isBefore(start) || !now.isAfter(end)) {
                                    categoryOpen = true;
                                    break;
                                }
                            }
                        }
                    }
                    
                    if (!categoryOpen) {
                        isAvail = false; // Override to false if category is currently closed
                    }
                }
            }
                
            return MenuItemDTO.builder()
                .id(master.getId())
                .restaurantId(outletId)
                .name(master.getName())
                .description(master.getDescription())
                .price(override != null && override.getOverriddenPrice() != null ? override.getOverriddenPrice() : master.getBasePrice())
                .isAvailable(isAvail)
                .prepTimeMinutes(override != null && override.getOverriddenPrepTimeMinutes() != null ? override.getOverriddenPrepTimeMinutes() : master.getDefaultPrepTimeMinutes())
                .imageUrl(master.getImageUrl())
                .categoryId(master.getCategoryId())
                .categoryName(master.getCategoryId() != null ? categoryNames.get(master.getCategoryId()) : null)
                .build();
        }).collect(Collectors.toList());
    }

    // Resolves specific items for batch queries
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getEffectiveMenuBatch(UUID outletId, List<UUID> itemIds) {
        Outlet outlet = outletRepository.findById(outletId)
                .orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
                
        List<MasterMenuItem> masterItems = masterMenuItemRepository.findByIdIn(itemIds);
        List<OutletMenuOverride> overrides = outletMenuOverrideRepository.findByOutletId(outletId);
        List<OutletCategoryTiming> categoryTimings = outletCategoryTimingRepository.findByOutletId(outletId);
        List<BrandCategoryTiming> brandTimings = brandCategoryTimingRepository.findByBrandId(outlet.getBrandId());
        List<Category> allCategories = categoryRepository.findActiveCategoriesForBrand(outlet.getBrandId());
        
        Map<UUID, String> categoryNames = allCategories.stream()
            .collect(Collectors.toMap(Category::getId, Category::getName));
        
        Map<UUID, OutletMenuOverride> overrideMap = overrides.stream()
            .collect(Collectors.toMap(OutletMenuOverride::getMasterMenuItemId, o -> o, (o1, o2) -> o1));
            
        Map<UUID, List<OutletCategoryTiming>> timingsByCategory = categoryTimings.stream()
            .collect(Collectors.groupingBy(t -> t.getCategory().getId()));
            
        Map<UUID, List<BrandCategoryTiming>> brandTimingsByCategory = brandTimings.stream()
            .collect(Collectors.groupingBy(t -> t.getCategory().getId()));
            
        LocalTime now = LocalTime.now(ZoneId.of("Asia/Kolkata"));
            
        return masterItems.stream()
            .filter(master -> master.getBrandId().equals(outlet.getBrandId()))
            .map(master -> {
                OutletMenuOverride override = overrideMap.get(master.getId());
                boolean isAvail = override != null && override.getIsAvailable() != null ? override.getIsAvailable() : true;
                
                // Evaluate category timings
                if (isAvail && master.getCategoryId() != null) {
                    List<OutletCategoryTiming> timings = timingsByCategory.get(master.getCategoryId());
                    
                    boolean hasOutletTimings = timings != null && !timings.isEmpty();
                    boolean hasBrandTimings = brandTimingsByCategory.containsKey(master.getCategoryId()) && !brandTimingsByCategory.get(master.getCategoryId()).isEmpty();
                    
                    if (hasOutletTimings || hasBrandTimings) {
                        boolean categoryOpen = false;
                        
                        if (hasOutletTimings) {
                            for (OutletCategoryTiming timing : timings) {
                                LocalTime start = timing.getOpeningTime();
                                LocalTime end = timing.getClosingTime();
                                if (start.isBefore(end) || start.equals(end)) {
                                    if (!now.isBefore(start) && !now.isAfter(end)) {
                                        categoryOpen = true;
                                        break;
                                    }
                                } else { // cross-midnight
                                    if (!now.isBefore(start) || !now.isAfter(end)) {
                                        categoryOpen = true;
                                        break;
                                    }
                                }
                            }
                        } else if (hasBrandTimings) {
                            for (BrandCategoryTiming timing : brandTimingsByCategory.get(master.getCategoryId())) {
                                LocalTime start = timing.getOpeningTime();
                                LocalTime end = timing.getClosingTime();
                                if (start.isBefore(end) || start.equals(end)) {
                                    if (!now.isBefore(start) && !now.isAfter(end)) {
                                        categoryOpen = true;
                                        break;
                                    }
                                } else { // cross-midnight
                                    if (!now.isBefore(start) || !now.isAfter(end)) {
                                        categoryOpen = true;
                                        break;
                                    }
                                }
                            }
                        }
                        
                        if (!categoryOpen) {
                            isAvail = false; // Override to false if category is currently closed
                        }
                    }
                }
                    
                return MenuItemDTO.builder()
                    .id(master.getId())
                    .restaurantId(outletId)
                    .name(master.getName())
                    .description(master.getDescription())
                    .price(override != null && override.getOverriddenPrice() != null ? override.getOverriddenPrice() : master.getBasePrice())
                    .isAvailable(isAvail)
                    .prepTimeMinutes(override != null && override.getOverriddenPrepTimeMinutes() != null ? override.getOverriddenPrepTimeMinutes() : master.getDefaultPrepTimeMinutes())
                    .imageUrl(master.getImageUrl())
                    .categoryId(master.getCategoryId())
                    .categoryName(master.getCategoryId() != null ? categoryNames.get(master.getCategoryId()) : null)
                    .build();
        }).collect(Collectors.toList());
    }
}
