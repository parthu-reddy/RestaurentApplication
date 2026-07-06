---
name: restaurant-service-api
description: Complete API reference and integration guide for the Restaurant Application. Use this when building frontends, other microservices, or agents that need to interact with restaurant onboarding, catalog management, or kitchen fulfillment endpoints.
---

# Restaurant Service API Reference

The Restaurant Service provides endpoints for restaurant owners and staff.

## Base URL
External requests must go through the ApiGateway: `http://localhost:8080/api/restaurant`

## Core Endpoints

### 1. Register Restaurant
`POST /register`
- **Headers**: `Authorization: Bearer <token>`
- **Payload**:
  ```json
  {
    "name": "Pizza Palace",
    "address": "123 Main St",
    "phone": "+1234567890"
  }
  ```

### 2. Catalog Management
`POST /menu/items`
- **Headers**: `Authorization: Bearer <token>`
- **Payload**:
  ```json
  {
    "name": "Margherita Pizza",
    "description": "Classic cheese and tomato",
    "price": 12.99
  }
  ```

### 3. Fulfill Order (Accept)
`POST /orders/{orderId}/accept`
- **Headers**: `Authorization: Bearer <token>`
- **Description**: Transitions the order from `RECEIVED` to `PREPARED`. This triggers a Kafka event that the DeliveryExecutiveApp listens to for dispatch.

## Internal API
`GET /api/v1/internal/restaurants/catalog`
- **Description**: Used by `CustomerApplication` via Feign to fetch menu data for the consumer frontend.
