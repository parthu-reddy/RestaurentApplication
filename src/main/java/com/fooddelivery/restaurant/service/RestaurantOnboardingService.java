package com.fooddelivery.restaurant.service;

import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.BrandRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import com.fooddelivery.common.outbox.repository.OutboxEventRepository;
import com.fooddelivery.common.outbox.entity.OutboxEventEntity;
import com.fooddelivery.common.constants.AggregateType;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.common.enums.OutboxStatus;
import com.fooddelivery.common.enums.VerificationStatus;
import com.fooddelivery.common.enums.VerificationType;
import com.fooddelivery.restaurant.dto.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
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
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class RestaurantOnboardingService {
private static final String KEY_BRAND_ID = "brandId";
    private static final String KEY_BRAND_NAME = "brandName";
    private static final String KEY_GSTIN = "gstin";
    private static final String KEY_BANK_ACCOUNT_NUMBER = "bankAccountNumber";
    private static final String KEY_IFSC_CODE = "ifscCode";
    private static final String KEY_OUTLET_ID = "outletId";
    private static final String KEY_IS_ACTIVE = "isActive";
    private static final String KEY_TIMESTAMP = "timestamp";
    private final BrandRepository brandRepository;
    private final OutletRepository outletRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final org.springframework.cache.CacheManager cacheManager;
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    // Phase 1: Brand & Financial Setup
    @org.springframework.transaction.annotation.Transactional
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
        boolean isDev = activeProfile != null && (activeProfile.contains("dev") || activeProfile.contains("test"));
        VerificationStatus initialStatus = isDev ? VerificationStatus.VERIFIED : VerificationStatus.PENDING;
        boolean initialVerified = isDev;

        Brand brand = Brand.builder().id(UUID.randomUUID()).ownerId(ownerId).name(name).gstin(gstin).pan(pan).cin(cin).bankAccountNumber(bankAccountNumber).bankIfsc(ifscCode).logoUrl(logoUrl).isGstinVerified(initialVerified).isBankVerified(initialVerified).kycStatus(initialStatus).pennyDropStatus(initialStatus).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        brand = brandRepository.save(brand);
        try {
            // Write to Outbox table within the same transaction for CDC/Kafka
            Map<String, Object> payload = Map.of(KEY_BRAND_ID, brand.getId().toString(), KEY_BRAND_NAME, brand.getName(), KEY_GSTIN, brand.getGstin(), KEY_BANK_ACCOUNT_NUMBER, brand.getBankAccountNumber(), KEY_IFSC_CODE, brand.getBankIfsc(), KEY_TIMESTAMP, LocalDateTime.now().toString());
            OutboxEventEntity event = OutboxEventEntity.builder().id(UUID.randomUUID()).aggregateType(AggregateType.BRAND).aggregateId(brand.getId().toString()).eventType(EventType.BRAND_CREATED).payload(objectMapper.writeValueAsString(payload)).status(OutboxStatus.UNPROCESSED).createdAt(LocalDateTime.now()).retryCount(0).build();
            outboxEventRepository.save(event);
        } catch (Exception e) {
            log.error("Failed to serialize outbox event payload for Brand", e);
            throw new RuntimeException("Failed to publish brand created event", e);
        }
        return brand;
    }

    // Phase 2: Outlet & Geospatial Setup
    @org.springframework.transaction.annotation.Transactional
    public Outlet onboardOutlet(UUID brandId, String name, String fssai, Double lat, Double lng, List<com.fooddelivery.restaurant.dto.TimingRequest> timingsReq, String bannerUrl, String cuisine, Double rating, Integer reviewsCount, Integer deliveryTime, java.math.BigDecimal deliveryFee, String tags) {
        log.info("Starting Outlet onboarding for Brand: {}, FSSAI: {}", brandId, fssai);
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        if (!brand.getIsGstinVerified() || !brand.getIsBankVerified()) {
            boolean isDev = activeProfile != null && activeProfile.contains("dev");
            if (!isDev) {
                throw new IllegalStateException("Brand financial setup incomplete.");
            } else {
                log.info("Bypassing brand financial setup check for dev profile");
            }
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
        Outlet outlet = Outlet.builder().id(UUID.randomUUID()).brandId(brand.getId()).name(name).fssaiLicenseNumber(fssai).location(locationPoint).bannerUrl(bannerUrl).cuisine(cuisine).rating(rating != null ? rating : 0.0).reviewsCount(reviewsCount != null ? reviewsCount : 0).deliveryTime(deliveryTime).deliveryFee(deliveryFee).tags(tags).isActive(true).defaultPrepTimeSeconds(900).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
        List<com.fooddelivery.restaurant.entity.OutletTiming> timings = new java.util.ArrayList<>();
        if (timingsReq != null) {
            for (com.fooddelivery.restaurant.dto.TimingRequest tr : timingsReq) {
                com.fooddelivery.restaurant.entity.OutletTiming timing = com.fooddelivery.restaurant.entity.OutletTiming.builder().id(UUID.randomUUID()).outlet(outlet).openingTime(tr.getOpeningTime()).closingTime(tr.getClosingTime()).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
                timings.add(timing);
            }
        }
        outlet.setTimings(timings);
        return outletRepository.save(outlet);
    }

    @org.springframework.transaction.annotation.Transactional
    public void updateOutletStatus(UUID outletId, boolean isActive) {
        Outlet outlet = outletRepository.findById(outletId).orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
        outlet.setIsActive(isActive);
        outlet.setUpdatedAt(LocalDateTime.now());
        outletRepository.save(outlet);
        try {
            // Write to Outbox table within the same transaction for CDC/Kafka
            Map<String, Object> payload = Map.of(KEY_OUTLET_ID, outletId.toString(), KEY_BRAND_ID, outlet.getBrandId().toString(), KEY_IS_ACTIVE, isActive, KEY_TIMESTAMP, LocalDateTime.now().toString());
            OutboxEventEntity event = OutboxEventEntity.builder().id(UUID.randomUUID()).aggregateType(AggregateType.OUTLET).aggregateId(outletId.toString()).eventType(isActive ? EventType.OUTLET_ACTIVATED : EventType.OUTLET_DEACTIVATED).payload(objectMapper.writeValueAsString(payload)).status(OutboxStatus.UNPROCESSED).createdAt(LocalDateTime.now()).retryCount(0).build();
            outboxEventRepository.save(event);
            
            // Proactively invalidate menu cache
            org.springframework.cache.Cache cache = cacheManager.getCache("outletMenus");
            if (cache != null) {
                cache.evict(outletId);
            }
        } catch (Exception e) {
            log.error("Failed to serialize outbox event payload", e);
            throw new RuntimeException("Failed to publish outlet status event", e);
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public void updateOutletSettings(UUID outletId, Integer defaultPrepTimeSeconds) {
        Outlet outlet = outletRepository.findById(outletId).orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
        if (defaultPrepTimeSeconds != null && defaultPrepTimeSeconds > 0) {
            outlet.setDefaultPrepTimeSeconds(defaultPrepTimeSeconds);
        }
        outlet.setUpdatedAt(LocalDateTime.now());
        outletRepository.save(outlet);
        
        // Proactively invalidate menu cache
        org.springframework.cache.Cache cache = cacheManager.getCache("outletMenus");
        if (cache != null) {
            cache.evict(outletId);
        }
    }

    @org.springframework.transaction.annotation.Transactional
    public void updateOutletTimings(UUID outletId, List<com.fooddelivery.restaurant.dto.TimingRequest> timingsReq) {
        Outlet outlet = outletRepository.findById(outletId).orElseThrow(() -> new IllegalArgumentException("Outlet not found"));
        outlet.getTimings().clear();
        if (timingsReq != null) {
            for (com.fooddelivery.restaurant.dto.TimingRequest tr : timingsReq) {
                com.fooddelivery.restaurant.entity.OutletTiming timing = com.fooddelivery.restaurant.entity.OutletTiming.builder().id(UUID.randomUUID()).outlet(outlet).openingTime(tr.getOpeningTime()).closingTime(tr.getClosingTime()).createdAt(LocalDateTime.now()).updatedAt(LocalDateTime.now()).build();
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
        log.info("Mock verifying FSSAI for {}", fssai);
        return fssai != null && fssai.length() == 14;
    }

    private boolean verifyGstin(String gstin) {
        log.info("Mock verifying GSTIN for {}", gstin);
        return gstin != null && gstin.length() == 15;
    }

    private boolean verifyBankAccount(String bankAccountNumber, String ifscCode) {
        log.info("Mock Penny Drop Verification A/C: {}, IFSC: {}", bankAccountNumber, ifscCode);
        return bankAccountNumber != null && ifscCode != null && bankAccountNumber.length() >= 9;
    }

    public org.springframework.data.domain.Page<Outlet> getAllOutlets(org.springframework.data.domain.Pageable pageable) {
        return outletRepository.findAll(pageable);
    }

    public List<Outlet> getNearbyOutlets(double lat, double lng, double radiusInKm) {
        double radiusInMeters = radiusInKm * 1000.0;
        List<Outlet> rawOutlets = outletRepository.findNearbyOutlets(lat, lng, radiusInMeters);
        return populateOutletTimingsInOrder(rawOutlets);
    }

    public List<Outlet> getNearbyOutletsByBrand(UUID brandId, double lat, double lng, double radiusInKm) {
        double radiusInMeters = radiusInKm * 1000.0;
        List<Outlet> rawOutlets = outletRepository.findNearbyOutletsByBrand(brandId, lat, lng, radiusInMeters);
        return populateOutletTimingsInOrder(rawOutlets);
    }

    private List<Outlet> populateOutletTimingsInOrder(List<Outlet> rawOutlets) {
        if (rawOutlets == null || rawOutlets.isEmpty()) {
            return rawOutlets;
        }
        List<UUID> outletIds = rawOutlets.stream().map(Outlet::getId).collect(java.util.stream.Collectors.toList());
        List<Outlet> fullyPopulated = outletRepository.findByIdIn(outletIds);
        java.util.Map<UUID, Outlet> outletMap = fullyPopulated.stream().collect(java.util.stream.Collectors.toMap(Outlet::getId, o -> o));
        // Maintain the original geospatial sorted order returned by the native query
        return rawOutlets.stream().map(o -> outletMap.getOrDefault(o.getId(), o)).collect(java.util.stream.Collectors.toList());
    }

    public List<Brand> getBrands(UUID ownerId) {
        return brandRepository.findByOwnerId(ownerId);
    }

    public List<Outlet> getOutletsByOwner(UUID ownerId) {
        return outletRepository.findByOwnerId(ownerId);
    }

    @org.springframework.transaction.annotation.Transactional
    public void updateVerificationStatusFromCallback(UUID brandId, String verificationType, String status, String legalEntityName, String bankBeneficiaryName) {
        Brand brand = brandRepository.findById(brandId).orElseThrow(() -> new IllegalArgumentException("Brand not found"));
        VerificationType type;
        try {
            type = VerificationType.valueOf(verificationType.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Unknown verification type received: {}", verificationType);
            return;
        }
        VerificationStatus statusEnum;
        try {
            String statusStr = VerificationStatus.APPROVED.name().equalsIgnoreCase(status) ? VerificationStatus.VERIFIED.name() : status;
            statusEnum = VerificationStatus.valueOf(statusStr.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            log.warn("Unknown verification status received: {}", status);
            return;
        }
        if (type == VerificationType.GSTIN) {
            brand.setKycStatus(statusEnum);
            if (statusEnum == VerificationStatus.VERIFIED || statusEnum == VerificationStatus.APPROVED) {
                brand.setIsGstinVerified(true);
                brand.setLegalEntityName(legalEntityName);
            } else {
                brand.setIsGstinVerified(false);
            }
        } else if (type == VerificationType.PENNY_DROP) {
            brand.setPennyDropStatus(statusEnum);
            if (statusEnum == VerificationStatus.VERIFIED || statusEnum == VerificationStatus.APPROVED) {
                brand.setIsBankVerified(true);
            } else {
                brand.setIsBankVerified(false);
            }
            brand.setBankBeneficiaryName(bankBeneficiaryName);
        }
        brandRepository.save(brand);
    }

}
// @Getter
