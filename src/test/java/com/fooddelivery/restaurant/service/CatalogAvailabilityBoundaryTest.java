package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.common.outbox.repository.OutboxEventRepository;
import com.fooddelivery.restaurant.entity.*;
import com.fooddelivery.restaurant.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.*;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/** Real cache advice must not freeze time-dependent availability or fresh outlet overrides. */
@SpringJUnitConfig(CatalogAvailabilityBoundaryTest.Config.class)
class CatalogAvailabilityBoundaryTest {
    @Configuration @EnableCaching static class Config {
        @Bean MasterMenuItemRepository menu() { return mock(MasterMenuItemRepository.class); }
        @Bean OutletMenuOverrideRepository overrides() { return mock(OutletMenuOverrideRepository.class); }
        @Bean OutletRepository outlets() { return mock(OutletRepository.class); }
        @Bean CategoryRepository categories() { return mock(CategoryRepository.class); }
        @Bean OutletCategoryTimingRepository hours() { return mock(OutletCategoryTimingRepository.class); }
        @Bean BrandCategoryTimingRepository brandHours() { return mock(BrandCategoryTimingRepository.class); }
        @Bean Clock clock() { return mock(Clock.class); }
        @Bean CacheManager cacheManager() { return new ConcurrentMapCacheManager("outletMenus"); }
        @Bean CatalogService catalog(MasterMenuItemRepository m, OutletMenuOverrideRepository v,
                OutletRepository o, CategoryRepository c, OutletCategoryTimingRepository h,
                BrandCategoryTimingRepository b, Clock clock) {
            return new CatalogService(m,v,o,c,h,b,mock(OutboxEventRepository.class),new ObjectMapper(),clock);
        }
    }
    @Autowired CatalogService catalog;
    @Autowired MasterMenuItemRepository menu;
    @Autowired OutletMenuOverrideRepository overrides;
    @Autowired OutletRepository outlets;
    @Autowired CategoryRepository categories;
    @Autowired OutletCategoryTimingRepository hours;
    @Autowired BrandCategoryTimingRepository brandHours;
    @Autowired Clock clock;
    @Autowired CacheManager cacheManager;
    UUID outlet, brand, item;

    @BeforeEach void setup() {
        reset(menu,overrides,outlets,categories,hours,brandHours,clock);
        cacheManager.getCache("outletMenus").clear();
        outlet=UUID.randomUUID(); brand=UUID.randomUUID(); item=UUID.randomUUID();
        Category category=Category.builder().id(UUID.randomUUID()).name("Food").active(true).build();
        when(outlets.findById(outlet)).thenReturn(Optional.of(Outlet.builder().id(outlet).brandId(brand)
                .timeZone(ZoneId.of("Asia/Kolkata")).build()));
        when(menu.findByBrandId(brand)).thenReturn(List.of(MasterMenuItem.builder().id(item).brandId(brand)
                .categoryId(category.getId()).name("Dish").basePrice(new BigDecimal("100.00"))
                .packingCharge(BigDecimal.ZERO).defaultPrepTimeMinutes(15).build()));
        when(categories.findActiveCategoriesForBrand(brand)).thenReturn(List.of(category));
        when(hours.findByOutletId(outlet)).thenReturn(List.of(OutletCategoryTiming.builder().category(category)
                .openingTime(LocalTime.of(9,0)).closingTime(LocalTime.of(22,0)).build()));
        when(brandHours.findByBrandId(brand)).thenReturn(List.of());
        when(overrides.findByOutletId(outlet)).thenReturn(List.of());
        when(clock.instant()).thenReturn(Instant.parse("2026-10-03T16:29:59Z"));
    }
    @Test void aPreviouslyReadMenuClosesAndReopensOnTheOutletClockWithoutAWrite() {
        assertTrue(catalog.getEffectiveMenuForOutlet(outlet).get(0).getIsAvailable());
        when(clock.instant()).thenReturn(Instant.parse("2026-10-03T16:30:01Z"));
        assertFalse(catalog.getEffectiveMenuForOutlet(outlet).get(0).getIsAvailable());
        when(clock.instant()).thenReturn(Instant.parse("2026-10-04T03:30:01Z"));
        assertTrue(catalog.getEffectiveMenuForOutlet(outlet).get(0).getIsAvailable());
    }
    @Test void aPreviouslyReadMenuReflectsCommittedStockAndPriceOnTheNextRead() {
        assertTrue(catalog.getEffectiveMenuForOutlet(outlet).get(0).getIsAvailable());
        var updated=OutletMenuOverride.builder().masterMenuItemId(item).isAvailable(false)
                .overriddenPrice(new BigDecimal("123.45")).build();
        when(overrides.findByOutletId(outlet)).thenReturn(List.of(updated));
        var read=catalog.getEffectiveMenuForOutlet(outlet).get(0);
        assertFalse(read.getIsAvailable());
        assertEquals(new BigDecimal("123.45"),read.getPrice());
        updated.setIsAvailable(true);
        assertTrue(catalog.getEffectiveMenuForOutlet(outlet).get(0).getIsAvailable());
    }
}
