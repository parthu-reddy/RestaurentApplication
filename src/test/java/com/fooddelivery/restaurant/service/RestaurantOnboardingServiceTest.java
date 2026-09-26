package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.BrandRepository;
import com.fooddelivery.common.outbox.repository.OutboxEventRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantOnboardingServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private OutletRepository outletRepository;

    @Mock
    private OutboxEventRepository outboxEventRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private org.springframework.cache.CacheManager cacheManager;

    @InjectMocks
    private RestaurantOnboardingService restaurantOnboardingService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testOnboardBrand_Success() {
        when(brandRepository.save(any(Brand.class))).thenAnswer(i -> i.getArguments()[0]);

        Brand brand = restaurantOnboardingService.onboardBrand(
                UUID.randomUUID(), "Test Brand", "123456789012345", "ABCDE1234F", "U12345MH2023PTC123456", "123456789", "HDFC0001234", null
        );

        assertNotNull(brand);
        assertEquals("Test Brand", brand.getName());
        assertFalse(brand.getIsGstinVerified());
        assertFalse(brand.getIsBankVerified());
    }

    @Test
    void testOnboardBrand_InvalidPan() {
        assertThrows(IllegalArgumentException.class, () -> restaurantOnboardingService.onboardBrand(
                UUID.randomUUID(), "Test Brand", "123456789012345", "SHORT", "U12345MH2023PTC123456", "123456789", "HDFC0001234", null
        ));
    }
    
    @Test
    void testOnboardOutlet_Success() {
        UUID brandId = UUID.randomUUID();
        Brand brand = Brand.builder()
                .id(brandId)
                .isGstinVerified(true)
                .isBankVerified(true)
                .build();
                
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));
        when(outletRepository.save(any(Outlet.class))).thenAnswer(i -> i.getArguments()[0]);

        com.fooddelivery.restaurant.dto.TimingRequest tr = new com.fooddelivery.restaurant.dto.TimingRequest();
        tr.setOpeningTime(LocalTime.of(9, 0));
        tr.setClosingTime(LocalTime.of(22, 0));

        Outlet outlet = restaurantOnboardingService.onboardOutlet(
                brandId, "Test Outlet", "12345678901234", 12.9716, 77.5946, java.util.List.of(tr), null, "Cuisine", 4.5, 100, 30, java.math.BigDecimal.ZERO, "Tag", "Asia/Kolkata"
        );

        assertNotNull(outlet);
        assertEquals(java.time.ZoneId.of("Asia/Kolkata"), outlet.getTimeZone());
        assertEquals(brandId, outlet.getBrandId());
        assertEquals("12345678901234", outlet.getFssaiLicenseNumber());
    }
    
    @Test
    void testOnboardOutlet_InvalidFssai() {
        UUID brandId = UUID.randomUUID();
        Brand brand = Brand.builder()
                .id(brandId)
                .isGstinVerified(true)
                .isBankVerified(true)
                .build();
                
        when(brandRepository.findById(brandId)).thenReturn(Optional.of(brand));

        com.fooddelivery.restaurant.dto.TimingRequest tr = new com.fooddelivery.restaurant.dto.TimingRequest();
        tr.setOpeningTime(LocalTime.of(9, 0));
        tr.setClosingTime(LocalTime.of(22, 0));

        assertThrows(IllegalArgumentException.class, () -> restaurantOnboardingService.onboardOutlet(
                brandId, "Test Outlet", "SHORT", 12.9716, 77.5946, java.util.List.of(tr), null, "Cuisine", 4.5, 100, 30, java.math.BigDecimal.ZERO, "Tag", "Asia/Kolkata"
        ));
    }

    /** An offset is not a zone: "+05:30" has no daylight-saving rules, so an outlet stored with one is wrong half the year wherever DST applies. */
    @Test
    void testOnboardOutlet_RefusesAnOffsetForATimeZone() {
        // Refused before anything is looked up: no brand stub is needed.
        UUID brandId = UUID.randomUUID();
        com.fooddelivery.restaurant.dto.TimingRequest tr = new com.fooddelivery.restaurant.dto.TimingRequest();
        tr.setOpeningTime(LocalTime.of(9, 0));
        tr.setClosingTime(LocalTime.of(22, 0));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> restaurantOnboardingService.onboardOutlet(
                brandId, "Test Outlet", "12345678901234", 12.9716, 77.5946, java.util.List.of(tr), null, "Cuisine", 4.5, 100, 30, java.math.BigDecimal.ZERO, "Tag", "+05:30"
        ));
        org.junit.jupiter.api.Assertions.assertTrue(e.getMessage().contains("timeZone"), e.getMessage());
    }

    @Test
    void updateOutletSettings_tellsCustomerAppToDropItsCachedOutlet() {
        // CustomerApplication floors every new order's prep time at this default and caches the
        // outlet; MENU_UPDATED for the brand is what clears that cache.
        UUID outletId = UUID.randomUUID();
        UUID brandId = UUID.randomUUID();
        Outlet outlet = Outlet.builder().id(outletId).brandId(brandId).defaultPrepTimeSeconds(900).build();
        when(outletRepository.findById(outletId)).thenReturn(Optional.of(outlet));

        restaurantOnboardingService.updateOutletSettings(outletId, 1500);

        assertEquals(1500, outlet.getDefaultPrepTimeSeconds());
        org.mockito.ArgumentCaptor<com.fooddelivery.common.outbox.entity.OutboxEventEntity> event =
                org.mockito.ArgumentCaptor.forClass(com.fooddelivery.common.outbox.entity.OutboxEventEntity.class);
        verify(outboxEventRepository).save(event.capture());
        assertEquals(com.fooddelivery.common.constants.EventType.MENU_UPDATED, event.getValue().getEventType());
        assertEquals(brandId.toString(), event.getValue().getAggregateId());
        assertTrue(event.getValue().getPayload().contains("\"brandId\":\"" + brandId + "\""));
        assertTrue(event.getValue().getPayload().contains("\"type\":\"MENU_UPDATED\""));
    }
}
