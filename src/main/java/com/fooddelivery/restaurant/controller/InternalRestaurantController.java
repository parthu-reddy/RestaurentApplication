package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ApiResponse;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.MasterMenuItemRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/internal/restaurants")
@lombok.extern.slf4j.Slf4j
@PreAuthorize("hasAnyRole('SERVICE', 'RESTAURANT', 'ADMIN')")
@lombok.RequiredArgsConstructor
public class InternalRestaurantController {
    private final OutletRepository outletRepository;
    private final MasterMenuItemRepository masterMenuItemRepository;
    private final com.fooddelivery.restaurant.repository.BrandRepository brandRepository;

    @GetMapping("/owner/{ownerId}/outlets")
    public ResponseEntity<List<String>> getOwnerOutlets(@PathVariable UUID ownerId) {
        List<String> outletIds = outletRepository.findByOwnerId(ownerId).stream().map(outlet -> outlet.getId().toString()).collect(Collectors.toList());
        return ResponseEntity.ok(outletIds);
    }

    @GetMapping("/outlets/{outletId}/owner")
    public ResponseEntity<?> getOutletOwner(@PathVariable String outletId) {
        return outletRepository.findById(UUID.fromString(outletId))
                .flatMap(outlet -> brandRepository.findById(outlet.getBrandId()))
                .<ResponseEntity<?>>map(brand -> ResponseEntity.ok(java.util.Map.of("ownerId", brand.getOwnerId().toString())))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/outlets/{outletId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> outletExists(@PathVariable String outletId) {
        boolean exists = outletRepository.existsById(UUID.fromString(outletId));
        return ResponseEntity.ok(ApiResponse.success(exists, "Outlet existence check completed"));
    }

    @GetMapping("/products/{productId}/exists")
    public ResponseEntity<ApiResponse<Boolean>> productExists(@PathVariable String productId) {
        boolean exists = masterMenuItemRepository.existsById(UUID.fromString(productId));
        return ResponseEntity.ok(ApiResponse.success(exists, "Product existence check completed"));
    }

    @GetMapping("/outlets/{outletId}/summary")
    public ResponseEntity<Map<String, String>> getOutletSummary(@PathVariable UUID outletId) {
        return outletRepository.findById(outletId)
                .map(outlet -> {
                    String brandName = brandRepository.findById(outlet.getBrandId())
                            .map(com.fooddelivery.restaurant.entity.Brand::getName)
                            .orElse("Unknown Brand");
                    return ResponseEntity.ok(Map.of(
                            "id", outlet.getId().toString(),
                            "name", outlet.getName(),
                            "brandName", brandName
                    ));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * What a customer's tax invoice names as the supplier: the legal entity behind the outlet,
     * its GSTIN (with whether it was verified) and the outlet's FSSAI licence. CustomerApplication
     * snapshots this once, when it issues the invoice, so a later edit here never changes an
     * invoice already issued.
     */
    @GetMapping("/outlets/{outletId}/invoice-details")
    public ResponseEntity<Map<String, Object>> getInvoiceDetails(@PathVariable UUID outletId) {
        return outletRepository.findById(outletId)
                .flatMap(outlet -> brandRepository.findById(outlet.getBrandId()).map(brand -> {
                    Map<String, Object> details = new java.util.LinkedHashMap<>();
                    details.put("outletName", outlet.getName());
                    details.put("brandName", brand.getName());
                    details.put("legalEntityName", brand.getLegalEntityName() != null ? brand.getLegalEntityName() : brand.getName());
                    details.put("gstin", brand.getGstin());
                    details.put("gstinVerified", Boolean.TRUE.equals(brand.getIsGstinVerified()));
                    details.put("fssaiLicenseNumber", outlet.getFssaiLicenseNumber());
                    return ResponseEntity.ok(details);
                }))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/outlets/summaries")
    public ResponseEntity<List<Map<String, String>>> getOutletSummaries(@RequestBody List<UUID> outletIds) {
        List<Map<String, String>> summaries = outletRepository.findAllById(outletIds).stream()
                .map(outlet -> {
                    String brandName = brandRepository.findById(outlet.getBrandId())
                            .map(com.fooddelivery.restaurant.entity.Brand::getName)
                            .orElse("Unknown Brand");
                    return Map.of(
                            "id", outlet.getId().toString(),
                            "name", outlet.getName(),
                            "brandName", brandName
                    );
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(summaries);
    }

}
