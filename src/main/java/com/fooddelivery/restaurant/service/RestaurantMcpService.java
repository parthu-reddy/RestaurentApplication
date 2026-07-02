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

    private final FulfillmentController fulfillmentController;
    private final CatalogController catalogController;
    private final RestaurantOnboardingController onboardingController;
    private final ObjectMapper objectMapper;

    public RestaurantMcpService(FulfillmentController fulfillmentController,
                                CatalogController catalogController,
                                RestaurantOnboardingController onboardingController,
                                ObjectMapper objectMapper) {
        this.fulfillmentController = fulfillmentController;
        this.catalogController = catalogController;
        this.onboardingController = onboardingController;
        this.objectMapper = objectMapper;
    }

    @Tool(description = "Accept an incoming order. Provide restaurantId, orderId, optional additionalPrepTime (in minutes), and delayReason if additional time is needed.")
    public String acceptOrder(String restaurantId, String orderId, Integer additionalPrepTime, String delayReason) {
        try {
            com.fooddelivery.restaurant.dto.AcceptOrderRequest req = new com.fooddelivery.restaurant.dto.AcceptOrderRequest();
            req.setAdditionalPrepTime(additionalPrepTime);
            req.setDelayReason(delayReason);
            return objectMapper.writeValueAsString(fulfillmentController.acceptOrder(UUID.fromString(restaurantId), UUID.fromString(orderId), req).getBody());
        } catch (Exception e) {
            return "Failed to accept order: " + e.getMessage();
        }
    }

    @Tool(description = "Reject an incoming order. Provide restaurantId and orderId.")
    public String rejectOrder(String restaurantId, String orderId) {
        try {
            return objectMapper.writeValueAsString(fulfillmentController.rejectOrder(UUID.fromString(restaurantId), UUID.fromString(orderId)).getBody());
        } catch (Exception e) {
            return "Failed to reject order: " + e.getMessage();
        }
    }

    @Tool(description = "Mark an accepted order as ready for pickup. Provide restaurantId and orderId.")
    public String readyOrder(String restaurantId, String orderId) {
        try {
            return objectMapper.writeValueAsString(fulfillmentController.readyOrder(UUID.fromString(restaurantId), UUID.fromString(orderId)).getBody());
        } catch (Exception e) {
            return "Failed to mark order as ready: " + e.getMessage();
        }
    }

    @Tool(description = "Cancel an order after acceptance. Provide restaurantId and orderId.")
    public String cancelOrder(String restaurantId, String orderId) {
        try {
            return objectMapper.writeValueAsString(fulfillmentController.cancelOrder(UUID.fromString(restaurantId), UUID.fromString(orderId)).getBody());
        } catch (Exception e) {
            return "Failed to cancel order: " + e.getMessage();
        }
    }

    @Tool(description = "Add a master menu item to a brand. Provide brandId and a JSON string of MasterMenuItem.")
    public String addMasterMenuItem(String brandId, String masterMenuItemJson) {
        try {
            MasterMenuItem item = objectMapper.readValue(masterMenuItemJson, MasterMenuItem.class);
            return objectMapper.writeValueAsString(catalogController.addMasterMenuItem(UUID.fromString(brandId), item).getBody());
        } catch (Exception e) {
            return "Failed to add master menu item: " + e.getMessage();
        }
    }

    @Tool(description = "Get master menu items for a brand. Provide brandId.")
    public String getMasterMenuItems(String brandId) {
        try {
            return objectMapper.writeValueAsString(catalogController.getMasterMenuItems(UUID.fromString(brandId)).getBody());
        } catch (Exception e) {
            return "Failed to get master menu items: " + e.getMessage();
        }
    }

    @Tool(description = "Add an override for a menu item at an outlet. Provide outletId, masterMenuItemId, and JSON string of OutletMenuOverride.")
    public String addMenuOverride(String outletId, String masterMenuItemId, String overrideJson) {
        try {
            OutletMenuOverride override = objectMapper.readValue(overrideJson, OutletMenuOverride.class);
            return objectMapper.writeValueAsString(catalogController.overrideMenuItem(UUID.fromString(outletId), UUID.fromString(masterMenuItemId), override).getBody());
        } catch (Exception e) {
            return "Failed to add menu override: " + e.getMessage();
        }
    }

    @Tool(description = "Get effective menu for an outlet/restaurant. Provide restaurantId.")
    public String getEffectiveMenu(String restaurantId) {
        try {
            return objectMapper.writeValueAsString(catalogController.getEffectiveMenu(UUID.fromString(restaurantId)).getBody());
        } catch (Exception e) {
            return "Failed to get effective menu: " + e.getMessage();
        }
    }

    @Tool(description = "Get batch effective menu items. Provide restaurantId and comma separated idsStr.")
    public String getEffectiveMenuBatch(String restaurantId, String idsStr) {
        try {
            return objectMapper.writeValueAsString(catalogController.getEffectiveMenuBatch(UUID.fromString(restaurantId), idsStr).getBody());
        } catch (Exception e) {
            return "Failed to get batch menu items: " + e.getMessage();
        }
    }

    @Tool(description = "Onboard a new brand. Provide JSON string of BrandOnboardRequest.")
    public String onboardBrand(String brandOnboardRequestJson) {
        try {
            RestaurantOnboardingController.BrandOnboardRequest req = objectMapper.readValue(brandOnboardRequestJson, RestaurantOnboardingController.BrandOnboardRequest.class);
            return objectMapper.writeValueAsString(onboardingController.onboardBrand(req).getBody());
        } catch (Exception e) {
            return "Failed to onboard brand: " + e.getMessage();
        }
    }

    @Tool(description = "Onboard an outlet for a brand. Provide brandId and JSON string of OutletOnboardRequest.")
    public String onboardOutlet(String brandId, String outletOnboardRequestJson) {
        try {
            RestaurantOnboardingController.OutletOnboardRequest req = objectMapper.readValue(outletOnboardRequestJson, RestaurantOnboardingController.OutletOnboardRequest.class);
            return objectMapper.writeValueAsString(onboardingController.onboardOutlet(UUID.fromString(brandId), req).getBody());
        } catch (Exception e) {
            return "Failed to onboard outlet: " + e.getMessage();
        }
    }

    @Tool(description = "Get outlets for a brand. Provide brandId.")
    public String getOutlets(String brandId) {
        try {
            return objectMapper.writeValueAsString(onboardingController.getOutletsByBrand(UUID.fromString(brandId)).getBody());
        } catch (Exception e) {
            return "Failed to get outlets: " + e.getMessage();
        }
    }

    @Tool(description = "Get details of a specific restaurant/outlet. Provide restaurantId.")
    public String getRestaurantDetails(String restaurantId) {
        try {
            return objectMapper.writeValueAsString(onboardingController.getRestaurant(UUID.fromString(restaurantId)).getBody());
        } catch (Exception e) {
            return "Failed to get restaurant details: " + e.getMessage();
        }
    }
}
