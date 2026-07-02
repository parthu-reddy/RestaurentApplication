package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class OrderEventConsumer {

    private final ObjectMapper objectMapper;
    private final com.fooddelivery.restaurant.service.strategy.RestaurantEventStrategy[] strategies;
    private final java.util.Map<String, com.fooddelivery.restaurant.service.strategy.RestaurantEventStrategy> strategyMap;

    @org.springframework.beans.factory.annotation.Autowired
    public OrderEventConsumer(ObjectMapper objectMapper, com.fooddelivery.restaurant.service.strategy.RestaurantEventStrategy[] strategies) {
        this.objectMapper = objectMapper;
        this.strategies = strategies;
        this.strategyMap = java.util.Arrays.stream(strategies)
                .collect(java.util.stream.Collectors.toMap(
                        com.fooddelivery.restaurant.service.strategy.RestaurantEventStrategy::getEventType,
                        s -> s,
                        (s1, s2) -> s1 // In case of duplicate keys like ORDER_DELAY_REJECTED mapped twice
                ));
    }

    @Transactional
    @KafkaListener(topics = com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS, groupId = com.fooddelivery.common.constants.KafkaConstants.GROUP_RESTAURANT_SERVICE)
    public void consumeOrderEvent(String message, @org.springframework.messaging.handler.annotation.Header(value = "eventType", required = false) String headerEventType) {
        try {
            JsonNode root = objectMapper.readTree(message);
            String jsonEventType = root.path("eventType").asText(null);
            String eventType = headerEventType != null ? headerEventType : jsonEventType;
            
            com.fooddelivery.restaurant.service.strategy.RestaurantEventStrategy strategy = strategyMap.get(eventType);
            if (strategy != null) {
                strategy.process(root);
            } else {
                log.info("No strategy mapped for event type: {}. Ignoring.", eventType);
            }
        } catch (Exception e) {
            log.error("Failed to process order event in RestaurantApplication", e);
            throw new RuntimeException("Failed to process order event in RestaurantApplication", e);
        }
    }
}
