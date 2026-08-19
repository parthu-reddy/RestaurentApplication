package com.fooddelivery.restaurant;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import com.fooddelivery.restaurant.repository.OutletRepository;
import com.fooddelivery.restaurant.controller.InternalRestaurantController;

public abstract class ContractTestBase {

    @BeforeEach
    public void setup() {

        OutletRepository outletRepository = Mockito.mock(OutletRepository.class);
        Mockito.when(outletRepository.findByOwnerId(Mockito.any(java.util.UUID.class)))
               .thenReturn(java.util.Collections.emptyList());
        InternalRestaurantController controller = new InternalRestaurantController(outletRepository);
        RestAssuredMockMvc.standaloneSetup(controller);
        
    }
}
