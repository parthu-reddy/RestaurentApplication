package contracts.messaging

/*
 * Real payload for restaurant-events, from RestaurantOnboardingService (BRAND aggregate,
 * eventType BRAND_CREATED, key = brandId). Consumed by GovernmentIDValidationService's
 * BrandCreatedEventListener, which declares @Header("eventType") -- unsatisfiable until the
 * Phase 7 header fix, which is why this contract asserts the header.
 *
 * IMPORTANT: the previous contract described an ORDER_ACCEPTED event with orderId/restaurantId.
 * Nothing publishes that to this topic -- RestaurantActionService uses the ORDER aggregate, which
 * routes to order-events. This topic carries only BRAND_CREATED / OUTLET_ACTIVATED /
 * OUTLET_DEACTIVATED.
 */
org.springframework.cloud.contract.spec.Contract.make {
    description("Should publish BRAND_CREATED to restaurant-events")
    label("restaurant_events")
    input { triggeredBy('fireRestaurantAccepted()') }
    outputMessage {
        sentTo('restaurant-events')
        headers { header('eventType', 'BRAND_CREATED') }
        body([
            brandId: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            brandName: "Pizza Hub",
            gstin: "29ABCDE1234F1Z5",
            bankAccountNumber: "1234567890",
            ifscCode: "HDFC0001234",
            timestamp: $(producer(regex('.+')))
        ])
    }
}
