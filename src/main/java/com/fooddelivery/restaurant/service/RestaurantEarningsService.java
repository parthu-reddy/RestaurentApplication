package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.restaurant.client.WalletClient;
import com.fooddelivery.restaurant.dto.WalletDto;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class RestaurantEarningsService {
    @java.lang.SuppressWarnings("all")
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestaurantEarningsService.class);
    private final WalletClient walletClient;

    public RestaurantEarningsService(WalletClient walletClient) {
        this.walletClient = walletClient;
    }

    public WalletDto getRestaurantBalance(UUID restaurantId) {
        try {
            return walletClient.getWallet("RESTAURANT", restaurantId);
        } catch (Exception e) {
            log.error("Failed to fetch balance for restaurant {}", restaurantId, e);
            throw new RuntimeException("Failed to fetch wallet balance", e);
        }
    }
    // In a real application, an EARNINGS_GENERATED event would be published via Kafka here
    // or credit would be directly invoked when HANDED_OVER occurs.
    // walletClient.credit("RESTAURANT", restaurantId, txRequest);
}
