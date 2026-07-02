package com.fooddelivery.restaurant.service.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderPaidStrategyTest {

    @Mock
    private RestaurantOrderRepository repository;

    private OrderPaidStrategy strategy;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        strategy = new OrderPaidStrategy(repository);
    }

    @Test
    void testProcessNewOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        
        ObjectNode root = objectMapper.createObjectNode();
        root.put("orderId", orderId.toString());
        root.put("restaurantId", restaurantId.toString());
        root.put("estimatedPrepTimeMinutes", 20);

        when(repository.existsById(orderId)).thenReturn(false);

        strategy.process(root);

        ArgumentCaptor<RestaurantOrder> captor = ArgumentCaptor.forClass(RestaurantOrder.class);
        verify(repository).save(captor.capture());

        RestaurantOrder saved = captor.getValue();
        assertThat(saved.getOrderId()).isEqualTo(orderId);
        assertThat(saved.getRestaurantId()).isEqualTo(restaurantId);
        assertThat(saved.getStatus()).isEqualTo("PENDING");
        assertThat(saved.getPrepTime()).isEqualTo(20);
    }

    @Test
    void testProcessDuplicateOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        
        ObjectNode root = objectMapper.createObjectNode();
        root.put("orderId", orderId.toString());
        root.put("restaurantId", UUID.randomUUID().toString());

        when(repository.existsById(orderId)).thenReturn(true);

        strategy.process(root);

        verify(repository, never()).save(any());
    }

    @Test
    void testGetEventType() {
        assertThat(strategy.getEventType()).isEqualTo(EventType.ORDER_PAID);
    }
}
