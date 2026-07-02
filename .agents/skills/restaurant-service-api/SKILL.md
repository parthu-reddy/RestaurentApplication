---
name: restaurant-service-api
description: Complete API reference and integration guide for the Restaurant Application. Use this when building frontends, other microservices, or agents that need to interact with restaurant onboarding, catalog management, or kitchen fulfillment endpoints.
---

# Restaurant Application: API & Integration Guide

This service manages the entire restaurant lifecycle — from brand onboarding and menu catalog management to real-time kitchen fulfillment. It runs as an independent Spring Boot microservice on **port 8083** (default).

## Base URL
Default local environment: `http://localhost:8083`

## REST API Endpoints

### Onboarding (Brand & Outlet Registration)

#### 1. Create Brand
**Endpoint**: `POST /api/v1/brands`
**Purpose**: Register a new restaurant brand/corporate entity with KYB verification (mocked).
**Body (JSON)**:
```json
{
  "name": "Pizza Palace",
  "gstin": "22AAAAA0000A1Z5",
  "panNumber": "AAAPA1234A",
  "bankAccountNumber": "1234567890",
  "ifscCode": "SBIN0001234"
}
```
> **Note**: `bankAccountNumber` and `ifscCode` are **REQUIRED** — the service runs a mocked "Penny Drop" bank verification.

#### 2. Create Outlet (under a Brand)
**Endpoint**: `POST /api/v1/brands/{brandId}/outlets`
**Purpose**: Register a physical storefront under an existing brand.
**Body (JSON)**:
```json
{
  "name": "Pizza Palace - Koramangala",
  "fssaiLicenseNumber": "10020011002001",
  "latitude": 12.9352,
  "longitude": 77.6245,
  "openingTime": "10:00",
  "closingTime": "23:00"
}
```

#### 3. List Outlets
**Endpoint**: `GET /api/v1/brands/{brandId}/outlets`
**Purpose**: Get all physical outlets for a brand.

#### 4. Get Restaurant (Outlet) Details
**Endpoint**: `GET /api/v1/restaurants/{id}`
**Purpose**: Fetch a single outlet's details including location, operating hours, and active status. Used by `CustomerApplication` during order creation to validate the restaurant.

---

### Catalog (Menu Management)

#### 5. Create Master Menu Item
**Endpoint**: `POST /api/v1/brands/{brandId}/master-menu`
**Purpose**: Add an item to the brand's global master menu.
**Body (JSON)**:
```json
{
  "name": "Margherita Pizza",
  "basePrice": 299.00,
  "active": true
}
```

#### 6. List Master Menu
**Endpoint**: `GET /api/v1/brands/{brandId}/master-menu`
**Purpose**: Get all master menu items for a brand.

#### 7. Create Outlet Menu Override
**Endpoint**: `POST /api/v1/outlets/{outletId}/menu-overrides/{masterMenuItemId}`
**Purpose**: Override price or availability of a master menu item at a specific outlet.
**Body (JSON)**:
```json
{
  "price": 349.00,
  "available": true
}
```

#### 8. List Outlet Catalog Items
**Endpoint**: `GET /api/v1/restaurants/{restaurantId}/catalog/items`
**Purpose**: Get the effective menu for a specific outlet (master items + overrides merged).

#### 9. Batch Menu Fetch
**Endpoint**: `GET /api/v1/restaurants/{restaurantId}/menu/batch`
**Purpose**: Fetch menu items and estimated prep time in bulk. Used by `CustomerApplication` during order creation for price validation and prep time calculation.
**Query Parameters**:
- `itemIds` (List<UUID>, required): Comma-separated list of menu item UUIDs.

---

### Fulfillment (Kitchen Operations)

All fulfillment endpoints follow the pattern:
`POST /api/v1/restaurants/{restaurantId}/fulfillment/orders/{orderId}/...`

#### 10. Accept Order
**Endpoint**: `POST .../accept`
**Purpose**: Restaurant accepts an incoming order. If `additionalPrepTime` exceeds 10 minutes, triggers a delay approval request to the customer.
**Body (JSON, optional)**:
```json
{
  "additionalPrepTime": 15,
  "delayReason": "Kitchen is busy"
}
```
**Side Effects**:
- If prepTime ≤ 10 min extra: Publishes `ORDER_ACCEPTED` (with restaurant lat/lng) to `order-events`.
- If prepTime > 10 min extra: Publishes `ORDER_DELAY_APPROVAL_REQUESTED` to `order-events`.

#### 11. Reject Order
**Endpoint**: `POST .../reject`
**Purpose**: Restaurant declines the order (e.g., out of stock).
**Side Effects**: Publishes `ORDER_REJECTED` to `order-events` → triggers customer refund.

#### 12. Mark Order Ready
**Endpoint**: `POST .../ready`
**Purpose**: Restaurant marks food as prepared and ready for driver pickup.
**Side Effects**: Publishes `ORDER_READY` to `order-events`.

#### 13. Cancel Order (After Acceptance)
**Endpoint**: `POST .../cancel`
**Purpose**: Restaurant cancels an already-accepted order.
**Side Effects**: Publishes `ORDER_CANCELLED_BY_RESTAURANT` to `order-events` → triggers customer refund and aborts any pending dispatch.

---

## Kafka Integration

### Consumed Events (from `order-events`)
| Event | Action |
|---|---|
| `ORDER_PAID` | Creates a `RestaurantOrder` (status: `PENDING`) for kitchen fulfillment |
| `ORDER_DELAY_APPROVED` | Accepts the delayed order and publishes `ORDER_ACCEPTED` |
| `ORDER_DELAY_REJECTED` | Stops preparation via `OrderDelayRejectedStrategy` |
| `ORDER_CANCELLED` | Cleans up via terminal state strategy |

### Published Events (to `order-events`)
| Event | Trigger |
|---|---|
| `ORDER_ACCEPTED` | Restaurant accepts (includes `restaurantLat`, `restaurantLng`, `deliveryLat`, `deliveryLng`) |
| `ORDER_REJECTED` | Restaurant rejects |
| `ORDER_CANCELLED_BY_RESTAURANT` | Restaurant cancels post-acceptance |
| `ORDER_DELAY_APPROVAL_REQUESTED` | Prep time exceeds 10-minute threshold |
| `ORDER_READY` | Food is prepared |

## Database
- **PostgreSQL** database: `restaurant_db`
- **Flyway migrations**: `src/main/resources/db/migration/`
- Key tables: `brands`, `outlets`, `master_menu_items`, `outlet_menu_overrides`, `restaurant_orders`, `outbox_events`

## State Machine
Restaurant orders use a State Pattern with implementations:
- `CreatedState` → `AcceptedState` → `ReadyState` → Terminal
- `CreatedState` → `RejectedState` (terminal)
- `AcceptedState` → `CancelledState` (terminal)
