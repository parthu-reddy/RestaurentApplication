package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.dto.NearbyRestaurantDTO;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.service.RestaurantOnboardingService;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RestaurantFleetMapControllerTest {

    @Test
    void omitsOutletsWithoutLocationsInsteadOfReturningAnEquatorPin() {
        RestaurantOnboardingService service = mock(RestaurantOnboardingService.class);
        RestaurantOutletController controller = new RestaurantOutletController(service);
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Outlet located = Outlet.builder().id(UUID.randomUUID()).name("Located")
                .cityId("BLR").isActive(true)
                .location(geometryFactory.createPoint(new Coordinate(77.5946, 12.9716))).build();
        Outlet unlocated = Outlet.builder().id(UUID.randomUUID()).name("No fix")
                .cityId("BLR").isActive(true).build();
        when(service.getAllOutletsForFleetCity(eq("BLR"), any())).thenReturn(
                new PageImpl<>(List.of(located, unlocated), PageRequest.of(0, 100), 2));

        var response = controller.getAllOutletsWithLocation("BLR", 0, 100);
        var page = response.getBody().getData();

        assertThat(page.getContent()).extracting(NearbyRestaurantDTO::getId).containsExactly(located.getId());
        assertThat(page.getContent()).allSatisfy(outlet -> {
            assertThat(outlet.getLat()).isNotZero();
            assertThat(outlet.getLng()).isNotZero();
        });
        assertThat(page.getTotalElements()).isEqualTo(2);
        assertThat(page.getNumberOfElements()).isEqualTo(1);
        verify(service).getAllOutletsForFleetCity(eq("BLR"), any());
    }
}
