package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurant.entity.*;
import com.fooddelivery.restaurant.repository.*;
import com.fooddelivery.common.outbox.repository.OutboxEventRepository;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CatalogStockToggleTest {
    MasterMenuItemRepository menu=mock(MasterMenuItemRepository.class);
    OutletMenuOverrideRepository overrides=mock(OutletMenuOverrideRepository.class);
    OutletRepository outlets=mock(OutletRepository.class);
    OutboxEventRepository outbox=mock(OutboxEventRepository.class);
    CatalogService service=new CatalogService(menu,overrides,outlets,mock(CategoryRepository.class),
            mock(OutletCategoryTimingRepository.class),mock(BrandCategoryTimingRepository.class),
            outbox,new ObjectMapper(),java.time.Clock.systemUTC());
    UUID outlet=UUID.randomUUID(),item=UUID.randomUUID(),brand=UUID.randomUUID();
    @Test void stockSwitchPreservesExistingPriceAndPreparationTime() {
        when(outlets.findById(outlet)).thenReturn(Optional.of(Outlet.builder().id(outlet).brandId(brand).build()));
        when(menu.findById(item)).thenReturn(Optional.of(MasterMenuItem.builder().id(item).brandId(brand).build()));
        var existing=OutletMenuOverride.builder().id(UUID.randomUUID()).outletId(outlet).masterMenuItemId(item)
                .overriddenPrice(new BigDecimal("123.45")).overriddenPrepTimeMinutes(19).isAvailable(true).build();
        when(overrides.findByOutletIdAndMasterMenuItemId(outlet,item)).thenReturn(Optional.of(existing));
        when(overrides.save(existing)).thenReturn(existing);
        var changed=service.toggleStock(outlet,item,false);
        assertFalse(changed.getIsAvailable());assertEquals(new BigDecimal("123.45"),changed.getOverriddenPrice());
        assertEquals(19,changed.getOverriddenPrepTimeMinutes());
        var event=org.mockito.ArgumentCaptor.forClass(com.fooddelivery.common.outbox.entity.OutboxEventEntity.class);
        verify(outbox).save(event.capture());
        assertEquals(com.fooddelivery.common.constants.EventType.MENU_UPDATED,event.getValue().getEventType());
        assertEquals(brand.toString(),event.getValue().getAggregateId());
    }
    @Test void anItemFromAnotherBrandCannotBeToggledOrPriceOverriddenAtThisOutlet() {
        when(outlets.findById(outlet)).thenReturn(Optional.of(Outlet.builder().id(outlet).brandId(brand).build()));
        when(menu.findById(item)).thenReturn(Optional.of(MasterMenuItem.builder().id(item).brandId(UUID.randomUUID()).build()));
        assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.toggleStock(outlet,item,true));
        assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.addOrUpdateOverride(outlet,item,new OutletMenuOverride()));
        verifyNoInteractions(overrides,outbox);
    }
}
