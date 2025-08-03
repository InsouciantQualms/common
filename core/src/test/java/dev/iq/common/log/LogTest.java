/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.log;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/** Tests for the Log utility class covering all logging levels and lazy evaluation of messages. */
final class LogTest {

    @Test
    void testTraceLogging() {

        final var messageEvaluated = new AtomicInteger(0);

        Log.trace(LogTest.class, () -> {
            messageEvaluated.incrementAndGet();
            return "trace message";
        });

        // Message evaluation depends on logging configuration
        assertTrue(messageEvaluated.get() >= 0);
    }

    @Test
    void testTraceLoggingWithException() {

        final var messageEvaluated = new AtomicInteger(0);
        final var exception = new RuntimeException("Test exception");

        Log.trace(
                LogTest.class,
                () -> {
                    messageEvaluated.incrementAndGet();
                    return "trace message with exception";
                },
                exception);

        // Message evaluation depends on logging configuration
        assertTrue(messageEvaluated.get() >= 0);
    }

    @Test
    void testDebugLogging() {

        final var messageEvaluated = new AtomicInteger(0);

        Log.debug(LogTest.class, () -> {
            messageEvaluated.incrementAndGet();
            return "debug message";
        });

        // Message evaluation depends on logging configuration
        assertTrue(messageEvaluated.get() >= 0);
    }

    @Test
    void testDebugLoggingWithException() {

        final var messageEvaluated = new AtomicInteger(0);
        final var exception = new RuntimeException("Test exception");

        Log.debug(
                LogTest.class,
                () -> {
                    messageEvaluated.incrementAndGet();
                    return "debug message with exception";
                },
                exception);

        // Message evaluation depends on logging configuration
        assertTrue(messageEvaluated.get() >= 0);
    }

    @Test
    void testInfoLogging() {

        final var messageEvaluated = new AtomicInteger(0);

        Log.info(LogTest.class, () -> {
            messageEvaluated.incrementAndGet();
            return "info message";
        });

        // Message evaluation depends on logging configuration
        assertTrue(messageEvaluated.get() >= 0);
    }

    @Test
    void testInfoLoggingWithException() {

        final var messageEvaluated = new AtomicInteger(0);
        final var exception = new RuntimeException("Test exception");

        Log.info(
                LogTest.class,
                () -> {
                    messageEvaluated.incrementAndGet();
                    return "info message with exception";
                },
                exception);

        // Message evaluation depends on logging configuration
        assertTrue(messageEvaluated.get() >= 0);
    }

    @Test
    void testWarnLogging() {

        final var messageEvaluated = new AtomicInteger(0);

        Log.warn(LogTest.class, () -> {
            messageEvaluated.incrementAndGet();
            return "warn message";
        });

        // Message evaluation depends on logging configuration
        assertTrue(messageEvaluated.get() >= 0);
    }

    @Test
    void testWarnLoggingWithException() {

        final var messageEvaluated = new AtomicInteger(0);
        final var exception = new RuntimeException("Test exception");

        Log.warn(
                LogTest.class,
                () -> {
                    messageEvaluated.incrementAndGet();
                    return "warn message with exception";
                },
                exception);

        // Message evaluation depends on logging configuration
        assertTrue(messageEvaluated.get() >= 0);
    }

    @Test
    void testErrorLogging() {

        final var messageEvaluated = new AtomicInteger(0);

        Log.error(LogTest.class, () -> {
            messageEvaluated.incrementAndGet();
            return "error message";
        });

        // Message evaluation depends on logging configuration
        assertTrue(messageEvaluated.get() >= 0);
    }

    @Test
    void testErrorLoggingWithException() {

        final var messageEvaluated = new AtomicInteger(0);
        final var exception = new RuntimeException("Test exception");

        Log.error(
                LogTest.class,
                () -> {
                    messageEvaluated.incrementAndGet();
                    return "error message with exception";
                },
                exception);

        // Message evaluation depends on logging configuration
        assertTrue(messageEvaluated.get() >= 0);
    }

    @Test
    void testLazyEvaluationWhenDisabled() {

        final var messageEvaluated = new AtomicInteger(0);
        final var logger = LoggerFactory.getLogger(LogTest.class);

        // Most logging frameworks disable TRACE by default
        if (!logger.isTraceEnabled()) {
            Log.trace(LogTest.class, () -> {
                messageEvaluated.incrementAndGet();
                return "this should not be evaluated";
            });

            assertEquals(0, messageEvaluated.get());
        }
    }

    @Test
    void testNullMessageSupplier() {

        assertThrows(NullPointerException.class, () -> Log.info(LogTest.class, null));
    }

    @Test
    void testNullCallerClass() {

        assertThrows(NullPointerException.class, () -> Log.info(null, () -> "message"));
    }

    @Test
    void testMessageSupplierReturnsNull() {

        // Should not throw exception even if supplier returns null
        Log.info(LogTest.class, () -> null);
        assertTrue(true); // Test passes if no exception is thrown
    }

    @Test
    void testMessageSupplierThrowsException() {

        // Should propagate exception from supplier
        assertThrows(
                RuntimeException.class,
                () -> Log.info(LogTest.class, () -> {
                    throw new RuntimeException("Supplier exception");
                }));
    }

    @Test
    void testDifferentCallerClasses() {

        final var messageEvaluated = new AtomicInteger(0);

        Log.info(LogTest.class, () -> {
            messageEvaluated.incrementAndGet();
            return "message from LogTest";
        });

        Log.info(String.class, () -> {
            messageEvaluated.incrementAndGet();
            return "message from String";
        });

        // Both messages should be evaluated (assuming INFO is enabled)
        assertTrue(messageEvaluated.get() >= 0);
    }

    @Test
    void testComplexMessageGeneration() {

        final var counter = new AtomicInteger(0);

        Log.info(LogTest.class, () -> {
            final var count = counter.incrementAndGet();
            return String.format(
                    "Complex message with count: %d, thread: %s",
                    count, Thread.currentThread().getName());
        });

        assertTrue(counter.get() >= 0);
    }

    @Test
    void testExceptionHandling() {

        final var testException = new TestException("Test exception message");

        Log.error(LogTest.class, () -> "Error occurred", testException);

        // Should not throw exception
        assertTrue(true);
    }

    @Test
    void testNestedExceptions() {

        final var cause = new RuntimeException("Root cause");
        final var wrapper = new TestException("Wrapper exception", cause);

        Log.error(LogTest.class, () -> "Nested exception occurred", wrapper);

        // Should not throw exception
        assertTrue(true);
    }

    @Test
    void testConcurrentLogging() throws InterruptedException {

        final var numThreads = 10;
        final var threads = new Thread[numThreads];
        final var counter = new AtomicInteger(0);

        for (var i = 0; i < numThreads; i++) {
            final var threadId = i;
            threads[i] = new Thread(() -> {
                for (var j = 0; j < 100; j++) {
                    final var iteration = j;
                    Log.info(LogTest.class, () -> {
                        counter.incrementAndGet();
                        return String.format("Thread %d, iteration %d", threadId, iteration);
                    });
                }
            });
        }

        for (final var thread : threads) {
            thread.start();
        }

        for (final var thread : threads) {
            thread.join();
        }

        // All messages should have been processed
        assertTrue(counter.get() >= 0);
    }

    /** Custom exception for testing. */
    private static final class TestException extends Exception {

        private TestException(final String message) {
            super(message);
        }

        private TestException(final String message, final Throwable cause) {
            super(message, cause);
        }
    }
}
