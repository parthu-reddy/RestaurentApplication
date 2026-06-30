package com.fooddelivery.restaurant.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import com.fooddelivery.restaurant.repository.IRestaurantRepository;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FulfillmentServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private IRestaurantRepository restaurantRepository;

    @Mock
    private StringRedisTemplate redisTemplate;
    
    @Mock
    private ValueOperations<String, String> valueOperations;

    private FulfillmentService fulfillmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fulfillmentService = new FulfillmentService(kafkaTemplate, restaurantRepository, redisTemplate);
        org.mockito.Mockito.lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void acceptOrder_ShouldPublishKafkaEvent() {
        UUID restaurantId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        
        com.fooddelivery.restaurant.entity.Restaurant restaurant = new com.fooddelivery.restaurant.entity.Restaurant();
        restaurant.setId(restaurantId);
        org.locationtech.jts.geom.GeometryFactory factory = new org.locationtech.jts.geom.GeometryFactory();
        restaurant.setLocation(factory.createPoint(new org.locationtech.jts.geom.Coordinate(77.5946, 12.9716)));
        
        org.mockito.Mockito.when(restaurantRepository.findById(restaurantId))
            .thenReturn(java.util.Optional.of(restaurant));
        
        org.mockito.Mockito.when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        org.mockito.Mockito.when(valueOperations.get(org.mockito.ArgumentMatchers.anyString())).thenReturn("20");

        fulfillmentService.acceptOrder(restaurantId, orderId, null, null);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("order-events"), eq(orderId.toString()), payloadCaptor.capture());

        String payload = payloadCaptor.getValue();
        assertThat(payload).contains("ORDER_ACCEPTED");
        assertThat(payload).contains(orderId.toString());
        assertThat(payload).contains(restaurantId.toString());
        assertThat(payload).contains("12.9716"); // Lat
        assertThat(payload).contains("77.5946"); // Lng
    }

    @Test
    void rejectOrder_ShouldPublishKafkaEvent() {
        UUID restaurantId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        fulfillmentService.rejectOrder(restaurantId, orderId);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("order-events"), eq(orderId.toString()), payloadCaptor.capture());

        String payload = payloadCaptor.getValue();
        assertThat(payload).contains("ORDER_REJECTED");
        assertThat(payload).contains(orderId.toString());
        assertThat(payload).contains(restaurantId.toString());
    }

    @Test
    void readyOrder_ShouldPublishKafkaEvent() {
        UUID restaurantId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        fulfillmentService.readyOrder(restaurantId, orderId);

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(kafkaTemplate).send(eq("order-events"), eq(orderId.toString()), payloadCaptor.capture());

        String payload = payloadCaptor.getValue();
        assertThat(payload).contains("ORDER_READY");
        assertThat(payload).contains(orderId.toString());
        assertThat(payload).contains(restaurantId.toString());
    }
}
