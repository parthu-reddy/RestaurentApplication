package com.fooddelivery.restaurant.controller;

import com.fooddelivery.common.client.GovernmentIdServiceClient;
import com.fooddelivery.common.service.RateLimitingService;
import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.service.RestaurantOnboardingService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BrandListPrivacyTest {
    @Test
    void membershipListKeepsDisplayMetadataWithoutDisclosingApplicationOrBankIdentifiers() throws Exception {
        UUID user = UUID.randomUUID(), organisation = UUID.randomUUID(), brandId = UUID.randomUUID();
        var service = mock(RestaurantOnboardingService.class);
        var brand = Brand.builder().id(brandId).organisationId(organisation).name("Test brand")
                .pan("AAAAA0000A").gstin("29AAAAA0000A1Z5").cin("U12345KA2020PTC123456")
                .bankAccountNumber("123456789012").bankIfsc("TEST0000001")
                .bankBeneficiaryName("Private beneficiary").legalEntityName("Private legal entity").build();
        when(service.getBrands(user)).thenReturn(List.of(brand));
        var controller = new RestaurantOnboardingController(service,
                mock(GovernmentIdServiceClient.class), mock(RateLimitingService.class));
        MockMvcBuilders.standaloneSetup(controller).build()
                .perform(get("/api/v1/brands").principal(() -> user.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(brandId.toString()))
                .andExpect(jsonPath("$.data[0].organisationId").value(organisation.toString()))
                .andExpect(jsonPath("$.data[0].name").value("Test brand"))
                .andExpect(jsonPath("$.data[0].kycStatus").value("PENDING"))
                .andExpect(jsonPath("$.data[0].pan").doesNotExist())
                .andExpect(jsonPath("$.data[0].gstin").doesNotExist())
                .andExpect(jsonPath("$.data[0].cin").doesNotExist())
                .andExpect(jsonPath("$.data[0].bankAccountNumber").doesNotExist())
                .andExpect(jsonPath("$.data[0].bankIfsc").doesNotExist())
                .andExpect(jsonPath("$.data[0].bankBeneficiaryName").doesNotExist())
                .andExpect(jsonPath("$.data[0].legalEntityName").doesNotExist());
        verify(service).getBrands(user);
    }
}
