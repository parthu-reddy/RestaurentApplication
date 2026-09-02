package com.fooddelivery.restaurant;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.controller.CatalogController;
import com.fooddelivery.restaurant.controller.InternalOrderController;
import com.fooddelivery.restaurant.controller.InternalRestaurantController;
import com.fooddelivery.restaurant.controller.RestaurantOutletController;
import com.fooddelivery.restaurant.dto.MenuItemDTO;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.OutletRepository;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurant.security.RestaurantSecurityHelper;
import com.fooddelivery.restaurant.service.CatalogService;
import com.fooddelivery.restaurant.service.RestaurantOnboardingService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;

/**
 * Base for the generated HTTP contract tests.
 *
 * RestAssuredMockMvc.standaloneSetup mounts ONLY the controllers handed to it. Previously just
 * InternalRestaurantController was mounted, so contracts for CatalogController,
 * RestaurantOutletController and InternalOrderController all returned 404 from an empty dispatcher --
 * which looks identical to a missing endpoint and was misdiagnosed as one.
 *
 * The fixtures below exist because the handlers return 404 or an empty list when their collaborators
 * find nothing, while the contracts assert populated 200s.
 */
public abstract class ContractTestBase {

    private static final UUID MENU_ITEM_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID SAMPLE_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @BeforeEach
    public void setup() {
        OutletRepository outletRepository = Mockito.mock(OutletRepository.class);
        RestaurantOnboardingService onboardingService = Mockito.mock(RestaurantOnboardingService.class);
        CatalogService catalogService = Mockito.mock(CatalogService.class);
        RestaurantSecurityHelper securityHelper = Mockito.mock(RestaurantSecurityHelper.class);
        RestaurantOrderRepository orderRepository = Mockito.mock(RestaurantOrderRepository.class);

        Outlet outlet = new Outlet();
        outlet.setId(SAMPLE_ID);
        outlet.setName("Test Restaurant");
        outlet.setIsActive(Boolean.TRUE);
        outlet.setDefaultPrepTimeSeconds(900);
        outlet.setRating(4.5);

        Mockito.when(outletRepository.findByOwnerId(any(UUID.class))).thenReturn(List.of(outlet));
        Mockito.when(onboardingService.getNearbyOutlets(anyDouble(), anyDouble(), anyDouble()))
               .thenReturn(List.of(outlet));
        // getBrandOutlets.groovy asserts 'Test Outlet' while getNearbyRestaurants.groovy asserts
        // 'Test Restaurant'; they are different service methods, so they get different fixtures.
        Outlet brandOutlet = new Outlet();
        brandOutlet.setId(SAMPLE_ID);
        brandOutlet.setName("Test Outlet");
        brandOutlet.setIsActive(Boolean.TRUE);
        brandOutlet.setDefaultPrepTimeSeconds(900);
        brandOutlet.setRating(4.5);
        Mockito.when(onboardingService.getNearbyOutletsByBrand(any(UUID.class), anyDouble(), anyDouble(), anyDouble()))
               .thenReturn(List.of(brandOutlet));
        Mockito.when(onboardingService.getOutletById(any(UUID.class))).thenReturn(outlet);

        // getRestaurant() dereferences getBrandById(...) unguarded -- a null brand NPEs there, in
        // production as well as here.
        com.fooddelivery.restaurant.entity.Brand brand = new com.fooddelivery.restaurant.entity.Brand();
        brand.setLogoUrl("https://example.test/logo.png");
        Mockito.when(onboardingService.getBrandById(any())).thenReturn(brand);

        MenuItemDTO item = new MenuItemDTO();
        item.setId(MENU_ITEM_ID);
        item.setName("Pizza");
        item.setPrice(new BigDecimal("10.0"));
        Mockito.when(catalogService.getEffectiveMenuBatch(any(UUID.class), anyList()))
               .thenReturn(List.of(item));

        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(SAMPLE_ID);
        order.setStatus(OrderStatus.PREPARING);
        Mockito.when(orderRepository.findById(any(UUID.class))).thenReturn(Optional.of(order));

        RestAssuredMockMvc.standaloneSetup(
                new InternalRestaurantController(outletRepository,
                        Mockito.mock(com.fooddelivery.restaurant.repository.MasterMenuItemRepository.class),
                        Mockito.mock(com.fooddelivery.restaurant.repository.BrandRepository.class)),
                new RestaurantOutletController(onboardingService),
                new CatalogController(catalogService, securityHelper),
                new InternalOrderController(orderRepository));
    }
}
