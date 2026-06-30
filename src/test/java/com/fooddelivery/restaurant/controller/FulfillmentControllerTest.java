package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.service.FulfillmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FulfillmentControllerTest {

    @Mock
    private FulfillmentService fulfillmentService;

    @InjectMocks
    private FulfillmentController fulfillmentController;

    private MockMvc mockMvc;
    private UUID restaurantId;
    private UUID orderId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(fulfillmentController).build();
        restaurantId = UUID.randomUUID();
        orderId = UUID.randomUUID();
    }

    @Test
    void acceptOrder_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/fulfillment/orders/{orderId}/accept", restaurantId, orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order accept processed"));
                
        verify(fulfillmentService, org.mockito.Mockito.times(1)).acceptOrder(restaurantId, orderId, null, null);
    }

    @Test
    void rejectOrder_ShouldReturnSuccess() throws Exception {
        mockMvc.perform(post("/api/v1/restaurants/{restaurantId}/fulfillment/orders/{orderId}/reject", restaurantId, orderId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Order rejected by restaurant"));

        verify(fulfillmentService).rejectOrder(restaurantId, orderId);
    }
}
