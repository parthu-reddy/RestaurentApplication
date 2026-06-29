# Restaurant Application (Catalog & Fulfillment)

The Restaurant Application is a dedicated microservice for restaurant partners. It handles the menu catalog, pricing, and the restaurant-side order fulfillment lifecycle. It operates completely independently of the Customer Application, communicating only via asynchronous Kafka events.

## Key Responsibilities

1. **Menu & Catalog Management**: 
   - Manages restaurant profiles, menu items, and pricing in its isolated database (`restaurant_db`).
2. **Order Fulfillment**: 
   - Listens for new order requests (`ORDER_CREATED`) from the Customer Application.
   - Allows restaurant staff to accept or reject orders via the `FulfillmentService`.
3. **Geospatial Context**:
   - When an order is accepted, it attaches the restaurant's geographic coordinates (`lat`/`lng`) to the `ORDER_ACCEPTED` event so the Delivery Application can route drivers effectively.

## Architecture & Integrations

- **Database**: PostgreSQL (`restaurant_db`). Fully isolated.
- **Message Broker**: Apache Kafka.
- **Events Published**: 
  - `ORDER_ACCEPTED` -> `order-events` (Consumed by Customer and Delivery apps)
  - `ORDER_REJECTED` -> `order-events` (Consumed by Customer app for refund processing)
- **Events Consumed**:
  - `ORDER_CREATED` (From `order-events`)

## Running Locally

```bash
# Start required infrastructure (Kafka, Zookeeper, PostgreSQL)
docker-compose up -d

# Run the application
./mvnw spring-boot:run
```
