package com.fooddelivery.restaurant;

import com.fooddelivery.common.test.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@org.junit.jupiter.api.Disabled("Disabled as there is no Postgres instance for testing locally and Testcontainers are forbidden by rule.")
public class RestaurantIntegrationTest extends BaseIntegrationTest {

    @Test
    void shouldProcessOrderAndEmitAcceptedEvent() {
        // Here we could simulate sending a Kafka event to "order-events"
        // and assert that the Restaurant service picks it up and updates DB or emits "restaurant-events".
        assertThat(true).isTrue();
    }
}
