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
class OrderDelayApprovedStrategyTest {

    @Mock
    private RestaurantOrderRepository repository;
    
    @Mock
    private com.fooddelivery.restaurant.repository.OutletRepository outletRepository;
    
    @Mock
    private com.fooddelivery.common.outbox.repository.OutboxEventRepository outboxEventRepository;

    private OrderDelayApprovedStrategy strategy;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        strategy = new OrderDelayApprovedStrategy(repository, outletRepository, objectMapper, outboxEventRepository);
    }

    @Test
    void testProcessApproveDelay() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        
        ObjectNode root = objectMapper.createObjectNode();
        root.put("orderId", orderId.toString());
        root.put("restaurantId", restaurantId.toString());

        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(orderId);
        order.setStatus("DELAY_APPROVAL_PENDING");

        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        strategy.process(root);

        verify(repository).save(order);
        assertThat(order.getStatus()).isEqualTo("ACCEPTED"); // Assuming approved means accepted
    }

    @Test
    void testGetEventType() {
        assertThat(strategy.getEventType()).isEqualTo(EventType.ORDER_DELAY_APPROVED);
    }
}
