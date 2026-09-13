package com.fooddelivery.restaurant.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fooddelivery.common.constants.EventType;
import com.fooddelivery.common.outbox.service.OutboxProcessor;
import com.fooddelivery.common.repository.IIdempotencyKeyRepository;
import com.fooddelivery.restaurant.entity.Outlet;
import com.fooddelivery.restaurant.repository.OutletRepository;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * The outlet's displayed rating is the one number every customer sees before choosing where to eat.
 * These tests pin the two properties that make it trustworthy: it tracks the real aggregate, and a
 * redelivered event cannot move it.
 */
@ExtendWith(MockitoExtension.class)
class ReviewEventConsumerTest {

    private static final UUID OUTLET_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Mock
    private OutletRepository outletRepository;

    @Mock
    private IIdempotencyKeyRepository idempotencyKeyRepository;

    @Mock
    private TransactionTemplate transactionTemplate;

    private MeterRegistry meterRegistry;
    private ReviewEventConsumer consumer;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        consumer = new ReviewEventConsumer(new ObjectMapper(), outletRepository,
                idempotencyKeyRepository, transactionTemplate, meterRegistry);
        lenient().when(transactionTemplate.execute(any())).thenAnswer(invocation ->
                invocation.getArgument(0, TransactionCallback.class).doInTransaction(null));
        lenient().when(idempotencyKeyRepository.tryClaim(anyString())).thenReturn(1);
    }

    @Test
    void writesTheAggregateOntoTheOutlet() {
        Outlet outlet = outletWithStaleRating();
        when(outletRepository.findById(OUTLET_ID)).thenReturn(Optional.of(outlet));

        consumer.consumeReviewEvent(reviewCreated("RESTAURANT", OUTLET_ID.toString(), 7, "4.29"),
                headers("evt-1", EventType.REVIEW_CREATED));

        assertThat(outlet.getRating()).isEqualTo(4.29);
        assertThat(outlet.getReviewsCount()).isEqualTo(7);
        verify(outletRepository).save(outlet);
    }

    /**
     * The property that matters most. The payload carries absolute totals and the consumer assigns
     * them, so replaying an event is a no-op on the value even before de-duplication is considered.
     * An increment-based consumer would drift permanently on a single redelivery — and Kafka
     * guarantees at-least-once, so redelivery is expected, not exceptional.
     */
    @Test
    void aRedeliveredEventDoesNotMoveTheRating() {
        Outlet outlet = outletWithStaleRating();
        when(outletRepository.findById(OUTLET_ID)).thenReturn(Optional.of(outlet));

        String message = reviewCreated("RESTAURANT", OUTLET_ID.toString(), 7, "4.29");
        Map<String, Object> headers = headers("evt-1", EventType.REVIEW_CREATED);

        consumer.consumeReviewEvent(message, headers);
        double afterFirst = outlet.getRating();
        int countAfterFirst = outlet.getReviewsCount();

        // Second delivery of the same event, with the claim now already taken.
        when(idempotencyKeyRepository.tryClaim(anyString())).thenReturn(0);
        consumer.consumeReviewEvent(message, headers);

        assertThat(outlet.getRating()).isEqualTo(afterFirst);
        assertThat(outlet.getReviewsCount()).isEqualTo(countAfterFirst);
        // Once across both deliveries: the duplicate claim short-circuits before any lookup.
        verify(outletRepository, times(1)).findById(OUTLET_ID);
        verify(outletRepository, times(1)).save(outlet);
    }

    /**
     * The same event processed twice with the de-duplication claim <em>succeeding both times</em>.
     *
     * <p>Not a hypothetical: {@code OutboxProcessor.cleanupOutboxEvents} and the central idempotency
     * sweep both delete rows on a retention schedule, so a replay after a rebalance can find the key
     * gone and claim it again. This is the case the claim does not cover, and it is the one that
     * absolute assignment exists for — with an increment here, the count would be 8 instead of 7,
     * permanently, with no error anywhere.
     *
     * <p>Written after observing that {@link #aRedeliveredEventDoesNotMoveTheRating} stays green
     * when the assignment is replaced by an increment: that test is guarded by the claim, so it
     * proves de-duplication, not idempotence.
     */
    @Test
    void reprocessingAfterTheDeduplicationKeyIsSweptStillLandsOnTheSameValue() {
        Outlet outlet = outletWithStaleRating();
        when(outletRepository.findById(OUTLET_ID)).thenReturn(Optional.of(outlet));

        String message = reviewCreated("RESTAURANT", OUTLET_ID.toString(), 7, "4.29");
        Map<String, Object> headers = headers("evt-swept", EventType.REVIEW_CREATED);

        consumer.consumeReviewEvent(message, headers);
        consumer.consumeReviewEvent(message, headers);

        assertThat(outlet.getReviewsCount()).isEqualTo(7);
        assertThat(outlet.getRating()).isEqualTo(4.29);
        verify(outletRepository, times(2)).save(outlet);
    }

    @Test
    void aDriverReviewNeverTouchesAnOutlet() {
        consumer.consumeReviewEvent(
                reviewCreated("DRIVER", UUID.randomUUID().toString(), 3, "4.00"),
                headers("evt-2", EventType.REVIEW_CREATED));

        verify(outletRepository, never()).findById(any());
        verify(outletRepository, never()).save(any());
    }

    @Test
    void aProductReviewNeverTouchesAnOutlet() {
        consumer.consumeReviewEvent(
                reviewCreated("PRODUCT", UUID.randomUUID().toString(), 3, "4.00"),
                headers("evt-3", EventType.REVIEW_CREATED));

        verify(outletRepository, never()).findById(any());
        verify(outletRepository, never()).save(any());
    }

    /** A deleted outlet is a normal end state. Retrying cannot bring it back, so it is dropped. */
    @Test
    void anUnknownOutletIsDroppedRatherThanRetried() {
        when(outletRepository.findById(OUTLET_ID)).thenReturn(Optional.empty());

        consumer.consumeReviewEvent(reviewCreated("RESTAURANT", OUTLET_ID.toString(), 1, "5.00"),
                headers("evt-4", EventType.REVIEW_CREATED));

        verify(outletRepository, never()).save(any());
    }

    /** No eventId means no de-duplication key, so processing blind is worse than dead-lettering. */
    @Test
    void aMissingEventIdHeaderIsDeadLettered() {
        Map<String, Object> headers = new HashMap<>();
        headers.put(OutboxProcessor.HEADER_EVENT_TYPE,
                EventType.REVIEW_CREATED.name().getBytes(StandardCharsets.UTF_8));

        assertThatThrownBy(() -> consumer.consumeReviewEvent(
                reviewCreated("RESTAURANT", OUTLET_ID.toString(), 1, "5.00"), headers))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("eventId");
    }

    private static Outlet outletWithStaleRating() {
        // What onboarding wrote and nothing has changed since -- the number this consumer exists
        // to replace.
        Outlet outlet = new Outlet();
        outlet.setId(OUTLET_ID);
        outlet.setRating(0.0);
        outlet.setReviewsCount(0);
        return outlet;
    }

    private static String reviewCreated(String entityType, String entityId,
                                        long totalReviews, String averageRating) {
        return """
                {"reviewId":"%s","orderId":"%s","entityType":"%s","entityId":"%s",
                 "userId":"%s","rating":5,"totalReviews":%d,"averageRating":"%s",
                 "timestamp":"2026-09-11T10:15:30Z"}
                """.formatted(UUID.randomUUID(), UUID.randomUUID(), entityType, entityId,
                UUID.randomUUID(), totalReviews, averageRating);
    }

    private static Map<String, Object> headers(String eventId, EventType eventType) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("eventId", eventId.getBytes(StandardCharsets.UTF_8));
        headers.put(OutboxProcessor.HEADER_EVENT_TYPE,
                eventType.name().getBytes(StandardCharsets.UTF_8));
        return headers;
    }
}
