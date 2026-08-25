package com.fooddelivery.contract;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.openfeign.EnableFeignClients;

@ActiveProfiles("contract-test")
@SpringBootTest(classes = RestaurantContractConsumerTest.TestConfig.class, webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
    // Stub ids are Maven artifactIds; Feign resolves by spring.application.name. These two
    // differ for these services, so the stub must be registered under the name the client asks for.
    "stubrunner.idsToServiceIds.food-delivery-backend=customer-service",
    "stubrunner.idsToServiceIds.delivery-executive-application=delivery-service"
})
@AutoConfigureStubRunner(ids = { "com.fooddelivery:food-delivery-backend:+:stubs:8090", "com.fooddelivery:delivery-executive-application:+:stubs:8092" }, stubsMode = StubRunnerProperties.StubsMode.LOCAL)
public class RestaurantContractConsumerTest {

    @MockBean
    private com.fooddelivery.restaurant.client.OrderClientFallback orderClientFallback;

    @MockBean
    private com.fooddelivery.restaurant.client.DeliveryClientFallback deliveryClientFallback;

    @Autowired
    private com.fooddelivery.restaurant.client.OrderClient orderClient;
    @Autowired
    private com.fooddelivery.restaurant.client.DeliveryClient deliveryClient;


    @org.springframework.boot.SpringBootConfiguration
    @org.springframework.boot.autoconfigure.EnableAutoConfiguration(exclude = {
            DataSourceAutoConfiguration.class,
            DataSourceTransactionManagerAutoConfiguration.class,
            HibernateJpaAutoConfiguration.class
    })
    @EnableFeignClients(basePackages = "com.fooddelivery.restaurant.client")
    static class TestConfig {
    }

    @Test
    public void testInitiatePartialRefund() {
        Map<String, String> payload = new java.util.HashMap<>();
        // Mirrors FulfillmentService.initiatePartialRefund, which sends {"amount": ...} and nothing else.
        payload.put("amount", "25.00");

        org.springframework.http.ResponseEntity<Map<String, String>> response = orderClient.initiatePartialRefund(
                java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000"), payload);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        // partialRefund returns ApiResponse.success(...) -> {success, message, data}.
        // the Feign client is typed Map<String, String>, so this arrives as the string "true"
        assertEquals("true", response.getBody().get("success"));
    }

    @Test
    public void testGetOrderInvoice() {
        org.springframework.http.ResponseEntity<Map<String, Object>> response = orderClient.getOrderInvoice(
                java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        // getOrderInvoice returns OrderResponse; there is no invoiceId field, and the money
        // field is totalAmount.
        assertEquals(100.0, response.getBody().get("totalAmount"));
    }

    @Test
    public void testGetDriverById() {
        org.springframework.http.ResponseEntity<Map<String, Object>> response = deliveryClient.getDriverById(
                java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000"));

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        // DeliveryExecutive serialises fullName, not name, and DeliveryExecutiveStatus is
        // OFFLINE | ONLINE | ON_DELIVERY -- there is no AVAILABLE. The contract has been correct
        // since 2026-08-20; these assertions had not caught up.
        assertEquals("Test Driver", response.getBody().get("fullName"));
        assertEquals("ONLINE", response.getBody().get("status"));
    }

    @Test
    public void testGetDriversByIds() {
        java.util.List<java.util.UUID> ids = java.util.Arrays.asList(
                java.util.UUID.fromString("123e4567-e89b-12d3-a456-426614174000")
        );
        org.springframework.http.ResponseEntity<java.util.List<Map<String, Object>>> response = deliveryClient.getDriversByIds(ids);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Driver", response.getBody().get(0).get("fullName"));
    }


}
