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

class InternalRestaurantOutletSummaryTest {

    private final OutletRepository outlets = mock(OutletRepository.class);
    private final BrandRepository brands = mock(BrandRepository.class);
    private final InternalRestaurantController controller =
            new InternalRestaurantController(outlets, mock(MasterMenuItemRepository.class), brands);

    /** CustomerApplication sums the outlet's earnings day, week and month in this zone. */
    @Test
    void carriesTheOutletsOwnTimeZone() {
        UUID outletId = UUID.randomUUID();
        UUID brandId = UUID.randomUUID();
        when(outlets.findById(outletId)).thenReturn(Optional.of(Outlet.builder().id(outletId).brandId(brandId)
                .name("Paradise Jersey City").timeZone(java.time.ZoneId.of("America/New_York")).build()));
        Brand brand = new Brand();
        brand.setId(brandId);
        brand.setName("Paradise");
        when(brands.findById(brandId)).thenReturn(Optional.of(brand));

        ResponseEntity<Map<String, String>> res = controller.getOutletSummary(outletId);

        assertEquals("America/New_York", res.getBody().get("timeZone"));
        assertEquals("Paradise Jersey City", res.getBody().get("name"));
        assertEquals("Paradise", res.getBody().get("brandName"));
        assertEquals(outletId.toString(), res.getBody().get("id"));
    }

    @Test
    void unknownOutletIs404() {
        UUID outletId = UUID.randomUUID();
        when(outlets.findById(outletId)).thenReturn(Optional.empty());
        assertEquals(404, controller.getOutletSummary(outletId).getStatusCode().value());
    }
}
