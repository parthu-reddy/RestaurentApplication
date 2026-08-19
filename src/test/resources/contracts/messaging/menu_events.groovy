package contracts.messaging

/*
 * Real wire payload for menu-events, from CatalogService.notifyMenuUpdate:
 *   String.format("{\"brandId\":\"%s\",\"type\":\"MENU_UPDATED\",\"timestamp\":\"%s\"}", ...)
 * Flat, keyed by brandId, and note the field is `type` -- not `eventType`.
 */
org.springframework.cloud.contract.spec.Contract.make {
    description("Should publish MENU_UPDATED to menu-events")
    label("menu_events")
    input {
        triggeredBy('fireMenuUpdated()')
    }
    outputMessage {
        sentTo('menu-events')
        body([
            brandId: $(producer(regex('[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}'))),
            type: "MENU_UPDATED",
            timestamp: $(producer(regex('.+')))
        ])
    }
}
