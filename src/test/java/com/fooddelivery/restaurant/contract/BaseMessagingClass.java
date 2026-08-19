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

@SpringBootTest(classes = BaseMessagingClass.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureMessageVerifier
@EmbeddedKafka(partitions = 1, topics = {"restaurant-events", "menu-events"})
public abstract class BaseMessagingClass {

    @org.springframework.boot.test.context.TestConfiguration
    
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

    public void fireRestaurantAccepted() {
        String payload = """
{
  "eventId": "res-222",
  "type": "ORDER_ACCEPTED",
  "payload": {
    "orderId": 1001,
    "restaurantId": 501
  }
}""";
        kafkaTemplate.send("restaurant-events", payload);
    }
    public void fireMenuUpdated() {
        String payload = """
{
  "eventId": "menu-333",
  "type": "MENU_UPDATED",
  "payload": {
    "restaurantId": 501,
    "itemId": "item-88"
  }
}""";
        kafkaTemplate.send("menu-events", payload);
    }

}
