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

    @Mock
    private com.fooddelivery.restaurant.client.OrderClient orderClient;

    private FulfillmentService fulfillmentService;

    @BeforeEach
    void setUp() {
        fulfillmentService = new FulfillmentService(outletRepository, restaurantOrderRepository, actionService, deliveryClient, orderClient);
    }

    @Test
    void readyOrder_ShouldNotThrowException() {
        UUID orderId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        
        RestaurantOrder order = new RestaurantOrder();
        order.setOrderId(orderId);
        order.setRestaurantId(restaurantId);
        order.setStatus(OrderStatus.PREPARING);
        // Finishing cooking early is allowed; the15min rule controls dispatch scheduling.
        order.setEstimatedCompletionTime(System.currentTimeMillis() + 30 * 60_000L);

        // Tenant-scoped: FulfillmentService no longer has findById available to it, because
        // loading an order without its restaurant is what let one outlet drive another's orders.
        when(restaurantOrderRepository.findByOrderIdAndRestaurantId(orderId, restaurantId))
                .thenReturn(Optional.of(order));


        assertDoesNotThrow(() -> fulfillmentService.readyOrder(restaurantId, orderId));
        org.assertj.core.api.Assertions.assertThat(order.getStatus()).isEqualTo(OrderStatus.READY_FOR_PICKUP);
        verify(actionService).saveOrder(order);
        ArgumentCaptor<com.fooddelivery.common.event.OrderReadyEvent> event = ArgumentCaptor.forClass(com.fooddelivery.common.event.OrderReadyEvent.class);
        verify(actionService).publishEvent(eq(orderId.toString()), eq(com.fooddelivery.common.constants.EventType.ORDER_READY), event.capture());
        org.assertj.core.api.Assertions.assertThat(event.getValue().getOrderId()).isEqualTo(orderId.toString());
        org.assertj.core.api.Assertions.assertThat(event.getValue().getRestaurantId()).isEqualTo(restaurantId.toString());
        org.junit.jupiter.api.Assertions.assertThrows(com.fooddelivery.common.exception.IllegalStateTransitionException.class,
                () -> fulfillmentService.readyOrder(restaurantId, orderId));
        verify(actionService, times(1)).saveOrder(order);
        verify(actionService, times(1)).publishEvent(eq(orderId.toString()), eq(com.fooddelivery.common.constants.EventType.ORDER_READY), any(com.fooddelivery.common.event.OrderReadyEvent.class));
    }
}
