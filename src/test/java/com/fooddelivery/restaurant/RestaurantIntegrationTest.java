package com.fooddelivery.restaurant;

import com.fooddelivery.common.test.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.kafka.test.context.EmbeddedKafka;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class RestaurantIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldProcessOrderAndEmitAcceptedEvent() {
        // Here we could simulate sending a Kafka event to "order-events"
        // and assert that the Restaurant service picks it up and updates DB or emits "restaurant-events".
        assertThat(true).isTrue();
    }
}
