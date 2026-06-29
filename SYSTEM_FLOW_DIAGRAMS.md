# Restaurant Application - System Flow Diagrams

This document illustrates the event-driven architecture and sequence flows of the `RestaurantApplication`, which manages the menu catalog and order fulfillment lifecycle for restaurant partners.

## High-Level Event Architecture

```mermaid
graph TD
    subgraph Restaurant Application
        API[Restaurant/Fulfillment REST API]
        DB[(restaurant_db)]
        Consumer[Order Event Consumer]
        Fulfill[Fulfillment Service]
    end

    subgraph Kafka Message Broker
        T1(order-events)
    end

    subgraph External Microservices
        CustApp[Customer Application]
        DelApp[Delivery Executive Application]
    end

    CustApp -->|Publishes ORDER_CREATED| T1
    
    T1 -->|Consumed by| Consumer
    Consumer -->|Process New Order| DB
    
    API -->|Accept/Reject Order| Fulfill
    Fulfill -->|Read Restaurant Lat/Lng| DB
    Fulfill -->|Publish ORDER_ACCEPTED| T1
    Fulfill -->|Publish ORDER_REJECTED| T1
    Fulfill -->|Publish ORDER_READY| T1
    
    T1 -->|Consumed by| DelApp
    T1 -->|Consumed by| CustApp
```

## Order Fulfillment Sequence

```mermaid
sequenceDiagram
    participant K_Order as Kafka (order-events)
    participant Consumer as OrderEventConsumer
    participant DB as Restaurant DB
    participant API as Fulfillment API
    participant Fulfill as FulfillmentService

    K_Order->>Consumer: Receive ORDER_CREATED
    Consumer->>DB: Save/Log incoming order request
    
    note over API,Fulfill: Restaurant Partner interaction
    API->>Fulfill: POST /api/v1/fulfillment/accept
    Fulfill->>DB: Fetch Restaurant Lat/Lng
    Fulfill->>K_Order: Publish ORDER_ACCEPTED (with Lat/Lng)
    
    API->>Fulfill: POST /api/v1/fulfillment/ready
    Fulfill->>K_Order: Publish ORDER_READY
```
