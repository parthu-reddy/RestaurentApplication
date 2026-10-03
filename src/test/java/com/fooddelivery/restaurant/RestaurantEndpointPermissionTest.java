package com.fooddelivery.restaurant;

import com.fooddelivery.common.client.OrganisationServiceClient;
import com.fooddelivery.common.dto.organisation.MembershipDto;
import com.fooddelivery.common.dto.restaurant.OutletOrganisationDto;
import com.fooddelivery.common.enums.*;
import com.fooddelivery.common.security.organisation.DefaultOrganisationAccessPolicy;
import com.fooddelivery.restaurant.controller.CatalogController;
import com.fooddelivery.restaurant.controller.FulfillmentController;
import com.fooddelivery.restaurant.controller.RestaurantOutletController;
import com.fooddelivery.restaurant.controller.RestaurantOnboardingController;
import com.fooddelivery.restaurant.dto.StockToggleRequest;
import com.fooddelivery.restaurant.entity.*;
import com.fooddelivery.restaurant.repository.*;
import com.fooddelivery.restaurant.security.RestaurantAccess;
import com.fooddelivery.restaurant.service.CatalogService;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringJUnitConfig(RestaurantEndpointPermissionTest.Config.class)
class RestaurantEndpointPermissionTest {
    @TestConfiguration @EnableMethodSecurity static class Config {
        @Bean BrandRepository brands() { return mock(BrandRepository.class); }
        @Bean OutletRepository outlets() { return mock(OutletRepository.class); }
        @Bean OrganisationServiceClient orgs() { return mock(OrganisationServiceClient.class); }
        @Bean CatalogService catalog() { return mock(CatalogService.class); }
        @Bean com.fooddelivery.restaurant.service.FulfillmentService fulfillment() { return mock(com.fooddelivery.restaurant.service.FulfillmentService.class); }
        @Bean com.fooddelivery.restaurant.service.RestaurantOnboardingService onboarding() { return mock(com.fooddelivery.restaurant.service.RestaurantOnboardingService.class); }
        @Bean RestaurantAccess restaurantAccess(BrandRepository b, OutletRepository o, OrganisationServiceClient c) {
            return new RestaurantAccess(b,o,new DefaultOrganisationAccessPolicy(c,new io.micrometer.core.instrument.simple.SimpleMeterRegistry()));
        }
        @Bean CatalogController controller(CatalogService catalog) { return new CatalogController(catalog); }
        @Bean FulfillmentController fulfillmentController(com.fooddelivery.restaurant.service.FulfillmentService service) { return new FulfillmentController(service); }
        @Bean RestaurantOutletController outletController(com.fooddelivery.restaurant.service.RestaurantOnboardingService service) { return new RestaurantOutletController(service); }
        @Bean RestaurantOnboardingController applicationController(com.fooddelivery.restaurant.service.RestaurantOnboardingService service) {
            return new RestaurantOnboardingController(service,
                mock(com.fooddelivery.common.client.GovernmentIdServiceClient.class),
                mock(com.fooddelivery.common.service.RateLimitingService.class));
        }
    }
    @org.springframework.beans.factory.annotation.Autowired CatalogController controller;
    @org.springframework.beans.factory.annotation.Autowired CatalogService catalog;
    @org.springframework.beans.factory.annotation.Autowired OutletRepository outlets;
    @org.springframework.beans.factory.annotation.Autowired OrganisationServiceClient orgs;
    @org.springframework.beans.factory.annotation.Autowired FulfillmentController fulfillmentController;
    @org.springframework.beans.factory.annotation.Autowired RestaurantOutletController outletController;
    @org.springframework.beans.factory.annotation.Autowired RestaurantOnboardingController applicationController;
    @org.springframework.beans.factory.annotation.Autowired com.fooddelivery.restaurant.service.FulfillmentService fulfillment;
    @org.springframework.beans.factory.annotation.Autowired com.fooddelivery.restaurant.service.RestaurantOnboardingService onboarding;
    UUID outlet, orgId, user, item;
    @BeforeEach void setup() {
        reset(catalog,outlets,orgs,fulfillment,onboarding); outlet=UUID.randomUUID();orgId=UUID.randomUUID();user=UUID.randomUUID();item=UUID.randomUUID();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user.toString(),null,List.of(new SimpleGrantedAuthority("ROLE_RESTAURANT"))));
        when(outlets.findOrganisationByOutletId(outlet)).thenReturn(Optional.of(new OutletOrganisationDto(outlet,UUID.randomUUID(),orgId)));
    }
    @AfterEach void clear() { SecurityContextHolder.clearContext(); }
    @Test void staffCanToggleStockButCannotUseThePriceOverrideEndpoint() {
        member(OrganisationRole.STAFF);
        when(catalog.toggleStock(outlet,item,false)).thenReturn(OutletMenuOverride.builder().id(UUID.randomUUID()).isAvailable(false).build());
        assertEquals(200,assertDoesNotThrow(()->controller.toggleStock(outlet,item,new StockToggleRequest(false))).getStatusCode().value());
        assertEquals(200,controller.getOverrides(outlet).getStatusCode().value());
        assertThrows(AccessDeniedException.class,()->controller.overrideMenuItem(outlet,item,new OutletMenuOverride()));
        verify(catalog,never()).addOrUpdateOverride(any(),any(),any());
    }
    @Test void staffCanAcceptAnOrderButCannotManageOutletSettings() {
        member(OrganisationRole.STAFF);
        assertEquals(200,fulfillmentController.acceptOrder(outlet,item,null).getStatusCode().value());
        verify(fulfillment).acceptOrder(outlet,item,null,null);
        assertThrows(AccessDeniedException.class,()->outletController.updateOutletTimings(outlet,new com.fooddelivery.restaurant.dto.OutletTimingsUpdateRequest()));
        verifyNoInteractions(onboarding);
    }
    @Test void managerCanManagePricesAndStaffCanReadOverrides() {
        member(OrganisationRole.MANAGER);
        when(catalog.addOrUpdateOverride(any(),any(),any())).thenReturn(OutletMenuOverride.builder().id(UUID.randomUUID()).build());
        assertEquals(200,controller.overrideMenuItem(outlet,item,new OutletMenuOverride()).getStatusCode().value());
        assertEquals(200,controller.getOverrides(outlet).getStatusCode().value());
    }
    @Test void managerCannotApplyForABrand() {
        member(OrganisationRole.MANAGER);
        var request=new com.fooddelivery.restaurant.dto.BrandOnboardRequest();request.setOrganisationId(orgId);
        assertThrows(AccessDeniedException.class,()->applicationController.onboardBrand(
            SecurityContextHolder.getContext().getAuthentication(),request));
        verifyNoInteractions(onboarding);
    }
    @Test void removedOrUnrelatedMembersCannotReadOrWrite() {
        when(orgs.getMembership(orgId,user)).thenReturn(new MembershipDto(orgId,OrganisationStatus.ACTIVE,user,OrganisationRole.OWNER,MembershipStatus.REMOVED));
        assertThrows(AccessDeniedException.class,()->controller.toggleStock(outlet,item,new StockToggleRequest(true)));
        assertThrows(AccessDeniedException.class,()->controller.getOverrides(outlet));
        verifyNoInteractions(catalog);
    }
    private void member(OrganisationRole role) { when(orgs.getMembership(orgId,user)).thenReturn(new MembershipDto(orgId,OrganisationStatus.ACTIVE,user,role,MembershipStatus.ACTIVE)); }
}
