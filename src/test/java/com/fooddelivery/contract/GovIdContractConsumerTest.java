package com.fooddelivery.contract;

import com.fooddelivery.common.client.GovernmentIdServiceClient;
import com.fooddelivery.common.dto.governmentid.BankAccountRequest;
import com.fooddelivery.common.dto.governmentid.GstinRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * RestaurantApplication consumes GovernmentIDValidationService's brand KYC endpoints from
 * RestaurantKycController. These calls return void, so the assertion is that the Feign client
 * resolves the stub without error -- which proves the URL, verb and request payload shape all
 * match the producer's contract.
 */
@SpringBootTest(classes = GovIdContractConsumerTest.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@AutoConfigureStubRunner(
        stubsMode = StubRunnerProperties.StubsMode.LOCAL,
        ids = {"com.fooddelivery:government-id-validation-service:+:stubs:8094"}
)
@ActiveProfiles("contract-test")
public class GovIdContractConsumerTest {

    @Configuration
    @EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })
    @EnableFeignClients(basePackages = {"com.fooddelivery.common.client"})
    static class TestConfig {
        @Bean
        public com.fooddelivery.common.client.GovernmentIdServiceClientFallback governmentIdServiceClientFallback() {
            return Mockito.mock(com.fooddelivery.common.client.GovernmentIdServiceClientFallback.class);
        }
    }

    @MockBean
    private org.springframework.kafka.core.KafkaTemplate kafkaTemplate;

    @Autowired
    private GovernmentIdServiceClient governmentIdServiceClient;

    @Test
    public void shouldVerifyBrandGstin() {
        GstinRequest request = new GstinRequest();
        request.setBrandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        request.setGstin("29ABCDE1234F1Z5");
        request.setBrandName("Pizza Hub");

        assertDoesNotThrow(() -> governmentIdServiceClient.verifyGstin(request));
    }

    @Test
    public void shouldVerifyBrandBankAccount() {
        BankAccountRequest request = new BankAccountRequest();
        request.setBrandId(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
        request.setAccountNumber("1234567890");
        request.setIfscCode("HDFC0001234");
        request.setBrandName("Pizza Hub");

        assertDoesNotThrow(() -> governmentIdServiceClient.verifyBrandBankAccount(request));
    }
}
