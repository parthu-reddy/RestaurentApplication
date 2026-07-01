package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.dto.MenuItemDTO;
import com.fooddelivery.restaurant.entity.MasterMenuItem;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.entity.OutletMenuOverride;
import com.fooddelivery.restaurant.repository.MasterMenuItemRepository;
import com.fooddelivery.restaurant.repository.OutletMenuOverrideRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private MasterMenuItemRepository masterMenuItemRepository;

    @Mock
    private OutletMenuOverrideRepository outletMenuOverrideRepository;

    @Mock
    private OutletRepository outletRepository;

    @InjectMocks
    private CatalogService catalogService;

    @Test
    void testGetEffectiveMenuForOutlet() {
        UUID brandId = UUID.randomUUID();
        UUID outletId = UUID.randomUUID();
        
        Outlet outlet = Outlet.builder().id(outletId).brandId(brandId).build();
        when(outletRepository.findById(outletId)).thenReturn(Optional.of(outlet));

        MasterMenuItem master1 = new MasterMenuItem();
        master1.setId(UUID.randomUUID());
        master1.setName("Burger");
        master1.setBasePrice(new BigDecimal("100.00"));
        master1.setDefaultPrepTimeMinutes(15);

        MasterMenuItem master2 = new MasterMenuItem();
        master2.setId(UUID.randomUUID());
        master2.setName("Fries");
        master2.setBasePrice(new BigDecimal("50.00"));
        master2.setDefaultPrepTimeMinutes(10);

        when(masterMenuItemRepository.findByBrandId(brandId)).thenReturn(Arrays.asList(master1, master2));

        OutletMenuOverride override = new OutletMenuOverride();
        override.setMasterMenuItemId(master1.getId());
        override.setOverriddenPrice(new BigDecimal("120.00")); // Override price
        override.setIsAvailable(true);
        override.setOverriddenPrepTimeMinutes(20);

        when(outletMenuOverrideRepository.findByOutletId(outletId)).thenReturn(Arrays.asList(override));

        List<MenuItemDTO> effectiveMenu = catalogService.getEffectiveMenuForOutlet(outletId);
        
        assertEquals(2, effectiveMenu.size());
        
        MenuItemDTO dto1 = effectiveMenu.stream().filter(i -> i.getId().equals(master1.getId())).findFirst().get();
        assertEquals(new BigDecimal("120.00"), dto1.getPrice());
        assertTrue(dto1.getIsAvailable());
        assertEquals(20, dto1.getPrepTimeMinutes());
        
        MenuItemDTO dto2 = effectiveMenu.stream().filter(i -> i.getId().equals(master2.getId())).findFirst().get();
        assertEquals(new BigDecimal("50.00"), dto2.getPrice());
        assertTrue(dto2.getIsAvailable());
        assertEquals(10, dto2.getPrepTimeMinutes());
    }
}
