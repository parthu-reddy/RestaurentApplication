package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurant.entity.OrderStatus;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.OutletRepository;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurant.service.state.RestaurantActionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FulfillmentServiceTest {

    @Mock
    private OutletRepository outletRepository;

    @Mock
    private RestaurantOrderRepository restaurantOrderRepository;

    @Mock
    private RestaurantActionService actionService;

    @Mock
    private com.fooddelivery.restaurant.client.DeliveryClient deliveryClient;

    private FulfillmentService fulfillmentService;

    @BeforeEach
    void setUp() {
        fulfillmentService = new FulfillmentService(outletRepository, restaurantOrderRepository, actionService, deliveryClient);
    }

    @Test
    void readyOrder_ShouldNotThrowException() {
        UUID orderId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        
        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(orderId);
        order.setRestaurantId(restaurantId);
        order.setStatus(OrderStatus.ACCEPTED);

        when(restaurantOrderRepository.findById(orderId)).thenReturn(Optional.of(order));

        assertDoesNotThrow(() -> fulfillmentService.readyOrder(restaurantId, orderId));
    }
}
