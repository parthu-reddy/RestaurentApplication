package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.BrandRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

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
    private RestTemplate restTemplate;

    @InjectMocks
    private RestaurantOnboardingService restaurantOnboardingService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(restaurantOnboardingService, "fssaiApiUrl", "http://mock/fssai");
        ReflectionTestUtils.setField(restaurantOnboardingService, "gstinApiUrl", "http://mock/gstin");
        ReflectionTestUtils.setField(restaurantOnboardingService, "pennyDropApiUrl", "http://mock/pennydrop");
    }

    @Test
    void testOnboardBrand_Success() {
        when(restTemplate.getForEntity(contains("gstin="), eq(java.util.Map.class)))
                .thenReturn(new ResponseEntity<>(new HashMap<>(), HttpStatus.OK));
        when(restTemplate.getForEntity(contains("account="), eq(java.util.Map.class)))
                .thenReturn(new ResponseEntity<>(new HashMap<>(), HttpStatus.OK));
        
        when(brandRepository.save(any(Brand.class))).thenAnswer(i -> i.getArguments()[0]);

        Brand brand = restaurantOnboardingService.onboardBrand(
                UUID.randomUUID(), "Test Brand", "123456789012345", "ABCDE1234F", "U12345MH2023PTC123456", "123456789", "HDFC0001234", null
        );

        assertNotNull(brand);
        assertEquals("Test Brand", brand.getName());
        assertTrue(brand.getIsGstinVerified());
        assertTrue(brand.getIsBankVerified());
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
        when(restTemplate.getForEntity(contains("fssai="), eq(java.util.Map.class)))
                .thenReturn(new ResponseEntity<>(new HashMap<>(), HttpStatus.OK));
        when(outletRepository.save(any(Outlet.class))).thenAnswer(i -> i.getArguments()[0]);

        com.fooddelivery.restaurant.controller.RestaurantOnboardingController.TimingRequest tr = new com.fooddelivery.restaurant.controller.RestaurantOnboardingController.TimingRequest();
        tr.setOpeningTime(LocalTime.of(9, 0));
        tr.setClosingTime(LocalTime.of(22, 0));

        Outlet outlet = restaurantOnboardingService.onboardOutlet(
                brandId, "Test Outlet", "12345678901234", 12.9716, 77.5946, java.util.List.of(tr), null, "Cuisine", 4.5, 100, 30, 0.0, "Tag"
        );

        assertNotNull(outlet);
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

        com.fooddelivery.restaurant.controller.RestaurantOnboardingController.TimingRequest tr = new com.fooddelivery.restaurant.controller.RestaurantOnboardingController.TimingRequest();
        tr.setOpeningTime(LocalTime.of(9, 0));
        tr.setClosingTime(LocalTime.of(22, 0));

        assertThrows(IllegalArgumentException.class, () -> restaurantOnboardingService.onboardOutlet(
                brandId, "Test Outlet", "SHORT", 12.9716, 77.5946, java.util.List.of(tr), null, "Cuisine", 4.5, 100, 30, 0.0, "Tag"
        ));
    }
}
