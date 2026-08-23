package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.common.client.WalletServiceClient;
import com.fooddelivery.common.dto.wallet.WalletDto;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
@lombok.extern.slf4j.Slf4j
@lombok.RequiredArgsConstructor
public class RestaurantEarningsService {
private final WalletServiceClient walletClient;


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
