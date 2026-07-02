package com.fooddelivery.restaurant.service.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.common.constants.AppConstants;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.common.outbox.entity.OutboxEventEntity;
import com.fooddelivery.common.outbox.repository.OutboxEventRepository;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.entity.RestaurantOrder;
import com.fooddelivery.restaurant.repository.OutletRepository;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderDelayApprovedStrategy implements RestaurantEventStrategy {

    private final RestaurantOrderRepository restaurantOrderRepository;
    private final OutletRepository outletRepository;
    private final ObjectMapper objectMapper;
    private final OutboxEventRepository outboxEventRepository;

    @Override
    public void process(JsonNode root) throws Exception {
        String orderId = root.path("orderId").asText();
        String restaurantId = root.path("restaurantId").asText();
        
        RestaurantOrder order = restaurantOrderRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));
        
        int basePrepTime = order.getPrepTime() != null ? order.getPrepTime() : 15;
        int additionalPrepTime = order.getAdditionalPrepTime() != null ? order.getAdditionalPrepTime() : 0;
        
        int finalPrepTime = basePrepTime + additionalPrepTime;
        long estimatedCompletionTime = System.currentTimeMillis() + (finalPrepTime * 60 * 1000L);
        
        // Fetch restaurant to get Lat/Lng
        Outlet restaurant = outletRepository.findById(UUID.fromString(restaurantId))
                .orElse(null);
        double lat = 0.0;
        double lng = 0.0;
        if (restaurant != null && restaurant.getLocation() != null) {
            lat = restaurant.getLocation().getY();
            lng = restaurant.getLocation().getX();
        }
        
        double deliveryLat = order.getDeliveryLat() != null ? order.getDeliveryLat() : 0.0;
        double deliveryLng = order.getDeliveryLng() != null ? order.getDeliveryLng() : 0.0;
        String deliveryAddress = order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "";
        
        com.fasterxml.jackson.databind.node.ObjectNode payloadNode = objectMapper.createObjectNode();
        payloadNode.put("eventType", EventType.ORDER_ACCEPTED);
        payloadNode.put("orderId", orderId);
        payloadNode.put("restaurantId", restaurantId);
        payloadNode.put("restaurantLat", lat);
        payloadNode.put("restaurantLng", lng);
        payloadNode.put("estimatedCompletionTime", estimatedCompletionTime);
        payloadNode.put("estimatedPrepTimeMinutes", finalPrepTime);
        payloadNode.put("deliveryLat", deliveryLat);
        payloadNode.put("deliveryLng", deliveryLng);
        payloadNode.put("deliveryAddress", deliveryAddress);
        String payload = objectMapper.writeValueAsString(payloadNode);
        
        OutboxEventEntity outbox = OutboxEventEntity.builder()
                .id(UUID.randomUUID())
                .aggregateType(AppConstants.AGGREGATE_ORDER)
                .aggregateId(orderId)
                .eventType(EventType.ORDER_ACCEPTED)
                .payload(payload)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        outboxEventRepository.save(outbox);
        
        order.setStatus("ACCEPTED");
        restaurantOrderRepository.save(order);
        log.info("Saved ORDER_ACCEPTED to outbox for order {} after delay approval with estimatedCompletionTime {}", orderId, estimatedCompletionTime);
    }

    @Override
    public String getEventType() {
        return EventType.ORDER_DELAY_APPROVED;
    }
}
