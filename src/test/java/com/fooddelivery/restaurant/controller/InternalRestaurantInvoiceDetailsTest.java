package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.BrandRepository;
import com.fooddelivery.restaurant.repository.MasterMenuItemRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InternalRestaurantInvoiceDetailsTest {

    private final OutletRepository outlets = mock(OutletRepository.class);
    private final BrandRepository brands = mock(BrandRepository.class);
    private final InternalRestaurantController controller =
            new InternalRestaurantController(outlets, mock(MasterMenuItemRepository.class), brands);

    @Test
    void namesTheLegalEntityGstinAndFssai() {
        UUID outletId = UUID.randomUUID();
        UUID brandId = UUID.randomUUID();
        when(outlets.findById(outletId)).thenReturn(Optional.of(Outlet.builder().id(outletId).brandId(brandId)
                .name("Paradise Koramangala").fssaiLicenseNumber("11224333000123").build()));
        Brand brand = new Brand();
        brand.setId(brandId);
        brand.setName("Paradise");
        brand.setLegalEntityName("Paradise Food Court Pvt Ltd");
        brand.setGstin("29ABCDE1234F1Z5");
        brand.setIsGstinVerified(true);
        when(brands.findById(brandId)).thenReturn(Optional.of(brand));

        ResponseEntity<Map<String, Object>> res = controller.getInvoiceDetails(outletId);

        assertEquals("Paradise Food Court Pvt Ltd", res.getBody().get("legalEntityName"));
        assertEquals("29ABCDE1234F1Z5", res.getBody().get("gstin"));
        assertEquals(true, res.getBody().get("gstinVerified"));
        assertEquals("11224333000123", res.getBody().get("fssaiLicenseNumber"));
        assertEquals("Paradise Koramangala", res.getBody().get("outletName"));
    }

    @Test
    void unknownOutletIs404() {
        UUID outletId = UUID.randomUUID();
        when(outlets.findById(outletId)).thenReturn(Optional.empty());
        assertEquals(404, controller.getInvoiceDetails(outletId).getStatusCode().value());
    }
}
