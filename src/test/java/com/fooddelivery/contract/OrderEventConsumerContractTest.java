package com.fooddelivery.contract;

import com.fooddelivery.common.contract.KafkaStubMessageSender;
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
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
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
@AutoConfigureStubRunner(ids = "com.fooddelivery:food-delivery-backend:+:stubs",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
@EmbeddedKafka(partitions = 1, topics = {"order-events"})
class OrderEventConsumerContractTest {

    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration
    @Import(OrderEventConsumer.class)
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
        Mockito.when(idempotencyKeyRepository.existsById(anyString())).thenReturn(false);
        Mockito.when(restaurantOrderRepository.findById(any())).thenReturn(Optional.empty());

        stubTrigger.trigger("order_created");

        await().atMost(15, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(idempotencyKeyRepository).save(any(com.fooddelivery.common.entity.IdempotencyKey.class));
            verify(restaurantOrderRepository).findById(any(java.util.UUID.class));
        });
    }

    @Autowired
    private StubTrigger stubTrigger;
}
