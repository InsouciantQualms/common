/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.error;

import dev.iq.common.fp.Proc0;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests for the Retry utility class and its retry strategies. */
final class RetryTest {

    @Test
    void testSimpleRetrySucceedsOnFirstAttempt() {

        final var attemptCount = new AtomicInteger(0);
        // Operation succeeds immediately
        final Proc0 successfulOperation = attemptCount::incrementAndGet;

        Retry.simple(successfulOperation, 3, 100);

        Assertions.assertEquals(1, attemptCount.get());
    }

    @Test
    void testSimpleRetrySucceedsOnSecondAttempt() {

        final var attemptCount = new AtomicInteger(0);
        final Proc0 eventuallySuccessfulOperation = () -> {
            final var count = attemptCount.incrementAndGet();
            if (count == 1) {
                throw new RuntimeException("First attempt fails");
            }
            // Second attempt succeeds
        };

        Retry.simple(eventuallySuccessfulOperation, 3, 50);

        Assertions.assertEquals(2, attemptCount.get());
    }

    @Test
    void testSimpleRetrySucceedsOnLastAttempt() {

        final var attemptCount = new AtomicInteger(0);
        final Proc0 lastAttemptSuccessfulOperation = () -> {
            final var count = attemptCount.incrementAndGet();
            if (count < 3) {
                throw new RuntimeException("Attempt " + count + " fails");
            }
            // Third attempt succeeds
        };

        Retry.simple(lastAttemptSuccessfulOperation, 3, 50);

        Assertions.assertEquals(3, attemptCount.get());
    }

    @Test
    void testSimpleRetryFailsAfterMaxAttempts() {

        final var attemptCount = new AtomicInteger(0);
        final Proc0 alwaysFailingOperation = () -> {
            attemptCount.incrementAndGet();
            throw new RuntimeException("Always fails");
        };

        final var exception = Assertions.assertThrows(
                Retry.RetryLimitExceededException.class, () -> Retry.simple(alwaysFailingOperation, 3, 50));

        Assertions.assertEquals(3, attemptCount.get());
        Assertions.assertTrue(exception.getMessage().contains("Failed after 3 attempts"));
    }

    @Test
    void testSimpleRetryWithZeroAttempts() {

        final var attemptCount = new AtomicInteger(0);
        final Proc0 operation = attemptCount::incrementAndGet;

        final var exception =
                Assertions.assertThrows(Retry.RetryLimitExceededException.class, () -> Retry.simple(operation, 0, 100));

        Assertions.assertEquals(0, attemptCount.get());
        Assertions.assertTrue(exception.getMessage().contains("Failed after 0 attempts"));
    }

    @Test
    void testSimpleRetryWithSingleAttempt() {

        final var attemptCount = new AtomicInteger(0);
        final Proc0 failingOperation = () -> {
            attemptCount.incrementAndGet();
            throw new RuntimeException("Single attempt fails");
        };

        final var exception = Assertions.assertThrows(
                Retry.RetryLimitExceededException.class, () -> Retry.simple(failingOperation, 1, 100));

        Assertions.assertEquals(1, attemptCount.get());
        Assertions.assertTrue(exception.getMessage().contains("Failed after 1 attempts"));
    }

    @Test
    void testSimpleRetryWithDifferentExceptions() {

        final var attemptCount = new AtomicInteger(0);
        final Proc0 differentExceptionsOperation = () -> {
            final var count = attemptCount.incrementAndGet();
            switch (count) {
                case 1:
                    throw new RuntimeException("First failure");
                case 2:
                    throw new IllegalArgumentException("Second failure");
                case 3:
                    throw new IllegalStateException("Third failure");
                default:
                    // Should not reach here
            }
        };

        final var exception = Assertions.assertThrows(
                Retry.RetryLimitExceededException.class, () -> Retry.simple(differentExceptionsOperation, 3, 50));

        Assertions.assertEquals(3, attemptCount.get());
        Assertions.assertTrue(exception.getMessage().contains("Failed after 3 attempts"));
    }

    @Test
    void testSimpleRetryWithWaitTime() {

        final var attemptCount = new AtomicInteger(0);
        final var startTime = System.currentTimeMillis();
        final Proc0 failingOperation = () -> {
            attemptCount.incrementAndGet();
            throw new RuntimeException("Always fails");
        };

        Assertions.assertThrows(Retry.RetryLimitExceededException.class, () -> Retry.simple(failingOperation, 3, 200));

        final var endTime = System.currentTimeMillis();

        Assertions.assertEquals(3, attemptCount.get());
        // Should have waited approximately 400ms (200ms between each of the 2 retries)
        // Allow for some timing variance
        final var totalTime = endTime - startTime;
        Assertions.assertTrue(totalTime >= 300, () -> "Total time should be at least 300ms but was " + totalTime);
    }

    @Test
    void testRetryLimitExceededExceptionMessage() {

        final var message = "Custom failure message";
        final var exception = new Retry.RetryLimitExceededException(message);

        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void testRetryLimitExceededExceptionIsRuntimeException() {

        final var exception = new Retry.RetryLimitExceededException("test");

        Assertions.assertInstanceOf(RuntimeException.class, exception);
    }

    @Test
    void testSimpleRetryWithInterruptedException() {

        final var attemptCount = new AtomicInteger(0);
        final Proc0 failingOperation = () -> {
            attemptCount.incrementAndGet();
            throw new RuntimeException("Always fails");
        };

        // Interrupt the current thread to test interrupt handling
        Thread.currentThread().interrupt();

        final var exception = Assertions.assertThrows(
                Retry.RetryLimitExceededException.class, () -> Retry.simple(failingOperation, 2, 100));

        Assertions.assertEquals(2, attemptCount.get());
        Assertions.assertTrue(exception.getMessage().contains("Failed after 2 attempts"));

        // Clear interrupted flag
        Thread.interrupted();
    }
}
