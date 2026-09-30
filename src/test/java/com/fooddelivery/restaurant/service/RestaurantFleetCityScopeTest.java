package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.common.outbox.repository.OutboxEventRepository;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.BrandRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class RestaurantFleetCityScopeTest {

    @Test
    void pagesOnlyOutletsInTheRequestedFleetCity() {
        OutletRepository outlets = mock(OutletRepository.class);
        Outlet outlet = Outlet.builder().id(UUID.randomUUID()).cityId("BLR").build();
        when(outlets.findByCityId(eq("BLR"), any())).thenReturn(new PageImpl<>(List.of(outlet), PageRequest.of(0, 100), 1));
        RestaurantOnboardingService service = service(outlets);

        var page = service.getAllOutletsForFleetCity("BLR", PageRequest.of(0, 100));

        assertThat(page.getContent()).extracting(Outlet::getCityId).containsExactly("BLR");
        verify(outlets).findByCityId(eq("BLR"), any());
    }

    @Test
    void rejectsAnUnconfiguredOrMalformedFleetCityBeforeQuerying() {
        OutletRepository outlets = mock(OutletRepository.class);
        RestaurantOnboardingService service = service(outlets);

        assertThatThrownBy(() -> service.getAllOutletsForFleetCity("NYC", PageRequest.of(0, 100)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("not an enabled");
        assertThatThrownBy(() -> service.getAllOutletsForFleetCity("BLR/redis", PageRequest.of(0, 100)))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("canonical");
        verifyNoInteractions(outlets);
    }

    @Test
    void resolvesTheOnlyConfiguredCityForALegacyMapCall() {
        OutletRepository outlets = mock(OutletRepository.class);
        when(outlets.findByCityId(eq("BLR"), any())).thenReturn(org.springframework.data.domain.Page.empty());
        RestaurantOnboardingService service = service(outlets);

        service.getAllOutletsForFleetCity(null, PageRequest.of(0, 100));

        verify(outlets).findByCityId(eq("BLR"), any());
    }

    private RestaurantOnboardingService service(OutletRepository outlets) {
        return new RestaurantOnboardingService(mock(BrandRepository.class), outlets,
                mock(OutboxEventRepository.class), new ObjectMapper(), mock(org.springframework.cache.CacheManager.class));
    }
}
