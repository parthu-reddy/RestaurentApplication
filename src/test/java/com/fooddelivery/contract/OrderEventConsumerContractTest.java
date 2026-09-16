package com.fooddelivery.contract;

import com.fooddelivery.common.contract.KafkaStubMessageSender;
import com.fooddelivery.restaurant.config.DeliveryZoneConfig;
import com.fooddelivery.restaurant.service.OrderEventConsumer;
import com.fooddelivery.common.repository.IIdempotencyKeyRepository;
import com.fooddelivery.restaurant.repository.RestaurantOrderRepository;
import com.fooddelivery.restaurant.service.state.RestaurantActionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.StubTrigger;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.verifier.messaging.MessageVerifierSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = OrderEventConsumerContractTest.TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration,org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration,org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration,org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration")
@ActiveProfiles("contract-test")
@AutoConfigureStubRunner(ids = "com.fooddelivery:food-delivery-backend:+:stubs")
@org.springframework.test.annotation.DirtiesContext(classMode = org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_CLASS)
@EmbeddedKafka(partitions = 1, topics = {"order-events"})
class OrderEventConsumerContractTest {

    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration
    
    @Import({OrderEventConsumer.class, DeliveryZoneConfig.class})
    static class TestConfig {
        @Bean
        public MessageVerifierSender<Message<?>> kafkaStubMessageSender(KafkaTemplate<String, String> t) {
            return new KafkaStubMessageSender(t);
        }

        @Bean
        public TransactionTemplate transactionTemplate() {
            PlatformTransactionManager tm = Mockito.mock(PlatformTransactionManager.class);
            Mockito.when(tm.getTransaction(any())).thenReturn(new SimpleTransactionStatus());
            return new TransactionTemplate(tm);
        }

        @Bean
        public MeterRegistry meterRegistry() {
            return new SimpleMeterRegistry();
        }

        @Bean
        public com.fooddelivery.common.event.EventBinder eventBinder(com.fasterxml.jackson.databind.ObjectMapper objectMapper) {
            jakarta.validation.Validator validator = jakarta.validation.Validation.buildDefaultValidatorFactory().getValidator();
            return new com.fooddelivery.common.event.EventBinder(objectMapper, validator);
        }
    }

    @MockBean
    private IIdempotencyKeyRepository idempotencyKeyRepository;

    @MockBean
    private RestaurantOrderRepository restaurantOrderRepository;

    @MockBean
    private RestaurantActionService actionService;

    @MockBean
    private StringRedisTemplate redisTemplate;

    @Test
    void consumesOrderCreatedAndGracefullyIgnoresIfOrderNotFound() {
        Mockito.when(idempotencyKeyRepository.tryClaim(anyString())).thenReturn(1);
        Mockito.when(restaurantOrderRepository.findById(any())).thenReturn(Optional.empty());

        stubTrigger.trigger("order_created");

        await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
            // idempotency is claimed with INSERT .. ON CONFLICT DO NOTHING, not save().
            // Assert the call, not the key format -- the format is deliberately not part of the contract.
            // This is what proves the message was actually consumed.
            verify(idempotencyKeyRepository).tryClaim(anyString());

            // ...and this is what proves it was ignored gracefully: nothing was written.
            //
            // It used to assert findById() instead. That passed only because the untyped consumer
            // looked EVERY order event up before deciding whether it handled it -- ORDER_CREATED
            // is not a RestaurantApplication concern at all (the restaurant-side order is created
            // on ORDER_PAID / ORDER_PLACED_COD), so the lookup was a wasted query per event and
            // the "graceful ignore" happened after it. Typed binding rejects an unmapped event
            // type before touching the database, which is the better behaviour, so the assertion
            // now pins the outcome the contract actually cares about rather than the mechanism.
            verify(actionService, org.mockito.Mockito.never()).saveOrder(org.mockito.ArgumentMatchers.any());
            org.mockito.Mockito.verifyNoInteractions(restaurantOrderRepository);
        });
    }

    @Autowired
    private StubTrigger stubTrigger;
}
