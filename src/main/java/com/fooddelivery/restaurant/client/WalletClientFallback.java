package com.fooddelivery.restaurant.client;

import com.fooddelivery.restaurant.dto.WalletDto;
import com.fooddelivery.restaurant.dto.TransactionRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.UUID;

@Component
public class WalletClientFallback implements WalletClient {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(WalletClientFallback.class);

    @Override
    public WalletDto getWallet(String entityType, UUID entityId) {
        log.error("Wallet service is down. Fallback triggered for getWallet for {} {}", entityType, entityId);
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Wallet service is currently unavailable");
    }

    @Override
    public WalletDto credit(String entityType, UUID entityId, TransactionRequest request) {
        log.error("Wallet service is down. Fallback triggered for credit for {} {}", entityType, entityId);
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Wallet service is currently unavailable");
    }

    @Override
    public WalletDto debit(String entityType, UUID entityId, TransactionRequest request) {
        log.error("Wallet service is down. Fallback triggered for debit for {} {}", entityType, entityId);
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Wallet service is currently unavailable");
    }
}
