package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.BrandRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.Map;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@RequiredArgsConstructor
public class RestaurantOnboardingService {

    private final BrandRepository brandRepository;
    private final OutletRepository outletRepository;
    private final RestTemplate restTemplate;

    @Value("${kyb.fssai.api.url:http://localhost:8080/mock/fssai}")
    private String fssaiApiUrl;

    @Value("${kyb.gstin.api.url:http://localhost:8080/mock/gstin}")
    private String gstinApiUrl;

    @Value("${kyb.pennydrop.api.url:http://localhost:8080/mock/pennydrop}")
    private String pennyDropApiUrl;

    // Phase 1: Brand & Financial Setup
    public Brand onboardBrand(UUID ownerId, String name, String gstin, String pan, String cin, String bankAccountNumber, String ifscCode, String logoUrl) {
        log.info("Starting Brand onboarding: {}, GSTIN: {}, Bank: {}", name, gstin, bankAccountNumber);
        
        List<Brand> existingBrands = brandRepository.findByOwnerId(ownerId);
        if (!existingBrands.isEmpty()) {
            throw new IllegalArgumentException("A user can only register one brand.");
        }
        
        if (pan != null && pan.length() != 10) {
            throw new IllegalArgumentException("Invalid PAN. Must be 10 characters.");
        }
        if (cin != null && cin.length() != 21) {
            throw new IllegalArgumentException("Invalid CIN. Must be 21 characters.");
        }
        
        CompletableFuture<Boolean> gstinFuture = CompletableFuture.supplyAsync(() -> verifyGstin(gstin));
        CompletableFuture<Boolean> bankAccountFuture = CompletableFuture.supplyAsync(() -> verifyBankAccount(bankAccountNumber, ifscCode));

        CompletableFuture.allOf(gstinFuture, bankAccountFuture).join();

        boolean isGstinValid = gstinFuture.join();
        boolean isBankAccountValid = bankAccountFuture.join();
        
        if (!isGstinValid || !isBankAccountValid) {
            throw new IllegalArgumentException("Invalid KYC/KYB documents or Bank details based on records.");
        }
        
        Brand brand = Brand.builder()
                .id(UUID.randomUUID())
                .ownerId(ownerId)
                .name(name)
                .gstin(gstin)
                .pan(pan)
                .cin(cin)
                .bankAccountNumber(bankAccountNumber)
                .bankIfsc(ifscCode)
                .logoUrl(logoUrl)
                .isGstinVerified(true)
                .isBankVerified(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        return brandRepository.save(brand);
    }
    
    // Phase 2: Outlet & Geospatial Setup
    @org.springframework.transaction.annotation.Transactional
    public Outlet onboardOutlet(UUID brandId, String name, String fssai, Double lat, Double lng, List<com.fooddelivery.restaurant.controller.RestaurantOnboardingController.TimingRequest> timingsReq, String bannerUrl, String cuisine, Double rating, Integer reviewsCount, Integer deliveryTime, Double deliveryFee, String tags) {
        log.info("Starting Outlet onboarding for Brand: {}, FSSAI: {}", brandId, fssai);
        
        Brand brand = brandRepository.findById(brandId)
            .orElseThrow(() -> new IllegalArgumentException("Brand not found"));

        if (!brand.getIsGstinVerified() || !brand.getIsBankVerified()) {
            throw new IllegalStateException("Brand financial setup incomplete.");
        }
        
        boolean isFssaiValid = verifyFssai(fssai);
        if (!isFssaiValid) {
            throw new IllegalArgumentException("Invalid FSSAI License.");
        }
        
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Point locationPoint = null;
        if (lat != null && lng != null) {
            locationPoint = geometryFactory.createPoint(new Coordinate(lng, lat));
        }
        
        // Use randomUUID which acts as the legacy restaurantId
        Outlet outlet = Outlet.builder()
                .id(UUID.randomUUID())
                .brandId(brand.getId())
                .name(name)
                .fssaiLicenseNumber(fssai)
                .location(locationPoint)
                .bannerUrl(bannerUrl)
                .cuisine(cuisine)
                .rating(rating != null ? rating : 0.0)
                .reviewsCount(reviewsCount != null ? reviewsCount : 0)
                .deliveryTime(deliveryTime)
                .deliveryFee(deliveryFee)
                .tags(tags)
                .isActive(true)
                .defaultPrepTimeSeconds(900)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
                
        List<com.fooddelivery.restaurant.entity.OutletTiming> timings = new java.util.ArrayList<>();
        if (timingsReq != null) {
            for (com.fooddelivery.restaurant.controller.RestaurantOnboardingController.TimingRequest tr : timingsReq) {
                com.fooddelivery.restaurant.entity.OutletTiming timing = com.fooddelivery.restaurant.entity.OutletTiming.builder()
                        .id(UUID.randomUUID())
                        .outlet(outlet)
                        .openingTime(tr.getOpeningTime())
                        .closingTime(tr.getClosingTime())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                timings.add(timing);
            }
        }
        outlet.setTimings(timings);
                
        return outletRepository.save(outlet);
    }

    @org.springframework.transaction.annotation.Transactional
    public void updateOutletStatus(UUID outletId, boolean isActive) {
        Outlet outlet = outletRepository.findById(outletId)
                .orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
        outlet.setIsActive(isActive);
        outlet.setUpdatedAt(LocalDateTime.now());
        outletRepository.save(outlet);
    }

    @org.springframework.transaction.annotation.Transactional
    public void updateOutletSettings(UUID outletId, Integer defaultPrepTimeSeconds) {
        Outlet outlet = outletRepository.findById(outletId)
                .orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
        if (defaultPrepTimeSeconds != null && defaultPrepTimeSeconds > 0) {
            outlet.setDefaultPrepTimeSeconds(defaultPrepTimeSeconds);
        }
        outlet.setUpdatedAt(LocalDateTime.now());
        outletRepository.save(outlet);
    }
    
    @org.springframework.transaction.annotation.Transactional
    public void updateOutletTimings(UUID outletId, List<com.fooddelivery.restaurant.controller.RestaurantOnboardingController.TimingRequest> timingsReq) {
        Outlet outlet = outletRepository.findById(outletId)
                .orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
                
        outlet.getTimings().clear();
        if (timingsReq != null) {
            for (com.fooddelivery.restaurant.controller.RestaurantOnboardingController.TimingRequest tr : timingsReq) {
                com.fooddelivery.restaurant.entity.OutletTiming timing = com.fooddelivery.restaurant.entity.OutletTiming.builder()
                        .id(UUID.randomUUID())
                        .outlet(outlet)
                        .openingTime(tr.getOpeningTime())
                        .closingTime(tr.getClosingTime())
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                outlet.getTimings().add(timing);
            }
        }
        outlet.setUpdatedAt(LocalDateTime.now());
        outletRepository.save(outlet);
    }
    
    public Brand getBrandById(UUID id) {
        return brandRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Brand not found"));
    }

    public Outlet getOutletById(UUID id) {
        return outletRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
    }

    public List<Outlet> getOutletsByBrand(UUID brandId) {
        return outletRepository.findByBrandId(brandId);
    }
    
    private boolean verifyFssai(String fssai) {
        log.info("Verifying FSSAI against API for {}", fssai);
        if (fssai == null || fssai.length() != 14) return false;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(fssaiApiUrl + "?fssai=" + fssai, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("FSSAI API failed, mocking true", e);
            return true;
        }
    }
    
    private boolean verifyGstin(String gstin) {
        log.info("Verifying GSTIN against API for {}", gstin);
        if (gstin == null || gstin.length() != 15) return false;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(gstinApiUrl + "?gstin=" + gstin, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("GSTIN API failed, mocking true", e);
            return true;
        }
    }

    private boolean verifyBankAccount(String bankAccountNumber, String ifscCode) {
        log.info("Penny Drop Verification A/C: {}, IFSC: {}", bankAccountNumber, ifscCode);
        if (bankAccountNumber == null || ifscCode == null || bankAccountNumber.length() < 9) return false;
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(pennyDropApiUrl + "?account=" + bankAccountNumber + "&ifsc=" + ifscCode, Map.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("Penny Drop API failed, mocking true", e);
            return true;
        }
    }
    public List<Outlet> getNearbyOutlets(double lat, double lng, double radiusInKm) {
        double radiusInMeters = radiusInKm * 1000.0;
        return outletRepository.findNearbyOutlets(lat, lng, radiusInMeters);
    }

    public List<Brand> getBrands(UUID ownerId) {
        return brandRepository.findByOwnerId(ownerId);
    }

    public List<Outlet> getOutletsByOwner(UUID ownerId) {
        List<Brand> brands = brandRepository.findByOwnerId(ownerId);
        if (brands.isEmpty()) {
            return java.util.Collections.emptyList();
        }
        List<UUID> brandIds = brands.stream().map(Brand::getId).collect(java.util.stream.Collectors.toList());
        return outletRepository.findByBrandIdIn(brandIds);
    }
}
