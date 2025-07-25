/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.adt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.iq.common.error.IoException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Lazy type covering lazy evaluation, thread safety, and recursive call protection.
 */
public final class LazyTest {

    @Test
    public void testLazyEvaluation() {

        final var counter = new AtomicInteger(0);
        final var lazy = Lazy.of(() -> {
            counter.incrementAndGet();
            return "computed";
        });

        assertFalse(lazy.loaded());
        assertEquals(0, counter.get());

        final var result = lazy.get();

        assertEquals("computed", result);
        assertTrue(lazy.loaded());
        assertEquals(1, counter.get());
    }

    @Test
    public void testMemoization() {

        final var counter = new AtomicInteger(0);
        final var lazy = Lazy.of(() -> {
            counter.incrementAndGet();
            return "computed";
        });

        final var result1 = lazy.get();
        final var result2 = lazy.get();
        final var result3 = lazy.get();

        assertEquals("computed", result1);
        assertEquals("computed", result2);
        assertEquals("computed", result3);
        assertSame(result1, result2);
        assertSame(result2, result3);
        assertEquals(1, counter.get());
        assertTrue(lazy.loaded());
    }

    @Test
    public void testThreadSafety() throws InterruptedException {

        final var counter = new AtomicInteger(0);
        final var lazy = Lazy.of(() -> {
            counter.incrementAndGet();
            return Thread.currentThread().getName();
        });

        final var executor = Executors.newFixedThreadPool(10);
        final var latch = new CountDownLatch(10);
        final var results = new String[10];

        for (var i = 0; i < 10; i++) {
            final var index = i;
            executor.submit(() -> {
                try {
                    results[index] = lazy.get();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(1, counter.get());
        assertTrue(lazy.loaded());

        final var firstResult = results[0];
        for (final var result : results) {
            assertEquals(firstResult, result);
        }
    }

    @Test
    public void testRecursiveCallPrevention() {

        final var recursiveLazy = new RecursiveLazy();

        final var exception = assertThrows(IoException.class, recursiveLazy::get);
        assertInstanceOf(IoException.class, exception.getCause());
        assertInstanceOf(IllegalStateException.class, exception.getCause().getCause());
        assertEquals(
                "Recursive call to Lazy.get() on same instance in the same thread",
                exception.getCause().getCause().getMessage());
    }

    @Test
    public void testExceptionHandling() {

        final var lazy = Lazy.of(() -> {
            throw new RuntimeException("Test exception");
        });

        assertFalse(lazy.loaded());
        assertThrows(IoException.class, lazy::get);
        assertFalse(lazy.loaded());
    }

    @Test
    public void testExceptionMemoization() {

        final var counter = new AtomicInteger(0);
        final var lazy = Lazy.of(() -> {
            counter.incrementAndGet();
            throw new RuntimeException("Test exception");
        });

        assertThrows(IoException.class, lazy::get);
        assertThrows(IoException.class, lazy::get);
        assertThrows(IoException.class, lazy::get);

        assertEquals(3, counter.get());
        assertFalse(lazy.loaded());
    }

    @Test
    public void testFailFastExceptionHandling() {

        final var lazy = Lazy.ofFailFast(() -> {
            throw new RuntimeException("Test exception");
        });

        assertFalse(lazy.loaded());
        assertThrows(IoException.class, lazy::get);
        assertTrue(lazy.loaded());
    }

    @Test
    public void testFailFastExceptionMemoization() {

        final var counter = new AtomicInteger(0);
        final var lazy = Lazy.ofFailFast(() -> {
            counter.incrementAndGet();
            throw new RuntimeException("Test exception");
        });

        assertThrows(IoException.class, lazy::get);
        assertThrows(IllegalStateException.class, lazy::get);
        assertThrows(IllegalStateException.class, lazy::get);

        assertEquals(1, counter.get());
        assertTrue(lazy.loaded());

        final var exception = assertThrows(IllegalStateException.class, lazy::get);
        assertEquals("Lazy initialization previously failed", exception.getMessage());
        assertNotNull(exception.getCause());

        // The exception is wrapped by Io.withReturn in an IoException
        final var cause = exception.getCause();
        assertInstanceOf(IoException.class, cause);

        // The IoException should have the original RuntimeException as its cause
        assertNotNull(cause.getCause());
        assertInstanceOf(RuntimeException.class, cause.getCause());
        assertEquals("Test exception", cause.getCause().getMessage());
    }

    @Test
    public void testNullValue() {

        final var lazy = Lazy.of(() -> null);

        assertFalse(lazy.loaded());
        assertNull(lazy.get());
        assertTrue(lazy.loaded());
    }

    @Test
    public void testComplexObject() {

        final var lazy = Lazy.of(() -> new TestObject("test", 42));

        assertFalse(lazy.loaded());

        final var result = lazy.get();

        assertNotNull(result);
        assertEquals("test", result.name);
        assertEquals(42, result.value);
        assertTrue(lazy.loaded());

        final var result2 = lazy.get();
        assertSame(result, result2);
    }

    @Test
    public void testMultipleLazyInstances() {

        final var counter1 = new AtomicInteger(0);
        final var counter2 = new AtomicInteger(0);

        final var lazy1 = Lazy.of(() -> {
            counter1.incrementAndGet();
            return "first";
        });

        final var lazy2 = Lazy.of(() -> {
            counter2.incrementAndGet();
            return "second";
        });

        assertEquals("first", lazy1.get());
        assertEquals("second", lazy2.get());
        assertEquals(1, counter1.get());
        assertEquals(1, counter2.get());

        assertTrue(lazy1.loaded());
        assertTrue(lazy2.loaded());
    }

    @Test
    public void testLazyChaining() {

        final var lazy1 = Lazy.of(() -> "base");
        final var lazy2 = Lazy.of(() -> lazy1.get() + "_extended");

        assertEquals("base_extended", lazy2.get());
        assertTrue(lazy1.loaded());
        assertTrue(lazy2.loaded());
    }

    @Test
    public void testFailFastSuccessfulEvaluation() {

        final var counter = new AtomicInteger(0);
        final var lazy = Lazy.ofFailFast(() -> {
            counter.incrementAndGet();
            return "computed";
        });

        assertFalse(lazy.loaded());
        assertEquals(0, counter.get());

        final var result = lazy.get();

        assertEquals("computed", result);
        assertTrue(lazy.loaded());
        assertEquals(1, counter.get());

        final var result2 = lazy.get();
        assertEquals("computed", result2);
        assertSame(result, result2);
        assertEquals(1, counter.get());
    }

    @Test
    public void testFailFastWithNullValue() {

        final var lazy = Lazy.ofFailFast(() -> null);

        assertFalse(lazy.loaded());
        assertNull(lazy.get());
        assertTrue(lazy.loaded());
        assertNull(lazy.get());
    }

    @Test
    public void testFailFastThreadSafety() throws InterruptedException {

        final var counter = new AtomicInteger(0);
        final var lazy = Lazy.ofFailFast(() -> {
            counter.incrementAndGet();
            if (counter.get() == 1) {
                throw new RuntimeException("First thread fails");
            }
            return "success";
        });

        final var executor = Executors.newFixedThreadPool(10);
        final var latch = new CountDownLatch(10);
        final var exceptions = new AtomicInteger(0);

        for (var i = 0; i < 10; i++) {
            executor.submit(() -> {
                try {
                    lazy.get();
                } catch (Exception e) {
                    exceptions.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(1, counter.get());
        assertTrue(lazy.loaded());
        assertEquals(10, exceptions.get());
    }

    /** Simple test object for complex object testing. */
    private record TestObject(String name, int value) {}

    /** Helper class to test recursive call prevention. */
    private static final class RecursiveLazy {
        private final Lazy<String> lazy;

        RecursiveLazy() {
            lazy = Lazy.of(this::getValue);
        }

        private String getValue() {
            return lazy.get();
        }

        public String get() {
            return lazy.get();
        }
    }
}
