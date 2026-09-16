package com.fooddelivery.restaurant.service.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.common.constants.AppConstants;
import com.fooddelivery.common.enums.OutboxStatus;
import com.fooddelivery.common.outbox.entity.OutboxEventEntity;
import com.fooddelivery.common.outbox.repository.OutboxEventRepository;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

import io.micrometer.observation.annotation.Observed;

@Service
@lombok.extern.slf4j.Slf4j
@Observed(name = "restaurant.order.processing")
@lombok.RequiredArgsConstructor
public class RestaurantActionService {
    private final RestaurantOrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;
    private final OutletRepository outletRepository;

    public void saveOrder(RestaurantOrder order) {
        orderRepository.save(order);
    }

    public void publishEvent(String aggregateId, com.fooddelivery.common.constants.EventType eventType, Object eventPayload) {
        try {
            String payload = objectMapper.writeValueAsString(eventPayload);
            OutboxEventEntity outboxEvent = OutboxEventEntity.builder().id(UUID.randomUUID()).aggregateType(com.fooddelivery.common.constants.AggregateType.ORDER).aggregateId(aggregateId).eventType(eventType).payload(payload).createdAt(LocalDateTime.now()).status(OutboxStatus.UNPROCESSED).build();
            outboxEventRepository.save(outboxEvent);
            log.info("RESTAURANT_OUTBOX_EVENT_ENQUEUED eventId={} eventType={} aggregateId={}",
                    outboxEvent.getId(), eventType, aggregateId);
        } catch (Exception e) {
            log.error("RESTAURANT_OUTBOX_EVENT_FAILED eventType={} aggregateId={} errorType={} error={}",
                    eventType, aggregateId, e.getClass().getSimpleName(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }

    public ObjectNode createPayloadNode() {
        return objectMapper.createObjectNode();
    }

    public double[] getRestaurantCoordinates(UUID restaurantId) {
        String locationWkt = outletRepository.findLocationWktById(restaurantId);
        if (locationWkt == null || !locationWkt.startsWith("POINT(")) {
            throw new IllegalStateException("Invalid or missing location for restaurant: " + restaurantId);
        }
        try {
            String coords = locationWkt.substring(6, locationWkt.length() - 1);
            String[] parts = coords.split("\\s+");
            double lng = Double.parseDouble(parts[0]);
            double lat = Double.parseDouble(parts[1]);
            return new double[]{lat, lng};
        } catch (Exception e) {
            throw new IllegalStateException("Failed to parse restaurant location: " + locationWkt, e);
        }
    }
}
