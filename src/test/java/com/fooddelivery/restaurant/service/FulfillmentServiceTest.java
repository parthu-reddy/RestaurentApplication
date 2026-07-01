package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import com.fooddelivery.restaurant.repository.OutletRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class FulfillmentServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private OutletRepository outletRepository;

    @Mock
    private StringRedisTemplate redisTemplate;
    
    @Mock
    private ValueOperations<String, String> valueOperations;

    private ObjectMapper objectMapper = new ObjectMapper();

    private FulfillmentService fulfillmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fulfillmentService = new FulfillmentService(kafkaTemplate, outletRepository, objectMapper, redisTemplate);
        org.mockito.Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        org.mockito.Mockito.lenient().when(kafkaTemplate.send(anyString(), anyString(), anyString()))
                .thenReturn(CompletableFuture.completedFuture(null));
    }

    @Test
    void acceptOrder_ShouldPublishKafkaEvent() {
        UUID outletId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        
        com.fooddelivery.restaurant.entity.Outlet outlet = new com.fooddelivery.restaurant.entity.Outlet();
        outlet.setId(outletId);
        org.locationtech.jts.geom.GeometryFactory factory = new org.locationtech.jts.geom.GeometryFactory();
        outlet.setLocation(factory.createPoint(new org.locationtech.jts.geom.Coordinate(77.5946, 12.9716)));
        
        org.mockito.Mockito.when(outletRepository.findById(outletId))
            .thenReturn(java.util.Optional.of(outlet));
        
        org.mockito.Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        org.mockito.Mockito.when(valueOperations.get(org.mockito.ArgumentMatchers.anyString())).thenReturn("20");

        fulfillmentService.acceptOrder(outletId, orderId, null, null);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS), eq(orderId.toString()), payloadCaptor.capture());

        String payload = payloadCaptor.getValue();
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
        
        org.mockito.Mockito.when(outletRepository.findById(outletId))
            .thenReturn(java.util.Optional.of(outlet));
            
        fulfillmentService.acceptOrder(outletId, orderId, 15, "Too busy");
        
        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS), eq(orderId.toString()), payloadCaptor.capture());

        String payload = payloadCaptor.getValue();
        assertThat(payload).contains("ORDER_DELAY_APPROVAL_REQUESTED");
        assertThat(payload).contains("15");
    }

    @Test
    void rejectOrder_ShouldPublishKafkaEvent() {
        UUID outletId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        fulfillmentService.rejectOrder(outletId, orderId);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS), eq(orderId.toString()), payloadCaptor.capture());

        String payload = payloadCaptor.getValue();
        assertThat(payload).contains(com.fooddelivery.common.constants.EventType.ORDER_REJECTED);
        assertThat(payload).contains(orderId.toString());
        assertThat(payload).contains(outletId.toString());
    }

    @Test
    void readyOrder_ShouldPublishKafkaEvent() {
        UUID outletId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        fulfillmentService.readyOrder(outletId, orderId);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq(com.fooddelivery.common.constants.KafkaConstants.TOPIC_ORDER_EVENTS), eq(orderId.toString()), payloadCaptor.capture());

        String payload = payloadCaptor.getValue();
        assertThat(payload).contains(com.fooddelivery.common.constants.EventType.ORDER_READY);
        assertThat(payload).contains(orderId.toString());
        assertThat(payload).contains(outletId.toString());
    }
}
