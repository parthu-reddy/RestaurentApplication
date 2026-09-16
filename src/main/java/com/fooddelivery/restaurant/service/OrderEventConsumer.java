package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.common.entity.IdempotencyKey;
import com.fooddelivery.restaurant.config.DeliveryZoneConfig;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.common.repository.IIdempotencyKeyRepository;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurant.service.state.RestaurantActionService;
import com.fooddelivery.restaurant.service.state.RestaurantOrderContext;
import com.fooddelivery.restaurant.service.state.RestaurantOrderState;
import com.fooddelivery.restaurant.service.state.RestaurantOrderStateFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.UUID;

@Service
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class OrderEventConsumer {
private final ObjectMapper objectMapper;

    private final com.fooddelivery.common.event.EventBinder eventBinder;
    
    /**
     * The event types this service binds, and the class each binds to.
     *
     * <p>Typed as {@code OrderScopedEvent} so the listener can take the order id off a bound event
     * without reflection, and so adding an entry whose class is not order-scoped does not compile.
     */
    private static final java.util.Map<EventType, Class<? extends com.fooddelivery.common.event.OrderScopedEvent>>
            EVENT_CLASSES = new java.util.EnumMap<>(EventType.class);
    static {
        EVENT_CLASSES.put(EventType.ORDER_PAID, com.fooddelivery.common.event.OrderPaidEvent.class);
        EVENT_CLASSES.put(EventType.ORDER_PLACED_COD, com.fooddelivery.common.event.OrderPaidEvent.class);
        EVENT_CLASSES.put(EventType.ORDER_CANCELLED, com.fooddelivery.common.event.OrderCancelledEvent.class);
        EVENT_CLASSES.put(EventType.ORDER_CANCELLED_BY_ADMIN, com.fooddelivery.common.event.OrderCancelledByAdminEvent.class);
        EVENT_CLASSES.put(EventType.ORDER_CANCELLED_BY_RESTAURANT, com.fooddelivery.common.event.OrderCancelledByRestaurantEvent.class);
        EVENT_CLASSES.put(EventType.ORDER_CANCELLED_BY_CUSTOMER, com.fooddelivery.common.event.OrderCancelledByCustomerEvent.class);
        EVENT_CLASSES.put(EventType.ORDER_DELAY_APPROVED, com.fooddelivery.common.event.OrderDelayApprovedEvent.class);
        EVENT_CLASSES.put(EventType.ORDER_DELAY_REJECTED, com.fooddelivery.common.event.OrderDelayRejectedEvent.class);
        EVENT_CLASSES.put(EventType.DRIVER_ASSIGNED, com.fooddelivery.common.event.DriverAssignedEvent.class);
        EVENT_CLASSES.put(EventType.ORDER_AT_RESTAURANT, com.fooddelivery.common.event.DriverAtRestaurantEvent.class);
        EVENT_CLASSES.put(EventType.ORDER_STATUS_UPDATED, com.fooddelivery.common.event.OrderStatusUpdatedEvent.class);
        EVENT_CLASSES.put(EventType.ORDER_STATUS_SYNC, com.fooddelivery.common.event.OrderStatusSyncEvent.class);
        EVENT_CLASSES.put(EventType.MANUAL_INTERVENTION_REQUIRED, com.fooddelivery.common.event.ManualInterventionRequiredEvent.class);
        EVENT_CLASSES.put(EventType.DELIVERY_FAILED, com.fooddelivery.common.event.DeliveryFailedEvent.class);
        EVENT_CLASSES.put(EventType.ORDER_DELIVERED, com.fooddelivery.common.event.DeliveredEvent.class);
    }

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final IIdempotencyKeyRepository idempotencyKeyRepository;
    private final RestaurantActionService actionService;
    private final org.springframework.transaction.support.TransactionTemplate transactionTemplate;
    private final org.springframework.data.redis.core.StringRedisTemplate redisTemplate;
    private final MeterRegistry meterRegistry;
    private final DeliveryZoneConfig deliveryZoneConfig;

    // No `include` list. It used to say {ObjectOptimisticLockingFailureException, RuntimeException},
    // which was redundant -- the default already retries every exception -- and actively harmful:
    // a class listed in `include` is CLASSIFIED, so traversingCauses stops at it and never reaches
    // the excluded EventBindingException underneath. A binding failure wrapped by the catch below
    // was therefore still retried five times. Proven in BindingFailureIsNotRetryableTest.
    @RetryableTopic(attempts = "5", backoff = @Backoff(delay = 100, multiplier = 2.0, maxDelay = 2000), exclude = {com.fooddelivery.common.event.EventBindingException.class}, traversingCauses = "true")
    @KafkaListener(topics = com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS, groupId = com.fooddelivery.common.constants.KafkaConstants.GROUP_RESTAURANT_SERVICE + "-ordereventconsumer")
    public void consumeOrderEvent(String message, @org.springframework.messaging.handler.annotation.Headers java.util.Map<String, Object> headers) {
        // Idempotency check
        String eventId = com.fooddelivery.common.util.KafkaHeaderUtils.extractHeaderValue(headers, "eventId");
        if (eventId == null) {
            log.error("Missing eventId header in OrderEventConsumer, sending to DLT.");
            throw new IllegalArgumentException("Missing eventId header");
        }
        log.info("Consumed order event with eventId={}", eventId);
        
        String idempotencyKeyStr = "processed_event:restaurant:" + eventId;
        
        try {
            transactionTemplate.execute(status -> {
                // Atomic claim: INSERT .. ON CONFLICT DO NOTHING. existsById-then-save was a
                // check-then-act race -- two consumers could both observe "absent" and both process.
                if (idempotencyKeyRepository.tryClaim(idempotencyKeyStr) == 0) {
                    log.info("Duplicate event detected (key={}), ignoring.", idempotencyKeyStr);
                    return null;
                }

                try {
                    com.fasterxml.jackson.databind.JsonNode rootNode = objectMapper.readTree(message);
                    String eventType = com.fooddelivery.common.util.KafkaHeaderUtils.extractEventType(headers, rootNode);
                    if (eventType == null) {
                        log.warn("Event type is missing in order event; ignoring payload without logging sensitive fields");
                        return null;
                    }
                    
                    final EventType type;
                    try {
                        type = EventType.valueOf(eventType);
                    } catch (IllegalArgumentException e) {
                        // order-events carries every order event the platform emits. A type this
                        // service's enum does not know is not addressed to it; retrying it five
                        // times and then DLTing it would poison the partition over an event we
                        // never wanted.
                        log.info("Unknown event type {} on order-events. Ignoring.", eventType);
                        return null;
                    }

                    Class<? extends com.fooddelivery.common.event.OrderScopedEvent> clazz =
                            EVENT_CLASSES.get(type);
                    if (clazz == null) {
                        log.info("Event {} not handled by RestaurantApplication. Ignoring.", eventType);
                        return null;
                    }
                    String payloadToBind = normalizeLegacyDispatchScope(type, rootNode, message);

                    // bindIf can only be empty when the type does not match, and it matches by
                    // construction here. A malformed body or a violated @NotNull throws out of
                    // bindIf, which is what feeds the retry/DLT path.
                    com.fooddelivery.common.event.OrderScopedEvent typedEvent =
                            eventBinder.bindIf(type, eventType, payloadToBind, clazz)
                                    .orElseThrow(() -> new IllegalStateException(
                                            "bindIf returned empty for " + eventType
                                                    + " despite an exact event-type match"));

                    UUID orderId = typedEvent.orderUuid();
                    if (orderId == null) {
                        log.warn("Order ID is missing in {} event; ignoring payload without logging sensitive fields", eventType);
                        return null;
                    }

                    // Handle ORDER_PAID and ORDER_PLACED_COD as a special case for creating the initial order entity
                    if (EventType.ORDER_PAID.name().equals(eventType) || EventType.ORDER_PLACED_COD.name().equals(eventType)) {
                        handleOrderPaid((com.fooddelivery.common.event.OrderPaidEvent) typedEvent);
                        return null;
                    }
                    RestaurantOrder order = restaurantOrderRepository.findById(orderId).orElse(null);
                    if (order == null) {
                        log.warn("Order {} not found for event type: {}", orderId, eventType);
                        return null;
                    }
                    double lat = 0.0;
                    double lng = 0.0;
                    try {
                        double[] coords = actionService.getRestaurantCoordinates(order.getRestaurantId());
                        lat = coords[0];
                        lng = coords[1];
                    } catch (Exception e) {
                        log.error("Failed to fetch coordinates for order {}", orderId, e);
                        throw e;
                    }
                    RestaurantOrderContext ctx = RestaurantOrderContext.builder()
                        .order(order)
                        .eventPayload(typedEvent)
                        .actionService(actionService)
                        .restaurantId(order.getRestaurantId())
                        .restaurantLat(lat)
                        .restaurantLng(lng)
                        .build();
                    RestaurantOrderState state = RestaurantOrderStateFactory.getState(order.getStatus());
                    try {
                        switch (EventType.valueOf(eventType)) {
                        case ORDER_CANCELLED: 
                        case ORDER_CANCELLED_BY_ADMIN: 
                        case ORDER_CANCELLED_BY_RESTAURANT:
                            state.handleOrderCancelled(ctx);
                            break;
                        case ORDER_CANCELLED_BY_CUSTOMER: 
                            state.handleOrderCancelledByCustomer(ctx);
                            break;
                        case ORDER_DELAY_APPROVED: 
                            state.handleDelayApproved(ctx);
                            break;
                        case ORDER_DELAY_REJECTED: 
                            state.handleDelayRejected(ctx);
                            break;
                        case DRIVER_ASSIGNED: 
                            state.handleDriverAssigned(ctx);
                            break;
                        case ORDER_AT_RESTAURANT: 
                            state.handleDriverAtRestaurant(ctx);
                            break;
                        case ORDER_STATUS_UPDATED: 
                            state.handleOrderStatusUpdated(ctx);
                            break;
                        case ORDER_STATUS_SYNC: 
                            state.handleOrderStatusSync(ctx);
                            break;
                        case MANUAL_INTERVENTION_REQUIRED: 
                            state.handleManualInterventionRequired(ctx);
                            break;
                        case DELIVERY_FAILED: 
                            state.handleDeliveryFailed(ctx);
                            break;
                        case ORDER_DELIVERED: 
                            state.handleOrderDelivered(ctx);
                            break;
                        default: 
                            log.info("Event {} not handled by state machine. Ignoring.", eventType);
                        }
                    } catch (com.fooddelivery.common.exception.IllegalStateTransitionException e) {
                        log.warn("Illegal state transition for event {} on order {}", eventType, orderId, e);
                    }
                    return null;
                } catch (org.springframework.orm.ObjectOptimisticLockingFailureException e) {
                    log.warn("Optimistic locking failure in consumeOrderEvent. Propagating for @RetryableTopic retry.");
                    throw e;
                } catch (Exception e) {
                    throw new RuntimeException("Error executing order event logic", e);
                }
            });
        } catch (Exception outerException) {
            throw outerException;
        }
    }

    @DltHandler
    public void handleDlt(String message, @org.springframework.messaging.handler.annotation.Headers java.util.Map<String, Object> headers) {
        log.error("DLT processing: order event exhausted retries in RestaurantApplication (eventId={})",
                com.fooddelivery.common.util.KafkaHeaderUtils.extractHeaderValue(headers, "eventId"));
        meterRegistry.counter("kafka.dlt.messages", "service", "restaurant-application").increment();
    }

    @lombok.Data
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    private static class LegacyDispatchCheck {
        private String dispatchCityId;
        private Double fleetSearchRadiusKm;
        private java.util.UUID orderId;
    }

    private String normalizeLegacyDispatchScope(EventType type, JsonNode rootNode, String originalPayload)
            throws com.fasterxml.jackson.core.JsonProcessingException {
        if ((type != EventType.ORDER_PAID && type != EventType.ORDER_PLACED_COD)
                || !rootNode.isObject()) {
            return originalPayload;
        }

        LegacyDispatchCheck check = objectMapper.readValue(originalPayload, LegacyDispatchCheck.class);
        boolean cityMissing = check.getDispatchCityId() == null || check.getDispatchCityId().isBlank();
        boolean radiusMissing = check.getFleetSearchRadiusKm() == null || check.getFleetSearchRadiusKm() <= 0;
        if (!cityMissing && !radiusMissing) {
            return originalPayload;
        }

        com.fasterxml.jackson.databind.node.ObjectNode normalized = rootNode.deepCopy();
        if (cityMissing) {
            normalized.put("dispatchCityId", deliveryZoneConfig.getDefaultCity());
        }
        if (radiusMissing) {
            normalized.put("fleetSearchRadiusKm", deliveryZoneConfig.getFleetSearchRadiusKm());
        }
        log.warn("Order {} predates dispatch-scope fields; supplied configured legacy defaults for cityMissing={} radiusMissing={}",
                check.getOrderId(), cityMissing, radiusMissing);
        return objectMapper.writeValueAsString(normalized);
    }

    private void handleOrderPaid(com.fooddelivery.common.event.OrderPaidEvent event) {
        UUID orderId = event.getOrderId();
        if (restaurantOrderRepository.existsById(orderId)) {
            log.info("Duplicate ORDER_PAID or ORDER_PLACED_COD event received for order {}. Ignoring.", orderId);
            return;
        }
        UUID restaurantId = event.getRestaurantId();
        int estimatedPrepTimeMinutes = event.getEstimatedPrepTimeMinutes() != null ? event.getEstimatedPrepTimeMinutes() : 15;
        double deliveryLat = event.getDeliveryLat();
        double deliveryLng = event.getDeliveryLng();
        String deliveryAddress = event.getDeliveryAddress() != null ? event.getDeliveryAddress() : "";
        String itemsJson = event.getItemsJson() != null ? event.getItemsJson() : "[]";
        String pickupOtp = event.getPickupOtp();
        String deliveryOtp = event.getDeliveryOtp();
        log.info("Received paid/COD order {} with validated pickup and delivery OTPs", orderId);
        UUID customerId = event.getCustomerId();
        String customerName = event.getCustomerName() != null ? event.getCustomerName() : "";
        com.fooddelivery.common.enums.PaymentMethod paymentMethod = event.getPaymentMethod();
        java.math.BigDecimal totalAmount = event.getTotalAmount();
        java.math.BigDecimal foodCost = event.getItemTotal();
        java.math.BigDecimal restaurantPlatformFee = event.getRestaurantPlatformFee();
        java.math.BigDecimal restaurantDeliveryContribution = event.getRestaurantDeliveryContribution();
        java.math.BigDecimal platformBonus = event.getPlatformBonus();
        java.math.BigDecimal restaurantPayout = event.getRestaurantPayout();

        RestaurantOrder order = RestaurantOrder.builder()
                .orderId(orderId)
                .restaurantId(restaurantId)
                .customerId(customerId)
                .customerName(customerName)
                .paymentMethod(paymentMethod)
                .status(OrderStatus.CREATED)
                .prepTime(estimatedPrepTimeMinutes)
                .additionalPrepTime(0)
                .deliveryLat(deliveryLat)
                .deliveryLng(deliveryLng)
                .deliveryAddress(deliveryAddress)
                .dispatchCityId(event.getDispatchCityId())
                .fleetSearchRadiusKm(event.getFleetSearchRadiusKm())
                .pickupOtp(pickupOtp)
                .deliveryOtp(deliveryOtp)
                .itemsJson(itemsJson)
                .totalAmount(totalAmount)
                .foodCost(foodCost)
                .restaurantPlatformFee(restaurantPlatformFee)
                .restaurantDeliveryContribution(restaurantDeliveryContribution)
                .platformBonus(platformBonus)
                .restaurantPayout(restaurantPayout)
                .build();
        actionService.saveOrder(order);
        log.info("Restaurant {} received new paid order {} with estimated prep time {}m. Awaiting restaurant staff to accept/reject.", restaurantId, orderId, estimatedPrepTimeMinutes);
    }

}
