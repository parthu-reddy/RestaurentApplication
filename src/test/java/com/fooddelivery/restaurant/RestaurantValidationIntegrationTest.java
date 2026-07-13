package com.fooddelivery.restaurant;

import com.fooddelivery.common.test.BaseIntegrationTest;
import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.BrandRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class RestaurantValidationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private OutletRepository outletRepository;

    @Test
    void shouldThrowExceptionWhenDuplicateGstinIsSaved() {
        String duplicateGstin = "22AAAAA0000A1Z5";
        
        Brand brand1 = Brand.builder()
                .id(UUID.randomUUID())
                .name("Brand One")
                .gstin(duplicateGstin)
                .pan("AAAAA0000A")
                .cin("U12345MH2023PTC123456")
                .bankAccountNumber("123456789")
                .bankIfsc("HDFC0001234")
                .isGstinVerified(true)
                .isBankVerified(true)
                .build();
        brandRepository.save(brand1);

        Brand brand2 = Brand.builder()
                .id(UUID.randomUUID())
                .name("Brand Two")
                .gstin(duplicateGstin) // Duplicate GSTIN
                .pan("BBBBB1111B")
                .cin("U12345MH2023PTC654321")
                .bankAccountNumber("987654321")
                .bankIfsc("SBIN0001234")
                .isGstinVerified(true)
                .isBankVerified(true)
                .build();

        assertThatThrownBy(() -> brandRepository.save(brand2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
    
    @Test
    void shouldThrowExceptionWhenDuplicatePanIsSaved() {
        String duplicatePan = "CCCCC2222C";
        
        Brand brand1 = Brand.builder()
                .id(UUID.randomUUID())
                .name("Brand One")
                .gstin("33AAAAA0000A1Z5")
                .pan(duplicatePan)
                .cin("U12345MH2023PTC123456")
                .bankAccountNumber("123456789")
                .bankIfsc("HDFC0001234")
                .isGstinVerified(true)
                .isBankVerified(true)
                .build();
        brandRepository.save(brand1);

        Brand brand2 = Brand.builder()
                .id(UUID.randomUUID())
                .name("Brand Two")
                .gstin("44BBBBB1111B1Z5")
                .pan(duplicatePan) // Duplicate PAN
                .cin("U12345MH2023PTC654321")
                .bankAccountNumber("987654321")
                .bankIfsc("SBIN0001234")
                .isGstinVerified(true)
                .isBankVerified(true)
                .build();

        assertThatThrownBy(() -> brandRepository.save(brand2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
    
    @Test
    void shouldThrowExceptionWhenDuplicateFssaiIsSaved() {
        String duplicateFssai = "12345678901234";
        
        Outlet outlet1 = Outlet.builder()
                .id(UUID.randomUUID())
                .brandId(UUID.randomUUID())
                .name("Outlet One")
                .fssaiLicenseNumber(duplicateFssai)
                .build();
        outletRepository.save(outlet1);

        Outlet outlet2 = Outlet.builder()
                .id(UUID.randomUUID())
                .brandId(UUID.randomUUID())
                .name("Outlet Two")
                .fssaiLicenseNumber(duplicateFssai) // Duplicate FSSAI
                .build();

        assertThatThrownBy(() -> outletRepository.save(outlet2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }
}
