package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.dto.MenuItemDTO;
import com.fooddelivery.restaurant.entity.MasterMenuItem;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.entity.OutletMenuOverride;
import com.fooddelivery.restaurant.repository.MasterMenuItemRepository;
import com.fooddelivery.restaurant.repository.OutletMenuOverrideRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
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

    @Transactional
    public MasterMenuItem addMasterMenuItem(UUID brandId, MasterMenuItem item) {
        item.setBrandId(brandId);
        if (item.getId() == null) {
            item.setId(UUID.randomUUID());
        }
        return masterMenuItemRepository.save(item);
    }

    @Transactional(readOnly = true)
    public List<MasterMenuItem> getMasterMenuItems(UUID brandId) {
        return masterMenuItemRepository.findByBrandId(brandId);
    }

    @Transactional
    public OutletMenuOverride addOrUpdateOverride(UUID outletId, UUID masterMenuItemId, OutletMenuOverride override) {
        Optional<OutletMenuOverride> existing = outletMenuOverrideRepository.findByOutletIdAndMasterMenuItemId(outletId, masterMenuItemId);
        OutletMenuOverride target = existing.orElse(new OutletMenuOverride());
        
        target.setOutletId(outletId);
        target.setMasterMenuItemId(masterMenuItemId);
        if (override.getOverriddenPrice() != null) target.setOverriddenPrice(override.getOverriddenPrice());
        if (override.getIsAvailable() != null) target.setIsAvailable(override.getIsAvailable());
        if (override.getOverriddenPrepTimeMinutes() != null) target.setOverriddenPrepTimeMinutes(override.getOverriddenPrepTimeMinutes());
        
        return outletMenuOverrideRepository.save(target);
    }

    // Resolves the effective menu for an outlet (Master items + Overrides)
    @Transactional(readOnly = true)
    public List<MenuItemDTO> getEffectiveMenuForOutlet(UUID outletId) {
        Outlet outlet = outletRepository.findById(outletId)
                .orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
                
        List<MasterMenuItem> masterItems = masterMenuItemRepository.findByBrandId(outlet.getBrandId());
        List<OutletMenuOverride> overrides = outletMenuOverrideRepository.findByOutletId(outletId);
        
        Map<UUID, OutletMenuOverride> overrideMap = overrides.stream()
            .collect(Collectors.toMap(OutletMenuOverride::getMasterMenuItemId, o -> o, (o1, o2) -> o1));
            
        return masterItems.stream().map(master -> {
            OutletMenuOverride override = overrideMap.get(master.getId());
                
            return MenuItemDTO.builder()
                .id(master.getId())
                .restaurantId(outletId)
                .name(master.getName())
                .description(master.getDescription())
                .price(override != null && override.getOverriddenPrice() != null ? override.getOverriddenPrice() : master.getBasePrice())
                .isAvailable(override != null && override.getIsAvailable() != null ? override.getIsAvailable() : true) // Default available
                .prepTimeMinutes(override != null && override.getOverriddenPrepTimeMinutes() != null ? override.getOverriddenPrepTimeMinutes() : master.getDefaultPrepTimeMinutes())
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
        
        Map<UUID, OutletMenuOverride> overrideMap = overrides.stream()
            .collect(Collectors.toMap(OutletMenuOverride::getMasterMenuItemId, o -> o, (o1, o2) -> o1));
            
        return masterItems.stream()
            .filter(master -> master.getBrandId().equals(outlet.getBrandId()))
            .map(master -> {
                OutletMenuOverride override = overrideMap.get(master.getId());
                    
                return MenuItemDTO.builder()
                    .id(master.getId())
                    .restaurantId(outletId)
                    .name(master.getName())
                    .description(master.getDescription())
                    .price(override != null && override.getOverriddenPrice() != null ? override.getOverriddenPrice() : master.getBasePrice())
                    .isAvailable(override != null && override.getIsAvailable() != null ? override.getIsAvailable() : true)
                    .prepTimeMinutes(override != null && override.getOverriddenPrepTimeMinutes() != null ? override.getOverriddenPrepTimeMinutes() : master.getDefaultPrepTimeMinutes())
                    .build();
        }).collect(Collectors.toList());
    }
}
