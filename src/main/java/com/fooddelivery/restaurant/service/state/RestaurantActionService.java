package com.fooddelivery.restaurant.service.state;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.common.constants.AppConstants;
import com.fooddelivery.common.enums.OutboxStatus;
import com.fooddelivery.common.outbox.entity.OutboxEventEntity;
import com.fooddelivery.common.outbox.repository.OutboxEventRepository;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantActionService {

    private final RestaurantOrderRepository orderRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public void saveOrder(RestaurantOrder order) {
        orderRepository.save(order);
    }

    public void publishEvent(String aggregateId, com.fooddelivery.common.constants.EventType eventType, ObjectNode payloadNode) {
        try {
            String payload = objectMapper.writeValueAsString(payloadNode);
            OutboxEventEntity outboxEvent = OutboxEventEntity.builder()
                    .id(UUID.randomUUID())
                    .aggregateType(com.fooddelivery.common.constants.AggregateType.ORDER)
                    .aggregateId(aggregateId)
                    .eventType(eventType)
                    .payload(payload)
                    .createdAt(LocalDateTime.now())
                    .status(OutboxStatus.UNPROCESSED)
                    .build();
            log.info("Triggering event: {} for aggregate: {}", eventType, aggregateId);
            outboxEventRepository.save(outboxEvent);
            log.info("Saved {} outbox event for order {}", eventType, aggregateId);
        } catch (Exception e) {
            log.error("Failed to serialize or save outbox event for type {}", eventType, e);
            throw new RuntimeException("Failed to publish event to Kafka", e);
        }
    }
    
    public ObjectNode createPayloadNode() {
        return objectMapper.createObjectNode();
    }
}
