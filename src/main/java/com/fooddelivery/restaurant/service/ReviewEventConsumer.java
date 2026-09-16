package com.fooddelivery.restaurant.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.common.constants.EventPayloadConstants;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.common.constants.KafkaConstants;
import com.fooddelivery.common.repository.IIdempotencyKeyRepository;
import com.fooddelivery.common.util.KafkaHeaderUtils;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.OutletRepository;

import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Keeps an outlet's displayed rating true.
 *
 * <p>{@code Outlet.rating} and {@code Outlet.reviews_count} were written exactly once, by
 * {@code RestaurantOnboardingService.onboardOutlet}, from a number supplied in the onboarding
 * request — and never again. The customer's restaurant card has been rendering that number ever
 * since. Meanwhile reviews-service maintained the real aggregate and published it to
 * {@code review-events}, which nothing consumed.
 *
 * <p><strong>Why consume rather than query.</strong> A restaurant feed renders around twenty
 * outlets. Asking reviews-service per card is twenty round trips on the hot path and makes browsing
 * depend on a service it has never needed. The event already carries the answer.
 *
 * <p><strong>Why absolute values.</strong> The payload carries {@code totalReviews} and
 * {@code averageRating} as the aggregate's state <em>after</em> the write, not a delta, and this
 * consumer assigns them. A redelivered event is therefore idempotent by construction rather than
 * only by the de-duplication claim below — which still runs, so that a duplicate is visible in the
 * log rather than silently absorbed. Ordering makes assignment safe: the producer keys the record
 * by {@code entityId}, so two reviews for one outlet cannot be reordered against each other.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewEventConsumer {

    private final ObjectMapper objectMapper;
    private final com.fooddelivery.common.event.EventBinder eventBinder;
    private final OutletRepository outletRepository;
    private final IIdempotencyKeyRepository idempotencyKeyRepository;
    private final TransactionTemplate transactionTemplate;
    private final MeterRegistry meterRegistry;

    /** Entity type this service has a home for. PRODUCT and DRIVER are read live instead. */
    private static final String ENTITY_TYPE_RESTAURANT = "RESTAURANT";

    // No `include` list: the default already retries every exception, so naming
    // ObjectOptimisticLockingFailureException and RuntimeException added nothing -- and a class in
    // `include` is CLASSIFIED, which stops traversingCauses before it reaches the excluded
    // EventBindingException underneath a wrapping catch. See BindingFailureIsNotRetryableTest.
    @RetryableTopic(
            attempts = "5",
            backoff = @Backoff(delay = 100, multiplier = 2.0, maxDelay = 2000),
            exclude = {com.fooddelivery.common.event.EventBindingException.class},
            traversingCauses = "true")
    @KafkaListener(
            topics = KafkaConstants.TOPIC_REVIEW_EVENTS,
            groupId = KafkaConstants.GROUP_RESTAURANT_SERVICE + "-revieweventconsumer")
    public void consumeReviewEvent(String message, @Headers java.util.Map<String, Object> headers) {
        String eventId = KafkaHeaderUtils.extractHeaderValue(headers, "eventId");
        if (eventId == null) {
            // Without it there is no de-duplication key. Dead-letter rather than process blind.
            log.error("Missing eventId header in ReviewEventConsumer, sending to DLT.");
            throw new IllegalArgumentException("Missing eventId header");
        }

        String idempotencyKey = "processed_event:restaurant:" + eventId;

        transactionTemplate.execute(status -> {
            // Atomic claim: INSERT .. ON CONFLICT DO NOTHING. exists-then-save is a check-then-act
            // race in which two consumers both observe "absent".
            if (idempotencyKeyRepository.tryClaim(idempotencyKey) == 0) {
                log.info("Duplicate review event (key={}), ignoring.", idempotencyKey);
                return null;
            }

            try {
                JsonNode root = objectMapper.readTree(message);
                String eventType = KafkaHeaderUtils.extractEventType(headers, root);

                if (!EventType.REVIEW_CREATED.name().equals(eventType)) {
                    log.debug("Review event type {} is not handled here. Ignoring.", eventType);
                    return null;
                }
                
                com.fooddelivery.common.event.ReviewCreatedEvent event = eventBinder.bindIf(
                        EventType.REVIEW_CREATED, eventType, message, com.fooddelivery.common.event.ReviewCreatedEvent.class).orElse(null);
                if (event == null) return null;

                String entityType = event.getEntityType();
                if (!ENTITY_TYPE_RESTAURANT.equals(entityType)) {
                    // PRODUCT and DRIVER aggregates have no denormalised home here, and giving the
                    // driver one would put performance data about a person in the catalogue service.
                    log.debug("Review event for entityType={} is not an outlet. Ignoring.", entityType);
                    return null;
                }

                UUID outletId = event.getEntityId();
                if (outletId == null) {
                    log.warn("REVIEW_EVENT_INVALID eventId={} reason=missing-entity-id",
                            com.fooddelivery.common.util.KafkaHeaderUtils.extractHeaderValue(headers, "eventId"));
                    return null;
                }

                Outlet outlet = outletRepository.findById(outletId).orElse(null);
                if (outlet == null) {
                    // The outlet is gone. Retrying will not bring it back, so this is dropped rather
                    // than dead-lettered — a deleted outlet is a normal end state, not a fault.
                    log.warn("REVIEW_CREATED for unknown outlet {}; ignoring.", outletId);
                    return null;
                }

                long totalReviews = event.getTotalReviews() != null ? event.getTotalReviews() : 0L;
                String averageRating = event.getAverageRating();
                if (averageRating == null) {
                    log.warn("REVIEW_CREATED for outlet {} carried no averageRating; ignoring.", outletId);
                    return null;
                }

                outlet.setRating(new BigDecimal(averageRating).doubleValue());
                outlet.setReviewsCount(toIntSaturating(totalReviews, outletId));
                outletRepository.save(outlet);

                meterRegistry.counter("reviews.outlet.rating.updated").increment();
                log.info("Outlet {} rating updated to {} over {} review(s)",
                        outletId, averageRating, totalReviews);
                return null;

            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                // Malformed payload. No retry will repair it; let it dead-letter and be looked at.
                log.error("REVIEW_EVENT_INVALID eventId={} payloadBytes={} reason=unparseable",
                        com.fooddelivery.common.util.KafkaHeaderUtils.extractHeaderValue(headers, "eventId"),
                        message == null ? 0 : message.getBytes(java.nio.charset.StandardCharsets.UTF_8).length, e);
                throw new IllegalArgumentException("Unparseable review event payload", e);
            }
        });
    }

    @DltHandler
    public void handleDlt(String message, @Headers java.util.Map<String, Object> headers) {
        log.error("REVIEW_EVENT_DLT eventId={} eventType={} payloadBytes={}",
                com.fooddelivery.common.util.KafkaHeaderUtils.extractHeaderValue(headers, "eventId"),
                com.fooddelivery.common.util.KafkaHeaderUtils.extractHeaderValue(headers, "eventType"),
                message == null ? 0 : message.getBytes(java.nio.charset.StandardCharsets.UTF_8).length);
        meterRegistry.counter("kafka.dlt.messages", "service", "restaurant-application").increment();
    }

    /**
     * {@code Outlet.reviewsCount} is an {@code Integer} while the aggregate counts in {@code long}.
     * Saturate rather than throw: an outlet with two billion reviews is a display problem, not a
     * reason to dead-letter an event and stop updating its rating.
     */
    private static int toIntSaturating(long totalReviews, UUID outletId) {
        if (totalReviews > Integer.MAX_VALUE) {
            log.warn("Outlet {} has {} reviews, beyond Integer range; clamping the displayed count.",
                    outletId, totalReviews);
            return Integer.MAX_VALUE;
        }
        return (int) Math.max(0L, totalReviews);
    }
}
