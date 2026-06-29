package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    private ObjectMapper objectMapper;
    private OrderEventConsumer orderEventConsumer;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        orderEventConsumer = new OrderEventConsumer(objectMapper);
    }

    @Test
    void consumeOrderEvent_ShouldProcessOrderCreatedEvent() {
        UUID orderId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        
        String message = String.format("{\"eventType\":\"ORDER_CREATED\", \"orderId\":\"%s\", \"restaurantId\":\"%s\"}", 
                orderId, restaurantId);

        assertDoesNotThrow(() -> orderEventConsumer.consumeOrderEvent(message));
    }

    @Test
    void consumeOrderEvent_ShouldIgnoreOtherEvents() {
        UUID orderId = UUID.randomUUID();
        
        String message = String.format("{\"eventType\":\"ORDER_ACCEPTED\", \"orderId\":\"%s\"}", orderId);

        assertDoesNotThrow(() -> orderEventConsumer.consumeOrderEvent(message));
    }
}
