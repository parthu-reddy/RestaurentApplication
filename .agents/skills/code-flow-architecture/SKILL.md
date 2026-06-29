---
name: code-flow-architecture
description: Explains the architecture and event-driven code flow in the Restaurant Application. Use this to understand how catalog and fulfillment are managed.
---

# Code Flow & Architecture Guide (Restaurant Application)

This document describes the architectural patterns used in the Restaurant Application.

## Architecture (Microservice)

The backend has been refactored from a Modular Monolith into independent microservices. This application (RestaurantApplication) runs on Java 21 and Spring Boot 3.3.0.

The codebase is organized under `com.fooddelivery`:
- **`restaurant`**: Manages KYB onboarding via mocked external verifications (`RestaurantOnboardingService`), catalog (`CatalogController`), and kitchen fulfillment (`FulfillmentService`).
- **`consumer`**: Contains `OrderEventConsumer` that listens to `order-events` from the Customer Application.

**Key Engineering Standards:**
- **Database**: The database schema is initialized using Flyway (`restaurant_db`). All initial tables and production indexes are squashed into a single `V1__init_schema.sql` file.
- **Onboarding Requirements**: The `RestaurantOnboardingService` mandates a "Penny Drop" mock verification. The `bankAccountNumber` and `ifscCode` fields are REQUIRED in the JSON payload when hitting the `POST /api/v1/restaurants/onboard` endpoint.

## Fulfillment Flow
1. **Receive Order**: `OrderEventConsumer` listens for `ORDER_CREATED` from Kafka.
2. **Acceptance**: `FulfillmentService` API is called by the restaurant partner to accept.
3. **Dispatch to Kafka**: The service fetches the restaurant's `lat`/`lng` from the isolated database and publishes `ORDER_ACCEPTED` (with location payload) to `order-events`.
4. **Rejection**: If the restaurant rejects, it publishes `ORDER_REJECTED`.

For visual diagrams, see `SYSTEM_FLOW_DIAGRAMS.md` in the repository root.
