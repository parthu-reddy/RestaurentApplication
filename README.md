# RestaurantApplication

The RestaurantApplication manages the restaurant partner side of the Food Delivery ecosystem. It handles restaurant onboarding, menu catalog management, and kitchen fulfillment.

## Setup & Build
1. Build the service: `mvn clean install`
2. Run the application: `mvn spring-boot:run`
3. Port: `8082`

## Key Responsibilities
- **Restaurant Onboarding**: Allows owners to register their restaurant details.
- **Catalog Management**: Provides CRUD operations for menus, categories, and master items.
- **Order Fulfillment**: Allows restaurant staff to accept incoming orders and transition their state (e.g., from `RECEIVED` to `PREPARED`).

