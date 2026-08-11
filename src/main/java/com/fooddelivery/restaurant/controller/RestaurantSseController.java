package com.fooddelivery.restaurant.controller;

import com.fooddelivery.restaurant.entity.Brand;
import com.fooddelivery.restaurant.service.RestaurantOnboardingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.UUID;
import java.util.List;

@RestController
public class RestaurantSseController {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RestaurantSseController.class);
    private final RestaurantOnboardingService onboardingService;
    private final java.util.concurrent.ScheduledExecutorService scheduler = java.util.concurrent.Executors.newScheduledThreadPool(4);

    public RestaurantSseController(RestaurantOnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @GetMapping(value = "/api/v1/brands/stream", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    @PreAuthorize("hasRole('RESTAURANT')")
    public org.springframework.web.servlet.mvc.method.annotation.SseEmitter streamBrands(java.security.Principal principal) {
        org.springframework.web.servlet.mvc.method.annotation.SseEmitter emitter = new org.springframework.web.servlet.mvc.method.annotation.SseEmitter(600000L); // 10 minutes timeout
        UUID ownerId = UUID.fromString(principal.getName());
        
        java.util.concurrent.ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            try {
                List<Brand> brands = onboardingService.getBrands(ownerId);
                emitter.send(org.springframework.web.servlet.mvc.method.annotation.SseEmitter.event().name("brands-update").data(brands));
            } catch (Exception e) {
                log.warn("Failed to send brands update for owner {}, terminating connection", ownerId);
                emitter.completeWithError(e);
            }
        }, 0, 5, java.util.concurrent.TimeUnit.SECONDS);

        Runnable cleanup = () -> {
            try {
                task.cancel(false);
            } catch (Exception e) {
                log.warn("Error during SSE cleanup for owner: {}", ownerId, e);
            }
        };

        emitter.onCompletion(cleanup);
        emitter.onTimeout(() -> {
            log.info("SSE timeout for owner: {}", ownerId);
            cleanup.run();
            emitter.complete();
        });
        emitter.onError(ex -> {
            log.warn("SSE error for owner: {}", ownerId, ex);
            cleanup.run();
        });

        return emitter;
    }

    @jakarta.annotation.PreDestroy
    public void onDestroy() {
        scheduler.shutdown();
    }
}
