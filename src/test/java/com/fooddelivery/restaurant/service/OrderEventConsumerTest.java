package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.when;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    private ObjectMapper objectMapper;

    @Mock
    private StringRedisTemplate redisTemplate;
    @Mock
    private org.springframework.kafka.core.KafkaTemplate<String, String> kafkaTemplate;
    @Mock
    private com.fooddelivery.restaurant.repository.IRestaurantRepository restaurantRepository;
    
    @Mock
    private org.springframework.data.redis.core.ValueOperations<String, String> valueOperations;

    private OrderEventConsumer orderEventConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        orderEventConsumer = new OrderEventConsumer(objectMapper, redisTemplate, kafkaTemplate, restaurantRepository);
        org.mockito.Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void consumeOrderEvent_ShouldProcessOrderCreatedEvent() {
        UUID orderId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        
        String message = String.format("{\"eventType\":\"ORDER_PAID\", \"orderId\":\"%s\", \"restaurantId\":\"%s\"}", 
                orderId, restaurantId);

        assertDoesNotThrow(() -> orderEventConsumer.consumeOrderEvent(message, null));
    }

    @Test
    void consumeOrderEvent_ShouldIgnoreOtherEvents() {
        UUID orderId = UUID.randomUUID();
        
        String message = String.format("{\"eventType\":\"ORDER_ACCEPTED\", \"orderId\":\"%s\"}", orderId);

        assertDoesNotThrow(() -> orderEventConsumer.consumeOrderEvent(message, null));
    }
}
