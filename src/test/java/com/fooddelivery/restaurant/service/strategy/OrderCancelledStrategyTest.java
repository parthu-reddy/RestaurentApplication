package com.fooddelivery.restaurant.service.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderCancelledStrategyTest {

    @Mock
    private RestaurantOrderRepository repository;

    private OrderCancelledStrategy strategy;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        strategy = new OrderCancelledStrategy(repository);
    }

    @Test
    void testProcessCancelOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        
        ObjectNode root = objectMapper.createObjectNode();
        root.put("orderId", orderId.toString());

        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(orderId);
        order.setStatus("CREATED");

        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        strategy.process(root);

        verify(repository).save(order);
        assertThat(order.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    void testProcessOrderNotFound() throws Exception {
        UUID orderId = UUID.randomUUID();
        ObjectNode root = objectMapper.createObjectNode();
        root.put("orderId", orderId.toString());

        when(repository.findById(orderId)).thenReturn(Optional.empty());

        strategy.process(root);

        verify(repository, never()).save(any());
    }

    @Test
    void testGetEventType() {
        assertThat(strategy.getEventType()).isEqualTo(EventType.ORDER_CANCELLED);
    }
}
