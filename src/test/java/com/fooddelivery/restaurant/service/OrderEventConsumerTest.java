package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import com.fooddelivery.restaurant.service.strategy.RestaurantEventStrategy;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    private ObjectMapper objectMapper;

    @Mock
    private RestaurantEventStrategy mockPaidStrategy;

    private OrderEventConsumer orderEventConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        
        when(mockPaidStrategy.getEventType()).thenReturn("ORDER_PAID");

        orderEventConsumer = new OrderEventConsumer(objectMapper, new RestaurantEventStrategy[]{mockPaidStrategy});
    }

    @Test
    void consumeOrderEvent_ShouldProcessOrderCreatedEvent() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        
        String message = String.format("{\"eventType\":\"ORDER_PAID\", \"orderId\":\"%s\", \"restaurantId\":\"%s\"}", 
                orderId, restaurantId);

        assertDoesNotThrow(() -> orderEventConsumer.consumeOrderEvent(message, null));
        
        verify(mockPaidStrategy).process(any());
    }

    @Test
    void consumeOrderEvent_ShouldIgnoreOtherEvents() throws Exception {
        UUID orderId = UUID.randomUUID();
        
        String message = String.format("{\"eventType\":\"ORDER_ACCEPTED\", \"orderId\":\"%s\"}", orderId);

        assertDoesNotThrow(() -> orderEventConsumer.consumeOrderEvent(message, null));
    }
}
