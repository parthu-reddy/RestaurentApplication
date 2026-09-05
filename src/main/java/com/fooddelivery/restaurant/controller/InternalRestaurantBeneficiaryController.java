package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.dto.ledger.BeneficiaryResponse;
import com.fooddelivery.common.enums.VerificationStatus;
import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/internal/restaurants")
@RequiredArgsConstructor
public class InternalRestaurantBeneficiaryController {

    private final BrandRepository brandRepository;

    @PreAuthorize("hasAnyRole('SERVICE', 'ADMIN')")
    @GetMapping("/{restaurantId}/beneficiary")
    public ResponseEntity<BeneficiaryResponse> getBeneficiary(@PathVariable UUID restaurantId) {
        return brandRepository.findById(restaurantId)
                .map(brand -> {
                    String account = brand.getBankAccountNumber();
                    String masked = (account != null && account.length() >= 4) 
                            ? "XXXX-XXXX-" + account.substring(account.length() - 4) 
                            : "XXXX";
                            
                    BeneficiaryResponse response = BeneficiaryResponse.builder()
                            .beneficiaryName(brand.getBankBeneficiaryName() != null ? brand.getBankBeneficiaryName() : brand.getName())
                            .accountNumberMasked(masked)
                            .ifsc(brand.getBankIfsc() != null ? brand.getBankIfsc() : "BANK001")
                            .verified(brand.getPennyDropStatus() == VerificationStatus.VERIFIED)
                            .source("RESTAURANT")
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
