package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurant.controller.CatalogController;
import com.fooddelivery.restaurant.controller.FulfillmentController;
import com.fooddelivery.restaurant.controller.RestaurantOnboardingController;
import com.fooddelivery.restaurant.entity.MasterMenuItem;
import com.fooddelivery.restaurant.entity.OutletMenuOverride;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RestaurantMcpService {

    private final FulfillmentService fulfillmentService;
    private final com.fooddelivery.restaurant.service.CatalogService catalogService;
    private final RestaurantOnboardingService onboardingService;
    private final ObjectMapper objectMapper;

    public RestaurantMcpService(FulfillmentService fulfillmentService,
                                com.fooddelivery.restaurant.service.CatalogService catalogService,
                                RestaurantOnboardingService onboardingService,
                                ObjectMapper objectMapper) {
        this.fulfillmentService = fulfillmentService;
        this.catalogService = catalogService;
        this.onboardingService = onboardingService;
        this.objectMapper = objectMapper;
    }

    @Tool(description = "Accept an incoming order. Provide restaurantId, orderId, optional additionalPrepTime (in minutes), and delayReason if additional time is needed.")
    public String acceptOrder(String restaurantId, String orderId, Integer additionalPrepTime, String delayReason) {
        try {
            fulfillmentService.acceptOrder(UUID.fromString(restaurantId), UUID.fromString(orderId), additionalPrepTime, delayReason);
            return "Order accepted successfully.";
        } catch (Exception e) {
            return "Failed to accept order: " + e.getMessage();
        }
    }

    @Tool(description = "Reject an incoming order. Provide restaurantId and orderId, and an optional rejectReason.")
    public String rejectOrder(String restaurantId, String orderId, String rejectReason) {
        try {
            fulfillmentService.rejectOrder(UUID.fromString(restaurantId), UUID.fromString(orderId), rejectReason);
            return "Order rejected successfully.";
        } catch (Exception e) {
            return "Failed to reject order: " + e.getMessage();
        }
    }

    @Tool(description = "Mark an accepted order as ready for pickup. Provide restaurantId and orderId.")
    public String readyOrder(String restaurantId, String orderId) {
        try {
            fulfillmentService.readyOrder(UUID.fromString(restaurantId), UUID.fromString(orderId));
            return "Order ready successfully.";
        } catch (Exception e) {
            return "Failed to mark order as ready: " + e.getMessage();
        }
    }

    @Tool(description = "Cancel an order after acceptance. Provide restaurantId and orderId, and an optional cancelReason.")
    public String cancelOrder(String restaurantId, String orderId, String cancelReason) {
        try {
            fulfillmentService.cancelOrderAfterAccept(UUID.fromString(restaurantId), UUID.fromString(orderId), cancelReason);
            return "Order cancelled successfully.";
        } catch (Exception e) {
            return "Failed to cancel order: " + e.getMessage();
        }
    }

    @Tool(description = "Add a master menu item to a brand. Provide brandId and a JSON string of MasterMenuItem.")
    public String addMasterMenuItem(String brandId, String masterMenuItemJson) {
        try {
            MasterMenuItem item = objectMapper.readValue(masterMenuItemJson, MasterMenuItem.class);
            return objectMapper.writeValueAsString(catalogService.addMasterMenuItem(UUID.fromString(brandId), item));
        } catch (Exception e) {
            return "Failed to add master menu item: " + e.getMessage();
        }
    }

    @Tool(description = "Get master menu items for a brand. Provide brandId.")
    public String getMasterMenuItems(String brandId) {
        try {
            return objectMapper.writeValueAsString(catalogService.getMasterMenuItems(UUID.fromString(brandId)));
        } catch (Exception e) {
            return "Failed to get master menu items: " + e.getMessage();
        }
    }

    @Tool(description = "Add an override for a menu item at an outlet. Provide outletId, masterMenuItemId, and JSON string of OutletMenuOverride.")
    public String addMenuOverride(String outletId, String masterMenuItemId, String overrideJson) {
        try {
            OutletMenuOverride override = objectMapper.readValue(overrideJson, OutletMenuOverride.class);
            return objectMapper.writeValueAsString(catalogService.addOrUpdateOverride(UUID.fromString(outletId), UUID.fromString(masterMenuItemId), override));
        } catch (Exception e) {
            return "Failed to add menu override: " + e.getMessage();
        }
    }

    @Tool(description = "Get effective menu for an outlet/restaurant. Provide restaurantId.")
    public String getEffectiveMenu(String restaurantId) {
        try {
            return objectMapper.writeValueAsString(catalogService.getEffectiveMenuForOutlet(UUID.fromString(restaurantId)));
        } catch (Exception e) {
            return "Failed to get effective menu: " + e.getMessage();
        }
    }

    @Tool(description = "Get batch effective menu items. Provide restaurantId and comma separated idsStr.")
    public String getEffectiveMenuBatch(String restaurantId, String idsStr) {
        try {
            java.util.List<UUID> ids = java.util.Arrays.stream(idsStr.split(","))
                .map(String::trim)
                .map(UUID::fromString)
                .collect(java.util.stream.Collectors.toList());
            return objectMapper.writeValueAsString(catalogService.getEffectiveMenuBatch(UUID.fromString(restaurantId), ids));
        } catch (Exception e) {
            return "Failed to get batch menu items: " + e.getMessage();
        }
    }

    @Tool(description = "Onboard a new brand. Provide JSON string of BrandOnboardRequest, and the ownerId.")
    public String onboardBrand(String ownerId, String brandOnboardRequestJson) {
        try {
            RestaurantOnboardingController.BrandOnboardRequest req = objectMapper.readValue(brandOnboardRequestJson, RestaurantOnboardingController.BrandOnboardRequest.class);
            return objectMapper.writeValueAsString(onboardingService.onboardBrand(UUID.fromString(ownerId), req.getName(), req.getGstin(), req.getPan(), req.getCin(), req.getBankAccountNumber(), req.getIfscCode(), req.getLogoUrl()));
        } catch (Exception e) {
            return "Failed to onboard brand: " + e.getMessage();
        }
    }

    @Tool(description = "Onboard an outlet for a brand. Provide brandId and JSON string of OutletOnboardRequest.")
    public String onboardOutlet(String brandId, String outletOnboardRequestJson) {
        try {
            RestaurantOnboardingController.OutletOnboardRequest req = objectMapper.readValue(outletOnboardRequestJson, RestaurantOnboardingController.OutletOnboardRequest.class);
            return objectMapper.writeValueAsString(onboardingService.onboardOutlet(UUID.fromString(brandId), req.getName(), req.getFssaiLicenseNumber(), req.getLat(), req.getLng(), req.getTimings(), req.getBannerUrl(), req.getCuisine(), req.getRating(), req.getReviewsCount(), req.getDeliveryTime(), req.getDeliveryFee(), req.getTags()));
        } catch (Exception e) {
            return "Failed to onboard outlet: " + e.getMessage();
        }
    }

    @Tool(description = "Get outlets for a brand. Provide brandId.")
    public String getOutlets(String brandId) {
        try {
            return objectMapper.writeValueAsString(onboardingService.getOutletsByBrand(UUID.fromString(brandId)));
        } catch (Exception e) {
            return "Failed to get outlets: " + e.getMessage();
        }
    }

    @Tool(description = "Get details of a specific restaurant/outlet. Provide restaurantId.")
    public String getRestaurantDetails(String restaurantId) {
        try {
            com.fooddelivery.restaurant.entity.Outlet outlet = onboardingService.getOutletById(UUID.fromString(restaurantId));
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("id", outlet.getId());
            response.put("name", outlet.getName());
            response.put("isActive", outlet.getIsActive());
            if (outlet.getLocation() != null) {
                response.put("lat", outlet.getLocation().getY());
                response.put("lng", outlet.getLocation().getX());
            }
            response.put("bannerUrl", outlet.getBannerUrl());
            com.fooddelivery.restaurant.entity.Brand brand = onboardingService.getBrandById(outlet.getBrandId());
            response.put("logoUrl", brand.getLogoUrl());
            return objectMapper.writeValueAsString(response);
        } catch (Exception e) {
            return "Failed to get restaurant details: " + e.getMessage();
        }
    }
}
