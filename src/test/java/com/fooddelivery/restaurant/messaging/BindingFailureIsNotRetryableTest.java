package com.fooddelivery.restaurant.messaging;

import com.fooddelivery.common.event.EventBindingException;

import org.junit.jupiter.api.Test;
import org.springframework.classify.BinaryExceptionClassifier;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A payload that cannot bind must not be retried.
 *
 * <p>Retrying it is pure waste — the bytes are identical on every attempt — and on the order and
 * money topics it delays the DLT signal by three to five backed-off attempts. The consumers express
 * this with {@code @RetryableTopic(exclude = {EventBindingException.class}, traversingCauses =
 * "true")}.
 *
 * <p>This test exercises the classifier those attributes configure, because two things about it are
 * easy to get wrong and neither is visible by reading the annotation:
 *
 * <ol>
 *   <li><b>Wrapping.</b> Nine consumers catch {@code Exception} and rethrow
 *       {@code new RuntimeException(e)}. Without {@code traversingCauses} the classifier sees only
 *       the outermost type and the exclusion never matches — the configuration would look correct
 *       and do nothing.</li>
 *   <li><b>include vs exclude.</b> RestaurantApplication's listener declares
 *       {@code include = {ObjectOptimisticLockingFailureException.class, RuntimeException.class}},
 *       and {@code EventBindingException} IS a RuntimeException. The classifier resolves by closest
 *       supertype, so the exact entry in {@code exclude} has to win over the broad one in
 *       {@code include}.</li>
 * </ol>
 */
public class BindingFailureIsNotRetryableTest {

    /** The classifier Spring Kafka builds from include/exclude, with causes traversed. */
    private static BinaryExceptionClassifier classifier(boolean withBroadRuntimeInclude) {
        Map<Class<? extends Throwable>, Boolean> types = new HashMap<>();
        types.put(EventBindingException.class, false);          // exclude = not retryable
        if (withBroadRuntimeInclude) {
            types.put(RuntimeException.class, true);            // include, as RestaurantApplication has
        }
        BinaryExceptionClassifier c = new BinaryExceptionClassifier(types, true);
        c.setTraverseCauses(true);
        return c;
    }

    @Test
    public void aBindingFailureIsNotRetryable() {
        assertFalse(classifier(false).classify(new EventBindingException("bad payload")),
                "an unbindable payload must go straight to the DLT");
    }

    @Test
    public void aBindingFailureWrappedInARuntimeExceptionIsStillNotRetryable() {
        RuntimeException wrapped =
                new RuntimeException("Failed to process order event",
                        new EventBindingException("bad payload"));
        assertFalse(classifier(false).classify(wrapped),
                "traversingCauses must see through the generic catch that nine consumers use");
    }

    /**
     * Why no listener may put {@code RuntimeException} in {@code include}.
     *
     * <p>This is the measured reason RestaurantApplication's
     * {@code include = {ObjectOptimisticLockingFailureException.class, RuntimeException.class}}
     * had to go. Traversal stops at the first CLASSIFIED type, and a broad include classifies the
     * outer wrapper as retryable — so the excluded cause underneath is never reached and the
     * message is retried five times anyway. The configuration looks correct and does nothing.
     *
     * <p>Kept as an executable statement of the hazard; EVENT-BIND-NO-RETRY forbids the include.
     */
    @Test
    public void aBroadRuntimeExceptionIncludeDefeatsTheExclusionWhenWrapped() {
        assertFalse(classifier(true).classify(new EventBindingException("bad payload")),
                "unwrapped, the exact exclusion still wins");
        assertTrue(classifier(true).classify(
                        new RuntimeException("wrapped", new EventBindingException("bad payload"))),
                "wrapped, a broad include makes it retryable again -- which is why it is banned");
    }

    @Test
    public void anOrdinaryFailureIsStillRetryable() {
        // The control. If everything classified as non-retryable this test file would pass while
        // silently disabling retries for transient faults, which are exactly what retries are for.
        assertTrue(classifier(true).classify(new RuntimeException("transient database blip")),
                "a non-binding RuntimeException must remain retryable");
    }
}
