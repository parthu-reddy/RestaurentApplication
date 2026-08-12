package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import com.fooddelivery.common.repository.IIdempotencyKeyRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.data.redis.core.StringRedisTemplate;
import com.fooddelivery.restaurant.service.state.RestaurantActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.transaction.support.TransactionCallback;

import java.util.UUID;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderEventConsumerTest {

    private ObjectMapper objectMapper;

    @Mock
    private RestaurantOrderRepository restaurantOrderRepository;

    @Mock
    private RestaurantActionService actionService;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private IIdempotencyKeyRepository idempotencyKeyRepository;

    @Mock
    private MeterRegistry meterRegistry;

    private OrderEventConsumer orderEventConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
        orderEventConsumer = new OrderEventConsumer(objectMapper, restaurantOrderRepository, idempotencyKeyRepository, actionService, transactionTemplate, redisTemplate, meterRegistry);
        lenient().when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
    }

    @Test
    void consumeOrderEvent_ShouldProcessOrderPaidEvent() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        
        String message = String.format("{\"eventType\":\"ORDER_PAID\", \"orderId\":\"%s\", \"restaurantId\":\"%s\"}", 
                orderId, restaurantId);

        when(restaurantOrderRepository.existsById(orderId)).thenReturn(false);

        assertDoesNotThrow(() -> orderEventConsumer.consumeOrderEvent(message, null));
        
        ArgumentCaptor<RestaurantOrder> captor = ArgumentCaptor.forClass(RestaurantOrder.class);
        verify(actionService).saveOrder(captor.capture());
        
        RestaurantOrder capturedOrder = captor.getValue();
        assertEquals(orderId, capturedOrder.getOrderId());
        assertEquals(restaurantId, capturedOrder.getRestaurantId());
        assertEquals(OrderStatus.CREATED, capturedOrder.getStatus());
    }

    @Test
    void consumeOrderEvent_ShouldIgnoreOtherEventsIfOrderNotFound() throws Exception {
        UUID orderId = UUID.randomUUID();
        
        String message = String.format("{\"eventType\":\"ORDER_CANCELLED\", \"orderId\":\"%s\"}", orderId);

        when(restaurantOrderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> orderEventConsumer.consumeOrderEvent(message, null));
    }
}
