package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import com.fooddelivery.restaurant.repository.OutletRepository;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class FulfillmentServiceTest {

    @Mock
    private OutletRepository outletRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private com.fooddelivery.common.outbox.repository.OutboxEventRepository outboxEventRepository;

    @Mock
    private com.fooddelivery.restaurant.repository.RestaurantOrderRepository restaurantOrderRepository;

    private FulfillmentService fulfillmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fulfillmentService = new FulfillmentService(outletRepository, outboxEventRepository, restaurantOrderRepository, objectMapper);
    }

    @Test
    void acceptOrder_ShouldPublishKafkaEvent() {
        UUID outletId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        
        com.fooddelivery.restaurant.entity.Outlet outlet = new com.fooddelivery.restaurant.entity.Outlet();
        outlet.setId(outletId);
        org.locationtech.jts.geom.GeometryFactory factory = new org.locationtech.jts.geom.GeometryFactory();
        outlet.setLocation(factory.createPoint(new org.locationtech.jts.geom.Coordinate(77.5946, 12.9716)));
        
        com.fooddelivery.restaurant.entity.RestaurantOrder order = new com.fooddelivery.restaurant.entity.RestaurantOrder();
        order.setOrderId(orderId);
        order.setPrepTime(15);
        order.setStatus("CREATED");
        
        org.mockito.Mockito.when(outletRepository.findById(outletId))
            .thenReturn(java.util.Optional.of(outlet));
        org.mockito.Mockito.when(restaurantOrderRepository.findById(orderId))
            .thenReturn(java.util.Optional.of(order));

        fulfillmentService.acceptOrder(outletId, orderId, null, null);

        ArgumentCaptor<com.fooddelivery.common.outbox.entity.OutboxEventEntity> outboxCaptor = ArgumentCaptor.forClass(com.fooddelivery.common.outbox.entity.OutboxEventEntity.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());

        String payload = outboxCaptor.getValue().getPayload();
        assertThat(payload).contains(com.fooddelivery.common.constants.EventType.ORDER_ACCEPTED);
        assertThat(payload).contains(orderId.toString());
        assertThat(payload).contains(outletId.toString());
        assertThat(payload).contains("12.9716"); // Lat
        assertThat(payload).contains("77.5946"); // Lng
    }
    
    @Test
    void acceptOrder_ShouldRequestDelayApprovalForLongDelay() {
        UUID outletId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        
        com.fooddelivery.restaurant.entity.Outlet outlet = new com.fooddelivery.restaurant.entity.Outlet();
        outlet.setId(outletId);
        org.locationtech.jts.geom.GeometryFactory factory = new org.locationtech.jts.geom.GeometryFactory();
        outlet.setLocation(factory.createPoint(new org.locationtech.jts.geom.Coordinate(77.5946, 12.9716)));
        
        com.fooddelivery.restaurant.entity.RestaurantOrder order = new com.fooddelivery.restaurant.entity.RestaurantOrder();
        order.setOrderId(orderId);
        order.setPrepTime(15);
        order.setStatus("CREATED");
        
        org.mockito.Mockito.when(outletRepository.findById(outletId))
            .thenReturn(java.util.Optional.of(outlet));
        org.mockito.Mockito.when(restaurantOrderRepository.findById(orderId))
            .thenReturn(java.util.Optional.of(order));
            
        fulfillmentService.acceptOrder(outletId, orderId, 15, "Too busy");
        
        ArgumentCaptor<com.fooddelivery.common.outbox.entity.OutboxEventEntity> outboxCaptor = ArgumentCaptor.forClass(com.fooddelivery.common.outbox.entity.OutboxEventEntity.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());

        String payload = outboxCaptor.getValue().getPayload();
        assertThat(payload).contains("ORDER_DELAY_APPROVAL_REQUESTED");
        assertThat(payload).contains("15");
    }

    @Test
    void rejectOrder_ShouldPublishKafkaEvent() {
        UUID outletId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        com.fooddelivery.restaurant.entity.RestaurantOrder order = new com.fooddelivery.restaurant.entity.RestaurantOrder();
        order.setOrderId(orderId);
        order.setStatus("CREATED");
        
        org.mockito.Mockito.when(restaurantOrderRepository.findById(orderId))
            .thenReturn(java.util.Optional.of(order));

        fulfillmentService.rejectOrder(outletId, orderId);

        ArgumentCaptor<com.fooddelivery.common.outbox.entity.OutboxEventEntity> outboxCaptor = ArgumentCaptor.forClass(com.fooddelivery.common.outbox.entity.OutboxEventEntity.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());

        String payload = outboxCaptor.getValue().getPayload();
        assertThat(payload).contains(com.fooddelivery.common.constants.EventType.ORDER_REJECTED);
        assertThat(payload).contains(orderId.toString());
        assertThat(payload).contains(outletId.toString());
    }

    @Test
    void readyOrder_ShouldPublishKafkaEvent() {
        UUID outletId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        com.fooddelivery.restaurant.entity.RestaurantOrder order = new com.fooddelivery.restaurant.entity.RestaurantOrder();
        order.setOrderId(orderId);
        order.setStatus("ACCEPTED");
        
        org.mockito.Mockito.when(restaurantOrderRepository.findById(orderId))
            .thenReturn(java.util.Optional.of(order));

        fulfillmentService.readyOrder(outletId, orderId);

        ArgumentCaptor<com.fooddelivery.common.outbox.entity.OutboxEventEntity> outboxCaptor = ArgumentCaptor.forClass(com.fooddelivery.common.outbox.entity.OutboxEventEntity.class);
        verify(outboxEventRepository).save(outboxCaptor.capture());

        String payload = outboxCaptor.getValue().getPayload();
        assertThat(payload).contains(com.fooddelivery.common.constants.EventType.ORDER_READY);
        assertThat(payload).contains(orderId.toString());
        assertThat(payload).contains(outletId.toString());
    }
}
