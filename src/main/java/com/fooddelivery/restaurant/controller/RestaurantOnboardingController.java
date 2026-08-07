package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.service.RestaurantOnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalTime;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
public class RestaurantOnboardingController {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestaurantOnboardingController.class);
    private final RestaurantOnboardingService onboardingService;
    private final com.fooddelivery.restaurant.security.RestaurantSecurityHelper securityHelper;
    private final com.fooddelivery.restaurant.client.GovernmentIdClient governmentIdClient;

    // Phase 1: Brand Onboarding
    @PostMapping("/api/v1/brands")
    @PreAuthorize("hasRole(\'RESTAURANT\')")
    public ResponseEntity<ApiResponse<Brand>> onboardBrand(java.security.Principal principal, @Valid @RequestBody BrandOnboardRequest request) {
        Brand brand = onboardingService.onboardBrand(UUID.fromString(principal.getName()), request.getName(), request.getGstin(), request.getPan(), request.getCin(), request.getBankAccountNumber(), request.getIfscCode(), request.getLogoUrl());
        // KYC is triggered async via Outbox/Kafka in the onboardingService
        return ResponseEntity.ok(ApiResponse.success(brand, "Brand onboarded successfully. KYC pending."));
    }

    // Callback from GovernmentIDValidationService
    @PostMapping("/api/v1/internal/brands/{brandId}/verification-callback")
    public ResponseEntity<Void> updateVerificationStatus(@PathVariable UUID brandId, @RequestBody VerificationCallbackRequest request) {
        onboardingService.updateVerificationStatusFromCallback(brandId, request.getVerificationType(), request.getStatus(), request.getLegalEntityName(), request.getBankBeneficiaryName());
        return ResponseEntity.ok().build();
    }

    // Proxy endpoints for KYC
    @GetMapping("/api/v1/restaurants/verification/upload-url")
    @PreAuthorize("hasRole(\'RESTAURANT\')")
    public ResponseEntity<ApiResponse<Map<String, String>>> getPresignedUploadUrl(@org.springframework.web.bind.annotation.RequestParam("docType") String docType, @org.springframework.web.bind.annotation.RequestParam("contentType") String contentType) {
        Map<String, String> response = governmentIdClient.getPresignedUploadUrl(docType, contentType);
        return ResponseEntity.ok(ApiResponse.success(response, "Upload URL generated"));
    }

    @PostMapping("/api/v1/restaurants/verification/brands/gstin")
    @PreAuthorize("hasRole(\'RESTAURANT\')")
    public ResponseEntity<ApiResponse<Void>> verifyGstin(@Valid @RequestBody com.fooddelivery.restaurant.client.GovernmentIdClient.GstinRequest request) {
        governmentIdClient.verifyGstin(request);
        return ResponseEntity.ok(ApiResponse.success(null, "GSTIN verification initiated"));
    }

    @PostMapping("/api/v1/restaurants/verification/brands/bank-account")
    @PreAuthorize("hasRole(\'RESTAURANT\')")
    public ResponseEntity<ApiResponse<Void>> verifyBankAccount(@Valid @RequestBody com.fooddelivery.restaurant.client.GovernmentIdClient.BankAccountRequest request) {
        governmentIdClient.verifyBankAccount(request);
        return ResponseEntity.ok(ApiResponse.success(null, "Bank account verification initiated"));
    }

    @GetMapping("/api/v1/brands")
    @PreAuthorize("hasRole(\'RESTAURANT\')")
    public ResponseEntity<ApiResponse<List<Brand>>> getBrands(java.security.Principal principal) {
        List<Brand> brands = onboardingService.getBrands(UUID.fromString(principal.getName()));
        return ResponseEntity.ok(ApiResponse.success(brands, "Brands retrieved successfully"));
    }

    @GetMapping("/api/v1/outlets")
    @PreAuthorize("hasRole(\'RESTAURANT\')")
    public ResponseEntity<ApiResponse<List<Outlet>>> getOutlets(java.security.Principal principal) {
        List<Outlet> outlets = onboardingService.getOutletsByOwner(UUID.fromString(principal.getName()));
        return ResponseEntity.ok(ApiResponse.success(outlets, "Outlets retrieved successfully"));
    }

    // Phase 2: Outlet Onboarding
    @PostMapping("/api/v1/brands/{brandId}/outlets")
    @PreAuthorize("hasRole(\'RESTAURANT\') and @restaurantSecurityHelper.isBrandOwner(#brandId, authentication.principal)")
    public ResponseEntity<ApiResponse<Outlet>> onboardOutlet(@PathVariable UUID brandId, @Valid @RequestBody OutletOnboardRequest request) {
        Outlet outlet = onboardingService.onboardOutlet(brandId, request.getName(), request.getFssaiLicenseNumber(), request.getLat(), request.getLng(), request.getTimings(), request.getBannerUrl(), request.getCuisine(), request.getRating(), request.getReviewsCount(), request.getDeliveryTime(), request.getDeliveryFee(), request.getTags());
        return ResponseEntity.ok(ApiResponse.success(outlet, "Outlet onboarded successfully"));
    }

    @org.springframework.web.bind.annotation.PutMapping("/api/v1/outlets/{outletId}/timings")
    @PreAuthorize("hasRole(\'RESTAURANT\') and @restaurantSecurityHelper.isOutletOwner(#outletId, authentication.principal)")
    public ResponseEntity<ApiResponse<Void>> updateOutletTimings(@PathVariable UUID outletId, @Valid @RequestBody OutletTimingsUpdateRequest request) {
        onboardingService.updateOutletTimings(outletId, request.getTimings());
        return ResponseEntity.ok(ApiResponse.success(null, "Outlet timings updated successfully"));
    }

    @GetMapping("/api/v1/brands/{brandId}/outlets")
    @PreAuthorize("hasRole(\'RESTAURANT\') and @restaurantSecurityHelper.isBrandOwner(#brandId, authentication.principal)")
    public ResponseEntity<ApiResponse<List<Outlet>>> getOutletsByBrand(@PathVariable UUID brandId) {
        return ResponseEntity.ok(ApiResponse.success(onboardingService.getOutletsByBrand(brandId), "Fetched outlets"));
    }

    @org.springframework.web.bind.annotation.PutMapping("/api/v1/outlets/{outletId}/status")
    @PreAuthorize("hasRole(\'RESTAURANT\') and @restaurantSecurityHelper.isOutletOwner(#outletId, authentication.principal)")
    public ResponseEntity<ApiResponse<Void>> updateOutletStatus(@PathVariable UUID outletId, @Valid @RequestBody OutletStatusUpdateRequest request) {
        onboardingService.updateOutletStatus(outletId, request.getIsActive());
        return ResponseEntity.ok(ApiResponse.success(null, "Outlet status updated successfully"));
    }

    @org.springframework.web.bind.annotation.PutMapping("/api/v1/outlets/{outletId}/settings")
    @PreAuthorize("hasRole(\'RESTAURANT\') and @restaurantSecurityHelper.isOutletOwner(#outletId, authentication.principal)")
    public ResponseEntity<ApiResponse<Void>> updateOutletSettings(@PathVariable UUID outletId, @Valid @RequestBody OutletSettingsUpdateRequest request) {
        onboardingService.updateOutletSettings(outletId, request.getDefaultPrepTimeSeconds());
        return ResponseEntity.ok(ApiResponse.success(null, "Outlet settings updated successfully"));
    }

    // Legacy backwards compatibility: CustomerApp uses /api/v1/restaurants/{id}
    @GetMapping("/api/v1/restaurants/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRestaurant(@PathVariable UUID id) {
        Outlet outlet = onboardingService.getOutletById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("id", outlet.getId());
        response.put("name", outlet.getName());
        response.put("isActive", outlet.getIsActive());
        response.put("defaultPrepTimeSeconds", outlet.getDefaultPrepTimeSeconds() != null ? outlet.getDefaultPrepTimeSeconds() : 900);
        boolean isOpen = false;
        if (outlet.getTimings() != null && !outlet.getTimings().isEmpty()) {
            java.time.LocalTime now = java.time.LocalTime.now(java.time.ZoneId.of("Asia/Kolkata"));
            for (com.fooddelivery.restaurant.entity.OutletTiming timing : outlet.getTimings()) {
                java.time.LocalTime start = timing.getOpeningTime();
                java.time.LocalTime end = timing.getClosingTime();
                if (start.isBefore(end) || start.equals(end)) {
                    if (!now.isBefore(start) && !now.isAfter(end)) {
                        isOpen = true;
                        break;
                    }
                } else {
                    if (!now.isBefore(start) || !now.isAfter(end)) {
                        isOpen = true;
                        break;
                    }
                }
            }
        } else {
            isOpen = false; // default to closed if no specific timings are configured
        }
        response.put("isOpen", isOpen);
        if (outlet.getLocation() != null) {
            response.put("lat", outlet.getLocation().getY());
            response.put("lng", outlet.getLocation().getX());
        }
        response.put("bannerUrl", outlet.getBannerUrl());
        response.put("deliveryFee", outlet.getDeliveryFee() != null ? outlet.getDeliveryFee() : 0.0);
        Brand brand = onboardingService.getBrandById(outlet.getBrandId());
        response.put("logoUrl", brand.getLogoUrl());
        return ResponseEntity.ok(ApiResponse.success(response, "Restaurant fetched successfully"));
    }

    @GetMapping("/api/v1/restaurants/nearby")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getNearbyRestaurants(@org.springframework.web.bind.annotation.RequestParam double lat, @org.springframework.web.bind.annotation.RequestParam double lng, @org.springframework.web.bind.annotation.RequestParam(defaultValue = "5.0") double radius) {
        List<Outlet> nearbyOutlets = onboardingService.getNearbyOutlets(lat, lng, radius);
        List<Map<String, Object>> responseList = new java.util.ArrayList<>();
        Map<UUID, Brand> brandCache = new HashMap<>();
        for (Outlet outlet : nearbyOutlets) {
            Map<String, Object> response = new HashMap<>();
            response.put("id", outlet.getId());
            response.put("name", outlet.getName());
            response.put("isActive", outlet.getIsActive());
            response.put("defaultPrepTimeSeconds", outlet.getDefaultPrepTimeSeconds() != null ? outlet.getDefaultPrepTimeSeconds() : 900);
            response.put("isOpen", true); // Filtered by native query
            double distance = 1.5;
            if (outlet.getLocation() != null) {
                response.put("lat", outlet.getLocation().getY());
                response.put("lng", outlet.getLocation().getX());
                // approximate distance
                double rEarth = 6371.0;
                double dLat = Math.toRadians(outlet.getLocation().getY() - lat);
                double dLon = Math.toRadians(outlet.getLocation().getX() - lng);
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(outlet.getLocation().getY())) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                distance = rEarth * c;
                response.put("distance", Math.round(distance * 10.0) / 10.0);
            } else {
                response.put("distance", 1.5);
            }
            // Use real DB values, fallback to dummies if null (for old records)
            response.put("image", outlet.getBannerUrl() != null ? outlet.getBannerUrl() : "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80");
            response.put("cuisine", outlet.getCuisine() != null ? outlet.getCuisine() : "Multi Cuisine");
            response.put("rating", outlet.getRating() != null ? outlet.getRating() : 0.0);
            response.put("reviewsCount", outlet.getReviewsCount() != null ? outlet.getReviewsCount() : 0);
            response.put("deliveryTime", outlet.getDeliveryTime() != null ? outlet.getDeliveryTime() : 30);
            response.put("deliveryFee", outlet.getDeliveryFee() != null ? outlet.getDeliveryFee() : 0.0);
            if (outlet.getTags() != null && !outlet.getTags().isEmpty()) {
                response.put("tags", java.util.Arrays.asList(outlet.getTags().split(",")));
            } else {
                response.put("tags", List.of());
            }
            try {
                Brand brand = brandCache.computeIfAbsent(outlet.getBrandId(), id -> onboardingService.getBrandById(id));
                response.put("logoUrl", brand.getLogoUrl());
                response.put("brandId", brand.getId());
                response.put("brandName", brand.getName());
                if (outlet.getBannerUrl() == null && brand.getLogoUrl() != null) {
                    response.put("image", brand.getLogoUrl());
                }
            } catch (Exception e) {
            }
            // Ignore missing brand
            responseList.add(response);
        }
        // Sort by distance (since DB groups by brand, we sort the final list by distance)
        responseList.sort(java.util.Comparator.comparingDouble(m -> (Double) m.get("distance")));
        return ResponseEntity.ok(ApiResponse.success(responseList, "Nearby restaurants fetched"));
    }

    @GetMapping("/api/v1/restaurants/brands/{brandId}/outlets")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getBrandOutlets(@PathVariable UUID brandId, @org.springframework.web.bind.annotation.RequestParam double lat, @org.springframework.web.bind.annotation.RequestParam double lng, @org.springframework.web.bind.annotation.RequestParam(defaultValue = "5.0") double radius) {
        List<Outlet> nearbyOutlets = onboardingService.getNearbyOutletsByBrand(brandId, lat, lng, radius);
        List<Map<String, Object>> responseList =  // Filtered by native query
        nearbyOutlets.stream().map(outlet -> {
            Map<String, Object> response = new HashMap<>();
            response.put("id", outlet.getId());
            response.put("name", outlet.getName());
            response.put("isActive", outlet.getIsActive());
            response.put("defaultPrepTimeSeconds", outlet.getDefaultPrepTimeSeconds() != null ? outlet.getDefaultPrepTimeSeconds() : 900);
            response.put("isOpen", true);
            double distance = 1.5;
            if (outlet.getLocation() != null) {
                response.put("lat", outlet.getLocation().getY());
                response.put("lng", outlet.getLocation().getX());
                double rEarth = 6371.0;
                double dLat = Math.toRadians(outlet.getLocation().getY() - lat);
                double dLon = Math.toRadians(outlet.getLocation().getX() - lng);
                double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(outlet.getLocation().getY())) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
                double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                distance = rEarth * c;
                response.put("distance", Math.round(distance * 10.0) / 10.0);
            } else {
                response.put("distance", 1.5);
            }
            response.put("image", outlet.getBannerUrl() != null ? outlet.getBannerUrl() : "https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=800&q=80");
            response.put("cuisine", outlet.getCuisine() != null ? outlet.getCuisine() : "Multi Cuisine");
            response.put("rating", outlet.getRating() != null ? outlet.getRating() : 0.0);
            response.put("reviewsCount", outlet.getReviewsCount() != null ? outlet.getReviewsCount() : 0);
            response.put("deliveryTime", outlet.getDeliveryTime() != null ? outlet.getDeliveryTime() : 30);
            response.put("deliveryFee", outlet.getDeliveryFee() != null ? outlet.getDeliveryFee() : 0.0);
            if (outlet.getTags() != null && !outlet.getTags().isEmpty()) {
                response.put("tags", java.util.Arrays.asList(outlet.getTags().split(",")));
            } else {
                response.put("tags", List.of());
            }
            try {
                Brand brand = onboardingService.getBrandById(outlet.getBrandId());
                response.put("logoUrl", brand.getLogoUrl());
                response.put("brandId", brand.getId());
                response.put("brandName", brand.getName());
                if (outlet.getBannerUrl() == null && brand.getLogoUrl() != null) {
                    response.put("image", brand.getLogoUrl());
                }
            } catch (Exception e) {
            }
            return response;
        }).sorted(java.util.Comparator.comparingDouble(m -> (Double) m.get("distance"))).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responseList, "Brand outlets fetched"));
    }

    @GetMapping("/api/v1/internal/admin/restaurants/all-with-location")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllOutletsWithLocation() {
        List<Outlet> outlets = onboardingService.getAllOutlets();
        List<Map<String, Object>> responseList = 
        // Removed dummy phone number as it is no longer required in UI
        outlets.stream().map(outlet -> {
            Map<String, Object> response = new HashMap<>();
            response.put("id", outlet.getId());
            response.put("name", outlet.getName());
            response.put("isActive", outlet.getIsActive());
            if (outlet.getLocation() != null) {
                response.put("lat", outlet.getLocation().getY());
                response.put("lng", outlet.getLocation().getX());
            } else {
                response.put("lat", 0.0);
                response.put("lng", 0.0);
            }
            return response;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(responseList, "All restaurants with locations fetched"));
    }


    public static class BrandOnboardRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String gstin;
        @NotBlank
        private String pan;
        private String cin;
        @NotBlank
        private String bankAccountNumber;
        @NotBlank
        private String ifscCode;
        private String logoUrl;

        @java.lang.SuppressWarnings("all")
        public BrandOnboardRequest() {
        }

        @java.lang.SuppressWarnings("all")
        public String getName() {
            return this.name;
        }

        @java.lang.SuppressWarnings("all")
        public String getGstin() {
            return this.gstin;
        }

        @java.lang.SuppressWarnings("all")
        public String getPan() {
            return this.pan;
        }

        @java.lang.SuppressWarnings("all")
        public String getCin() {
            return this.cin;
        }

        @java.lang.SuppressWarnings("all")
        public String getBankAccountNumber() {
            return this.bankAccountNumber;
        }

        @java.lang.SuppressWarnings("all")
        public String getIfscCode() {
            return this.ifscCode;
        }

        @java.lang.SuppressWarnings("all")
        public String getLogoUrl() {
            return this.logoUrl;
        }

        @java.lang.SuppressWarnings("all")
        public void setName(final String name) {
            this.name = name;
        }

        @java.lang.SuppressWarnings("all")
        public void setGstin(final String gstin) {
            this.gstin = gstin;
        }

        @java.lang.SuppressWarnings("all")
        public void setPan(final String pan) {
            this.pan = pan;
        }

        @java.lang.SuppressWarnings("all")
        public void setCin(final String cin) {
            this.cin = cin;
        }

        @java.lang.SuppressWarnings("all")
        public void setBankAccountNumber(final String bankAccountNumber) {
            this.bankAccountNumber = bankAccountNumber;
        }

        @java.lang.SuppressWarnings("all")
        public void setIfscCode(final String ifscCode) {
            this.ifscCode = ifscCode;
        }

        @java.lang.SuppressWarnings("all")
        public void setLogoUrl(final String logoUrl) {
            this.logoUrl = logoUrl;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof RestaurantOnboardingController.BrandOnboardRequest)) return false;
            final RestaurantOnboardingController.BrandOnboardRequest other = (RestaurantOnboardingController.BrandOnboardRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$name = this.getName();
            final java.lang.Object other$name = other.getName();
            if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
            final java.lang.Object this$gstin = this.getGstin();
            final java.lang.Object other$gstin = other.getGstin();
            if (this$gstin == null ? other$gstin != null : !this$gstin.equals(other$gstin)) return false;
            final java.lang.Object this$pan = this.getPan();
            final java.lang.Object other$pan = other.getPan();
            if (this$pan == null ? other$pan != null : !this$pan.equals(other$pan)) return false;
            final java.lang.Object this$cin = this.getCin();
            final java.lang.Object other$cin = other.getCin();
            if (this$cin == null ? other$cin != null : !this$cin.equals(other$cin)) return false;
            final java.lang.Object this$bankAccountNumber = this.getBankAccountNumber();
            final java.lang.Object other$bankAccountNumber = other.getBankAccountNumber();
            if (this$bankAccountNumber == null ? other$bankAccountNumber != null : !this$bankAccountNumber.equals(other$bankAccountNumber)) return false;
            final java.lang.Object this$ifscCode = this.getIfscCode();
            final java.lang.Object other$ifscCode = other.getIfscCode();
            if (this$ifscCode == null ? other$ifscCode != null : !this$ifscCode.equals(other$ifscCode)) return false;
            final java.lang.Object this$logoUrl = this.getLogoUrl();
            final java.lang.Object other$logoUrl = other.getLogoUrl();
            if (this$logoUrl == null ? other$logoUrl != null : !this$logoUrl.equals(other$logoUrl)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof RestaurantOnboardingController.BrandOnboardRequest;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $name = this.getName();
            result = result * PRIME + ($name == null ? 43 : $name.hashCode());
            final java.lang.Object $gstin = this.getGstin();
            result = result * PRIME + ($gstin == null ? 43 : $gstin.hashCode());
            final java.lang.Object $pan = this.getPan();
            result = result * PRIME + ($pan == null ? 43 : $pan.hashCode());
            final java.lang.Object $cin = this.getCin();
            result = result * PRIME + ($cin == null ? 43 : $cin.hashCode());
            final java.lang.Object $bankAccountNumber = this.getBankAccountNumber();
            result = result * PRIME + ($bankAccountNumber == null ? 43 : $bankAccountNumber.hashCode());
            final java.lang.Object $ifscCode = this.getIfscCode();
            result = result * PRIME + ($ifscCode == null ? 43 : $ifscCode.hashCode());
            final java.lang.Object $logoUrl = this.getLogoUrl();
            result = result * PRIME + ($logoUrl == null ? 43 : $logoUrl.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "RestaurantOnboardingController.BrandOnboardRequest(name=" + this.getName() + ", gstin=" + this.getGstin() + ", pan=" + this.getPan() + ", cin=" + this.getCin() + ", bankAccountNumber=" + this.getBankAccountNumber() + ", ifscCode=" + this.getIfscCode() + ", logoUrl=" + this.getLogoUrl() + ")";
        }
    }


    public static class OutletOnboardRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String fssaiLicenseNumber;
        @NotNull
        private Double lat;
        @NotNull
        private Double lng;
        @NotNull
        private List<TimingRequest> timings;
        private String bannerUrl;
        private String cuisine;
        private Double rating;
        private Integer reviewsCount;
        private Integer deliveryTime;
        private Double deliveryFee;
        private String tags;

        @java.lang.SuppressWarnings("all")
        public OutletOnboardRequest() {
        }

        @java.lang.SuppressWarnings("all")
        public String getName() {
            return this.name;
        }

        @java.lang.SuppressWarnings("all")
        public String getFssaiLicenseNumber() {
            return this.fssaiLicenseNumber;
        }

        @java.lang.SuppressWarnings("all")
        public Double getLat() {
            return this.lat;
        }

        @java.lang.SuppressWarnings("all")
        public Double getLng() {
            return this.lng;
        }

        @java.lang.SuppressWarnings("all")
        public List<TimingRequest> getTimings() {
            return this.timings;
        }

        @java.lang.SuppressWarnings("all")
        public String getBannerUrl() {
            return this.bannerUrl;
        }

        @java.lang.SuppressWarnings("all")
        public String getCuisine() {
            return this.cuisine;
        }

        @java.lang.SuppressWarnings("all")
        public Double getRating() {
            return this.rating;
        }

        @java.lang.SuppressWarnings("all")
        public Integer getReviewsCount() {
            return this.reviewsCount;
        }

        @java.lang.SuppressWarnings("all")
        public Integer getDeliveryTime() {
            return this.deliveryTime;
        }

        @java.lang.SuppressWarnings("all")
        public Double getDeliveryFee() {
            return this.deliveryFee;
        }

        @java.lang.SuppressWarnings("all")
        public String getTags() {
            return this.tags;
        }

        @java.lang.SuppressWarnings("all")
        public void setName(final String name) {
            this.name = name;
        }

        @java.lang.SuppressWarnings("all")
        public void setFssaiLicenseNumber(final String fssaiLicenseNumber) {
            this.fssaiLicenseNumber = fssaiLicenseNumber;
        }

        @java.lang.SuppressWarnings("all")
        public void setLat(final Double lat) {
            this.lat = lat;
        }

        @java.lang.SuppressWarnings("all")
        public void setLng(final Double lng) {
            this.lng = lng;
        }

        @java.lang.SuppressWarnings("all")
        public void setTimings(final List<TimingRequest> timings) {
            this.timings = timings;
        }

        @java.lang.SuppressWarnings("all")
        public void setBannerUrl(final String bannerUrl) {
            this.bannerUrl = bannerUrl;
        }

        @java.lang.SuppressWarnings("all")
        public void setCuisine(final String cuisine) {
            this.cuisine = cuisine;
        }

        @java.lang.SuppressWarnings("all")
        public void setRating(final Double rating) {
            this.rating = rating;
        }

        @java.lang.SuppressWarnings("all")
        public void setReviewsCount(final Integer reviewsCount) {
            this.reviewsCount = reviewsCount;
        }

        @java.lang.SuppressWarnings("all")
        public void setDeliveryTime(final Integer deliveryTime) {
            this.deliveryTime = deliveryTime;
        }

        @java.lang.SuppressWarnings("all")
        public void setDeliveryFee(final Double deliveryFee) {
            this.deliveryFee = deliveryFee;
        }

        @java.lang.SuppressWarnings("all")
        public void setTags(final String tags) {
            this.tags = tags;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof RestaurantOnboardingController.OutletOnboardRequest)) return false;
            final RestaurantOnboardingController.OutletOnboardRequest other = (RestaurantOnboardingController.OutletOnboardRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$lat = this.getLat();
            final java.lang.Object other$lat = other.getLat();
            if (this$lat == null ? other$lat != null : !this$lat.equals(other$lat)) return false;
            final java.lang.Object this$lng = this.getLng();
            final java.lang.Object other$lng = other.getLng();
            if (this$lng == null ? other$lng != null : !this$lng.equals(other$lng)) return false;
            final java.lang.Object this$rating = this.getRating();
            final java.lang.Object other$rating = other.getRating();
            if (this$rating == null ? other$rating != null : !this$rating.equals(other$rating)) return false;
            final java.lang.Object this$reviewsCount = this.getReviewsCount();
            final java.lang.Object other$reviewsCount = other.getReviewsCount();
            if (this$reviewsCount == null ? other$reviewsCount != null : !this$reviewsCount.equals(other$reviewsCount)) return false;
            final java.lang.Object this$deliveryTime = this.getDeliveryTime();
            final java.lang.Object other$deliveryTime = other.getDeliveryTime();
            if (this$deliveryTime == null ? other$deliveryTime != null : !this$deliveryTime.equals(other$deliveryTime)) return false;
            final java.lang.Object this$deliveryFee = this.getDeliveryFee();
            final java.lang.Object other$deliveryFee = other.getDeliveryFee();
            if (this$deliveryFee == null ? other$deliveryFee != null : !this$deliveryFee.equals(other$deliveryFee)) return false;
            final java.lang.Object this$name = this.getName();
            final java.lang.Object other$name = other.getName();
            if (this$name == null ? other$name != null : !this$name.equals(other$name)) return false;
            final java.lang.Object this$fssaiLicenseNumber = this.getFssaiLicenseNumber();
            final java.lang.Object other$fssaiLicenseNumber = other.getFssaiLicenseNumber();
            if (this$fssaiLicenseNumber == null ? other$fssaiLicenseNumber != null : !this$fssaiLicenseNumber.equals(other$fssaiLicenseNumber)) return false;
            final java.lang.Object this$timings = this.getTimings();
            final java.lang.Object other$timings = other.getTimings();
            if (this$timings == null ? other$timings != null : !this$timings.equals(other$timings)) return false;
            final java.lang.Object this$bannerUrl = this.getBannerUrl();
            final java.lang.Object other$bannerUrl = other.getBannerUrl();
            if (this$bannerUrl == null ? other$bannerUrl != null : !this$bannerUrl.equals(other$bannerUrl)) return false;
            final java.lang.Object this$cuisine = this.getCuisine();
            final java.lang.Object other$cuisine = other.getCuisine();
            if (this$cuisine == null ? other$cuisine != null : !this$cuisine.equals(other$cuisine)) return false;
            final java.lang.Object this$tags = this.getTags();
            final java.lang.Object other$tags = other.getTags();
            if (this$tags == null ? other$tags != null : !this$tags.equals(other$tags)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof RestaurantOnboardingController.OutletOnboardRequest;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $lat = this.getLat();
            result = result * PRIME + ($lat == null ? 43 : $lat.hashCode());
            final java.lang.Object $lng = this.getLng();
            result = result * PRIME + ($lng == null ? 43 : $lng.hashCode());
            final java.lang.Object $rating = this.getRating();
            result = result * PRIME + ($rating == null ? 43 : $rating.hashCode());
            final java.lang.Object $reviewsCount = this.getReviewsCount();
            result = result * PRIME + ($reviewsCount == null ? 43 : $reviewsCount.hashCode());
            final java.lang.Object $deliveryTime = this.getDeliveryTime();
            result = result * PRIME + ($deliveryTime == null ? 43 : $deliveryTime.hashCode());
            final java.lang.Object $deliveryFee = this.getDeliveryFee();
            result = result * PRIME + ($deliveryFee == null ? 43 : $deliveryFee.hashCode());
            final java.lang.Object $name = this.getName();
            result = result * PRIME + ($name == null ? 43 : $name.hashCode());
            final java.lang.Object $fssaiLicenseNumber = this.getFssaiLicenseNumber();
            result = result * PRIME + ($fssaiLicenseNumber == null ? 43 : $fssaiLicenseNumber.hashCode());
            final java.lang.Object $timings = this.getTimings();
            result = result * PRIME + ($timings == null ? 43 : $timings.hashCode());
            final java.lang.Object $bannerUrl = this.getBannerUrl();
            result = result * PRIME + ($bannerUrl == null ? 43 : $bannerUrl.hashCode());
            final java.lang.Object $cuisine = this.getCuisine();
            result = result * PRIME + ($cuisine == null ? 43 : $cuisine.hashCode());
            final java.lang.Object $tags = this.getTags();
            result = result * PRIME + ($tags == null ? 43 : $tags.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "RestaurantOnboardingController.OutletOnboardRequest(name=" + this.getName() + ", fssaiLicenseNumber=" + this.getFssaiLicenseNumber() + ", lat=" + this.getLat() + ", lng=" + this.getLng() + ", timings=" + this.getTimings() + ", bannerUrl=" + this.getBannerUrl() + ", cuisine=" + this.getCuisine() + ", rating=" + this.getRating() + ", reviewsCount=" + this.getReviewsCount() + ", deliveryTime=" + this.getDeliveryTime() + ", deliveryFee=" + this.getDeliveryFee() + ", tags=" + this.getTags() + ")";
        }
    }


    public static class OutletStatusUpdateRequest {
        @NotNull
        @com.fasterxml.jackson.annotation.JsonProperty("isActive")
        private Boolean isActive;

        @java.lang.SuppressWarnings("all")
        public OutletStatusUpdateRequest() {
        }

        @java.lang.SuppressWarnings("all")
        public Boolean getIsActive() {
            return this.isActive;
        }

        @com.fasterxml.jackson.annotation.JsonProperty("isActive")
        @java.lang.SuppressWarnings("all")
        public void setIsActive(final Boolean isActive) {
            this.isActive = isActive;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof RestaurantOnboardingController.OutletStatusUpdateRequest)) return false;
            final RestaurantOnboardingController.OutletStatusUpdateRequest other = (RestaurantOnboardingController.OutletStatusUpdateRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$isActive = this.getIsActive();
            final java.lang.Object other$isActive = other.getIsActive();
            if (this$isActive == null ? other$isActive != null : !this$isActive.equals(other$isActive)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof RestaurantOnboardingController.OutletStatusUpdateRequest;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $isActive = this.getIsActive();
            result = result * PRIME + ($isActive == null ? 43 : $isActive.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "RestaurantOnboardingController.OutletStatusUpdateRequest(isActive=" + this.getIsActive() + ")";
        }
    }


    public static class OutletSettingsUpdateRequest {
        @NotNull
        private Integer defaultPrepTimeSeconds;

        @java.lang.SuppressWarnings("all")
        public OutletSettingsUpdateRequest() {
        }

        @java.lang.SuppressWarnings("all")
        public Integer getDefaultPrepTimeSeconds() {
            return this.defaultPrepTimeSeconds;
        }

        @java.lang.SuppressWarnings("all")
        public void setDefaultPrepTimeSeconds(final Integer defaultPrepTimeSeconds) {
            this.defaultPrepTimeSeconds = defaultPrepTimeSeconds;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof RestaurantOnboardingController.OutletSettingsUpdateRequest)) return false;
            final RestaurantOnboardingController.OutletSettingsUpdateRequest other = (RestaurantOnboardingController.OutletSettingsUpdateRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$defaultPrepTimeSeconds = this.getDefaultPrepTimeSeconds();
            final java.lang.Object other$defaultPrepTimeSeconds = other.getDefaultPrepTimeSeconds();
            if (this$defaultPrepTimeSeconds == null ? other$defaultPrepTimeSeconds != null : !this$defaultPrepTimeSeconds.equals(other$defaultPrepTimeSeconds)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof RestaurantOnboardingController.OutletSettingsUpdateRequest;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $defaultPrepTimeSeconds = this.getDefaultPrepTimeSeconds();
            result = result * PRIME + ($defaultPrepTimeSeconds == null ? 43 : $defaultPrepTimeSeconds.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "RestaurantOnboardingController.OutletSettingsUpdateRequest(defaultPrepTimeSeconds=" + this.getDefaultPrepTimeSeconds() + ")";
        }
    }


    public static class TimingRequest {
        @NotNull
        private LocalTime openingTime;
        @NotNull
        private LocalTime closingTime;

        @java.lang.SuppressWarnings("all")
        public TimingRequest() {
        }

        @java.lang.SuppressWarnings("all")
        public LocalTime getOpeningTime() {
            return this.openingTime;
        }

        @java.lang.SuppressWarnings("all")
        public LocalTime getClosingTime() {
            return this.closingTime;
        }

        @java.lang.SuppressWarnings("all")
        public void setOpeningTime(final LocalTime openingTime) {
            this.openingTime = openingTime;
        }

        @java.lang.SuppressWarnings("all")
        public void setClosingTime(final LocalTime closingTime) {
            this.closingTime = closingTime;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof RestaurantOnboardingController.TimingRequest)) return false;
            final RestaurantOnboardingController.TimingRequest other = (RestaurantOnboardingController.TimingRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$openingTime = this.getOpeningTime();
            final java.lang.Object other$openingTime = other.getOpeningTime();
            if (this$openingTime == null ? other$openingTime != null : !this$openingTime.equals(other$openingTime)) return false;
            final java.lang.Object this$closingTime = this.getClosingTime();
            final java.lang.Object other$closingTime = other.getClosingTime();
            if (this$closingTime == null ? other$closingTime != null : !this$closingTime.equals(other$closingTime)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof RestaurantOnboardingController.TimingRequest;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $openingTime = this.getOpeningTime();
            result = result * PRIME + ($openingTime == null ? 43 : $openingTime.hashCode());
            final java.lang.Object $closingTime = this.getClosingTime();
            result = result * PRIME + ($closingTime == null ? 43 : $closingTime.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "RestaurantOnboardingController.TimingRequest(openingTime=" + this.getOpeningTime() + ", closingTime=" + this.getClosingTime() + ")";
        }
    }


    public static class OutletTimingsUpdateRequest {
        @NotNull
        private List<TimingRequest> timings;

        @java.lang.SuppressWarnings("all")
        public OutletTimingsUpdateRequest() {
        }

        @java.lang.SuppressWarnings("all")
        public List<TimingRequest> getTimings() {
            return this.timings;
        }

        @java.lang.SuppressWarnings("all")
        public void setTimings(final List<TimingRequest> timings) {
            this.timings = timings;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof RestaurantOnboardingController.OutletTimingsUpdateRequest)) return false;
            final RestaurantOnboardingController.OutletTimingsUpdateRequest other = (RestaurantOnboardingController.OutletTimingsUpdateRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$timings = this.getTimings();
            final java.lang.Object other$timings = other.getTimings();
            if (this$timings == null ? other$timings != null : !this$timings.equals(other$timings)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof RestaurantOnboardingController.OutletTimingsUpdateRequest;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $timings = this.getTimings();
            result = result * PRIME + ($timings == null ? 43 : $timings.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "RestaurantOnboardingController.OutletTimingsUpdateRequest(timings=" + this.getTimings() + ")";
        }
    }


    public static class VerificationCallbackRequest {
        private String verificationType;
        private String status;
        private String legalEntityName;
        private String bankBeneficiaryName;
        private Double matchScore;

        @java.lang.SuppressWarnings("all")
        public VerificationCallbackRequest() {
        }

        @java.lang.SuppressWarnings("all")
        public String getVerificationType() {
            return this.verificationType;
        }

        @java.lang.SuppressWarnings("all")
        public String getStatus() {
            return this.status;
        }

        @java.lang.SuppressWarnings("all")
        public String getLegalEntityName() {
            return this.legalEntityName;
        }

        @java.lang.SuppressWarnings("all")
        public String getBankBeneficiaryName() {
            return this.bankBeneficiaryName;
        }

        @java.lang.SuppressWarnings("all")
        public Double getMatchScore() {
            return this.matchScore;
        }

        @java.lang.SuppressWarnings("all")
        public void setVerificationType(final String verificationType) {
            this.verificationType = verificationType;
        }

        @java.lang.SuppressWarnings("all")
        public void setStatus(final String status) {
            this.status = status;
        }

        @java.lang.SuppressWarnings("all")
        public void setLegalEntityName(final String legalEntityName) {
            this.legalEntityName = legalEntityName;
        }

        @java.lang.SuppressWarnings("all")
        public void setBankBeneficiaryName(final String bankBeneficiaryName) {
            this.bankBeneficiaryName = bankBeneficiaryName;
        }

        @java.lang.SuppressWarnings("all")
        public void setMatchScore(final Double matchScore) {
            this.matchScore = matchScore;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public boolean equals(final java.lang.Object o) {
            if (o == this) return true;
            if (!(o instanceof RestaurantOnboardingController.VerificationCallbackRequest)) return false;
            final RestaurantOnboardingController.VerificationCallbackRequest other = (RestaurantOnboardingController.VerificationCallbackRequest) o;
            if (!other.canEqual((java.lang.Object) this)) return false;
            final java.lang.Object this$matchScore = this.getMatchScore();
            final java.lang.Object other$matchScore = other.getMatchScore();
            if (this$matchScore == null ? other$matchScore != null : !this$matchScore.equals(other$matchScore)) return false;
            final java.lang.Object this$verificationType = this.getVerificationType();
            final java.lang.Object other$verificationType = other.getVerificationType();
            if (this$verificationType == null ? other$verificationType != null : !this$verificationType.equals(other$verificationType)) return false;
            final java.lang.Object this$status = this.getStatus();
            final java.lang.Object other$status = other.getStatus();
            if (this$status == null ? other$status != null : !this$status.equals(other$status)) return false;
            final java.lang.Object this$legalEntityName = this.getLegalEntityName();
            final java.lang.Object other$legalEntityName = other.getLegalEntityName();
            if (this$legalEntityName == null ? other$legalEntityName != null : !this$legalEntityName.equals(other$legalEntityName)) return false;
            final java.lang.Object this$bankBeneficiaryName = this.getBankBeneficiaryName();
            final java.lang.Object other$bankBeneficiaryName = other.getBankBeneficiaryName();
            if (this$bankBeneficiaryName == null ? other$bankBeneficiaryName != null : !this$bankBeneficiaryName.equals(other$bankBeneficiaryName)) return false;
            return true;
        }

        @java.lang.SuppressWarnings("all")
        protected boolean canEqual(final java.lang.Object other) {
            return other instanceof RestaurantOnboardingController.VerificationCallbackRequest;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final java.lang.Object $matchScore = this.getMatchScore();
            result = result * PRIME + ($matchScore == null ? 43 : $matchScore.hashCode());
            final java.lang.Object $verificationType = this.getVerificationType();
            result = result * PRIME + ($verificationType == null ? 43 : $verificationType.hashCode());
            final java.lang.Object $status = this.getStatus();
            result = result * PRIME + ($status == null ? 43 : $status.hashCode());
            final java.lang.Object $legalEntityName = this.getLegalEntityName();
            result = result * PRIME + ($legalEntityName == null ? 43 : $legalEntityName.hashCode());
            final java.lang.Object $bankBeneficiaryName = this.getBankBeneficiaryName();
            result = result * PRIME + ($bankBeneficiaryName == null ? 43 : $bankBeneficiaryName.hashCode());
            return result;
        }

        @java.lang.Override
        @java.lang.SuppressWarnings("all")
        public java.lang.String toString() {
            return "RestaurantOnboardingController.VerificationCallbackRequest(verificationType=" + this.getVerificationType() + ", status=" + this.getStatus() + ", legalEntityName=" + this.getLegalEntityName() + ", bankBeneficiaryName=" + this.getBankBeneficiaryName() + ", matchScore=" + this.getMatchScore() + ")";
        }
    }

    @java.lang.SuppressWarnings("all")
    public RestaurantOnboardingController(final RestaurantOnboardingService onboardingService, final com.fooddelivery.restaurant.security.RestaurantSecurityHelper securityHelper, final com.fooddelivery.restaurant.client.GovernmentIdClient governmentIdClient) {
        this.onboardingService = onboardingService;
        this.securityHelper = securityHelper;
        this.governmentIdClient = governmentIdClient;
    }
}
