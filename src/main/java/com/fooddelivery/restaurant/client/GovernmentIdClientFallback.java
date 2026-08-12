package com.fooddelivery.restaurant.client;

import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.Map;

@Component("restaurantGovernmentIdClientFallback")
public class GovernmentIdClientFallback implements GovernmentIdClient {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GovernmentIdClientFallback.class);

    @Override
    public Map<String, String> getPresignedUploadUrl(String docType, String contentType) {
        log.error("GovernmentId service is down. Fallback triggered for getPresignedUploadUrl");
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "GovernmentId service is currently unavailable");
    }

    @Override
    public Map<String, String> getPresignedDownloadUrl(String objectKey) {
        log.error("GovernmentId service is down. Fallback triggered for getPresignedDownloadUrl");
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "GovernmentId service is currently unavailable");
    }

    @Override
    public void verifyGstin(GstinRequest request) {
        log.error("GovernmentId service is down. Fallback triggered for verifyGstin");
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "GovernmentId service is currently unavailable");
    }

    @Override
    public void verifyBankAccount(BankAccountRequest request) {
        log.error("GovernmentId service is down. Fallback triggered for verifyBankAccount");
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "GovernmentId service is currently unavailable");
    }
}
