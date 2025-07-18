/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/**
 * Tests for the SimpleLock covering withReturn and withVoid methods for both reentrant and
 * non-reentrant locks.
 */
public final class SimpleLockTest {

    @Test
    public void testReentrantLockWithReturn() {

        final var lock = SimpleLock.reentrant();

        final var result = lock.withReturn(() -> "test result");

        assertEquals("test result", result);
    }

    @Test
    public void testReentrantLockWithVoid() {

        final var lock = SimpleLock.reentrant();
        final var counter = new AtomicInteger(0);

        lock.withVoid(counter::incrementAndGet);

        assertEquals(1, counter.get());
    }

    @Test
    public void testNonReentrantLockWithReturn() {

        final var lock = SimpleLock.nonReentrant();

        final var result = lock.withReturn(() -> "test result");

        assertEquals("test result", result);
    }

    @Test
    public void testNonReentrantLockWithVoid() {

        final var lock = SimpleLock.nonReentrant();
        final var counter = new AtomicInteger(0);

        lock.withVoid(counter::incrementAndGet);

        assertEquals(1, counter.get());
    }

    @Test
    public void testWithReturnMutualExclusion() throws InterruptedException {

        final var lock = SimpleLock.reentrant();
        final var counter = new AtomicInteger(0);
        final var latch = new CountDownLatch(2);
        final var executor = Executors.newFixedThreadPool(2);

        final Runnable task = () -> {
            final var result = lock.withReturn(() -> {
                final var current = counter.get();
                try {
                    Thread.sleep(50); // Simulate work
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                counter.set(current + 1);
                return current + 1;
            });
            assertTrue(result > 0);
            latch.countDown();
        };

        executor.submit(task);
        executor.submit(task);

        latch.await();
        executor.shutdown();

        assertEquals(2, counter.get());
    }

    @Test
    public void testWithVoidMutualExclusion() throws InterruptedException {

        final var lock = SimpleLock.reentrant();
        final var counter = new AtomicInteger(0);
        final var latch = new CountDownLatch(2);
        final var executor = Executors.newFixedThreadPool(2);

        final Runnable task = () -> {
            lock.withVoid(() -> {
                final var current = counter.get();
                try {
                    Thread.sleep(50); // Simulate work
                } catch (final InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                counter.set(current + 1);
            });
            latch.countDown();
        };

        executor.submit(task);
        executor.submit(task);

        latch.await();
        executor.shutdown();

        assertEquals(2, counter.get());
    }

    @Test
    public void testReentrantLockAllowsRecursion() {

        final var lock = SimpleLock.reentrant();
        final var counter = new AtomicInteger(0);

        final var result = lock.withReturn(() -> {
            counter.incrementAndGet();
            return lock.withReturn(() -> {
                counter.incrementAndGet();
                return counter.get();
            });
        });

        assertEquals(2, result);
        assertEquals(2, counter.get());
    }

    @Test
    public void testWithReturnHandlesException() {

        final var lock = SimpleLock.reentrant();

        assertThrows(
                RuntimeException.class,
                () -> lock.withReturn(() -> {
                    throw new RuntimeException("Test exception");
                }));
    }

    @Test
    public void testWithVoidHandlesException() {

        final var lock = SimpleLock.reentrant();

        assertThrows(
                RuntimeException.class,
                () -> lock.withVoid(() -> {
                    throw new RuntimeException("Test exception");
                }));
    }

    @Test
    public void testLockIsReleasedAfterException() {

        final var lock = SimpleLock.reentrant();

        assertThrows(
                RuntimeException.class,
                () -> lock.withReturn(() -> {
                    throw new RuntimeException("Test exception");
                }));

        // Lock should be released and available
        final var result = lock.withReturn(() -> "success");
        assertEquals("success", result);
    }

    @Test
    public void testWithReturnNullValue() {

        final var lock = SimpleLock.reentrant();

        final var result = lock.withReturn(() -> null);

        assertNull(result);
    }

    @Test
    public void testComplexReturnTypes() {

        final var lock = SimpleLock.reentrant();

        final var result = lock.withReturn(() -> new TestObject("test", 42));

        assertNotNull(result);
        assertEquals("test", result.name);
        assertEquals(42, result.value);
    }

    @Test
    public void testConcurrentAccess() throws InterruptedException {

        final var lock = SimpleLock.reentrant();
        final var counter = new AtomicInteger(0);
        final var numThreads = 10;
        final var latch = new CountDownLatch(numThreads);
        final var executor = Executors.newFixedThreadPool(numThreads);

        for (var i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (var j = 0; j < 100; j++) {
                    lock.withVoid(counter::incrementAndGet);
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(numThreads * 100, counter.get());
    }

    @Test
    public void testMixedWithReturnAndWithVoid() {

        final var lock = SimpleLock.reentrant();
        final var counter = new AtomicInteger(0);

        final var result = lock.withReturn(() -> {
            counter.incrementAndGet();
            lock.withVoid(counter::incrementAndGet);
            return counter.get();
        });

        assertEquals(2, result);
        assertEquals(2, counter.get());
    }

    @Test
    public void testNonReentrantLockBasicFunctionality() {

        final var lock = SimpleLock.nonReentrant();
        final var counter = new AtomicInteger(0);

        final var result = lock.withReturn(() -> {
            counter.incrementAndGet();
            return counter.get();
        });

        assertEquals(1, result);
        assertEquals(1, counter.get());

        lock.withVoid(counter::incrementAndGet);
        assertEquals(2, counter.get());
    }

    @Test
    public void testMultipleLockInstances() {

        final var lock1 = SimpleLock.reentrant();
        final var lock2 = SimpleLock.reentrant();
        final var counter = new AtomicInteger(0);

        final var result1 = lock1.withReturn(() -> {
            counter.incrementAndGet();
            return lock2.withReturn(() -> {
                counter.incrementAndGet();
                return counter.get();
            });
        });

        assertEquals(2, result1);
        assertEquals(2, counter.get());
    }

    /** Simple test object for complex return type testing. */
    private record TestObject(String name, int value) {}
}
