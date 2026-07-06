---
name: understand-restaurant-service
description: Architectural overview and troubleshooting guide for the Restaurant Application. Use this skill when investigating order fulfillment issues or catalog sync problems.
---

# Understand RestaurantService

The RestaurantService acts as the master record for menus and the starting point for physical order fulfillment.

## Architecture & Integration

- **Role Provisioning**: It integrates tightly with `IdentityService`. When a new restaurant signs up, it makes a synchronous Feign call to create the internal user and provision the `ROLE_RESTAURANT_OWNER` role dynamically.
- **Order Flow**: It consumes `NEW_ORDER` events from Kafka (published by CustomerApplication) and presents them to the restaurant UI via WebSocket. Once the kitchen prepares the food, the owner clicks "Accept/Prepared" which publishes a `PREPARED` event to Kafka (consumed by `DeliveryExecutiveApplication`).

## Troubleshooting

- **Restaurant Owner Gets 403 Forbidden**: Ensure the `IdentityService` successfully assigned the `ROLE_RESTAURANT_OWNER` during onboarding. Verify the JWT contains this role.
- **Customer App Doesn't Show Menu**: The `CustomerApplication` relies on a synchronous Feign call to `/api/v1/internal/restaurants/catalog`. Check if `RestaurantApplication` is up and successfully registered with `EurekaServer`.
- **Order Not Reaching Kitchen**: Ensure the Kafka consumer for `NEW_ORDER` is running and the database is successfully persisting the incoming order state.
