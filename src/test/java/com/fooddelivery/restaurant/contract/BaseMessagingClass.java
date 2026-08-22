package com.fooddelivery.restaurant.contract;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.verifier.messaging.boot.AutoConfigureMessageVerifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.context.annotation.Bean;

@SpringBootTest(classes = BaseMessagingClass.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"})
@org.springframework.test.context.ActiveProfiles("contract-test")
@AutoConfigureMessageVerifier
@EmbeddedKafka(partitions = 1, topics = {"restaurant-events", "menu-events"})
public abstract class BaseMessagingClass {

    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration
    static class TestConfig {
        @Bean
        public KafkaMessageVerifier kafkaMessageVerifier() {
            return new KafkaMessageVerifier();
        }
    }

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", () -> System.getProperty("spring.embedded.kafka.brokers", "localhost:9092"));
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private com.fasterxml.jackson.databind.ObjectMapper objectMapper;

    /** Mirrors RestaurantOnboardingService's BRAND_CREATED payload (BRAND aggregate). */
    public void fireRestaurantAccepted() throws Exception {
        String brandId = "9c8b7a65-1e2d-4f30-b5a6-7c8d9e0f1a23";
        java.util.Map<String, Object> payload = java.util.Map.of(
                "brandId", brandId,
                "brandName", "Pizza Hub",
                "gstin", "29ABCDE1234F1Z5",
                "bankAccountNumber", "1234567890",
                "ifscCode", "HDFC0001234",
                "timestamp", java.time.LocalDateTime.now().toString());
        publishViaOutbox(com.fooddelivery.common.constants.AggregateType.BRAND, brandId,
                com.fooddelivery.common.constants.EventType.BRAND_CREATED, payload);
    }
    /** Mirrors CatalogService.notifyMenuUpdate exactly (private method, so replicated here). */
    public void fireMenuUpdated() {
        java.util.UUID brandId = java.util.UUID.fromString("9c8b7a65-1e2d-4f30-b5a6-7c8d9e0f1a23");
        String payload = String.format("{\"brandId\":\"%s\",\"type\":\"MENU_UPDATED\",\"timestamp\":\"%s\"}",
                brandId, java.time.Instant.now().toString());
        kafkaTemplate.send(com.fooddelivery.common.constants.KafkaConstants.TOPIC_MENU_EVENTS,
                brandId.toString(), payload);
    }


    /** Drives the real OutboxProcessor: real topic routing, real key, real eventType header. */
    protected void publishViaOutbox(com.fooddelivery.common.constants.AggregateType aggregateType,
                                    String aggregateId,
                                    com.fooddelivery.common.constants.EventType eventType,
                                    Object payloadObject) throws Exception {
        com.fooddelivery.common.outbox.entity.OutboxEventEntity outboxEvent =
                com.fooddelivery.common.outbox.entity.OutboxEventEntity.builder()
                        .id(java.util.UUID.randomUUID())
                        .aggregateType(aggregateType)
                        .aggregateId(aggregateId)
                        .eventType(eventType)
                        .payload(payloadObject instanceof String
                                ? (String) payloadObject
                                : objectMapper.writeValueAsString(payloadObject))
                        .createdAt(java.time.LocalDateTime.now())
                        .build();
        com.fooddelivery.common.outbox.repository.OutboxEventRepository repo =
                org.mockito.Mockito.mock(com.fooddelivery.common.outbox.repository.OutboxEventRepository.class);
        org.mockito.Mockito.when(repo.findTop100ByStatusInOrderByCreatedAtAsc(org.mockito.ArgumentMatchers.anyList()))
                .thenReturn(new java.util.ArrayList<>(java.util.List.of(outboxEvent)));
        new com.fooddelivery.common.outbox.service.OutboxProcessor(repo, kafkaTemplate, new io.micrometer.core.instrument.simple.SimpleMeterRegistry()).processOutboxEvents();
    }

}
