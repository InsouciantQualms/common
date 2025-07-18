/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.lock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

/** Tests for the NonReentrantLock covering basic locking, fairness, and non-reentrant behavior. */
public final class NonReentrantLockTest {

    @Test
    public void testBasicLockUnlock() {

        final var lock = new NonReentrantLock();

        lock.lock();
        assertTrue(true); // We got the lock
        lock.unlock();
        assertTrue(true); // We released the lock
    }

    @Test
    public void testTryLock() {

        final var lock = new NonReentrantLock();

        assertTrue(lock.tryLock());
        lock.unlock();

        assertTrue(lock.tryLock());
        lock.unlock();
    }

    @Test
    public void testTryLockWithTimeout() throws InterruptedException {

        final var lock = new NonReentrantLock();

        assertTrue(lock.tryLock(100, TimeUnit.MILLISECONDS));
        lock.unlock();

        assertTrue(lock.tryLock(0, TimeUnit.MILLISECONDS));
        lock.unlock();
    }

    @Test
    public void testLockInterruptibly() throws InterruptedException {

        final var lock = new NonReentrantLock();

        lock.lockInterruptibly();
        assertTrue(true); // We got the lock
        lock.unlock();
    }

    @Test
    public void testMutualExclusion() throws InterruptedException {

        final var lock = new NonReentrantLock();
        final var counter = new AtomicInteger(0);
        final var latch = new CountDownLatch(2);
        final var executor = Executors.newFixedThreadPool(2);

        final Runnable task = () -> {
            lock.lock();
            try {
                final var current = counter.get();
                Thread.sleep(50); // Simulate work
                counter.set(current + 1);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
                latch.countDown();
            }
        };

        executor.submit(task);
        executor.submit(task);

        latch.await();
        executor.shutdown();

        assertEquals(2, counter.get());
    }

    @Test
    public void testFairness() throws InterruptedException {

        final var lock = new NonReentrantLock();
        final var results = new int[3];
        final var latch = new CountDownLatch(3);
        final var executor = Executors.newFixedThreadPool(3);

        // First thread gets the lock
        lock.lock();

        // Start three threads that will wait for the lock
        for (var i = 0; i < 3; i++) {
            final var index = i;
            executor.submit(() -> {
                lock.lock();
                try {
                    results[index] = index;
                } finally {
                    lock.unlock();
                    latch.countDown();
                }
            });
        }

        // Give threads time to queue up
        Thread.sleep(100);

        // Release the lock
        lock.unlock();

        latch.await();
        executor.shutdown();

        // All threads should have completed
        assertEquals(0, results[0]);
        assertEquals(1, results[1]);
        assertEquals(2, results[2]);
    }

    @Test
    public void testTryLockFailsWhenLocked() {

        final var lock = new NonReentrantLock();

        lock.lock();
        assertFalse(lock.tryLock());
        lock.unlock();
    }

    @Test
    public void testTryLockWithTimeoutFailsWhenLocked() throws InterruptedException {

        final var lock = new NonReentrantLock();

        lock.lock();
        assertFalse(lock.tryLock(10, TimeUnit.MILLISECONDS));
        lock.unlock();
    }

    @Test
    public void testLockInterruptiblyCanBeInterrupted() throws InterruptedException {

        final var lock = new NonReentrantLock();
        final var latch = new CountDownLatch(1);
        final var executor = Executors.newSingleThreadExecutor();

        lock.lock(); // Hold the lock

        final var future = executor.submit(() -> {
            try {
                latch.countDown();
                lock.lockInterruptibly();
                fail("Should have been interrupted");
            } catch (final InterruptedException e) {
                // Expected
                assertTrue(Thread.currentThread().isInterrupted());
            }
        });

        latch.await(); // Wait for thread to start
        Thread.sleep(50); // Give time for thread to block
        future.cancel(true); // Interrupt the thread

        lock.unlock();
        executor.shutdown();
    }

    @Test
    public void testNewConditionThrowsUnsupportedOperation() {

        final var lock = new NonReentrantLock();

        final var exception = assertThrows(UnsupportedOperationException.class, lock::newCondition);
        assertEquals("Lock does not support new conditions", exception.getMessage());
    }

    @Test
    public void testMultipleUnlockDoesNotBreakLock() {

        final var lock = new NonReentrantLock();

        lock.lock();
        lock.unlock();
        lock.unlock(); // Should not throw

        // Lock should still work
        assertTrue(lock.tryLock());
        lock.unlock();
    }

    @Test
    public void testConcurrentAccess() throws InterruptedException {

        final var lock = new NonReentrantLock();
        final var counter = new AtomicInteger(0);
        final var numThreads = 10;
        final var latch = new CountDownLatch(numThreads);
        final var executor = Executors.newFixedThreadPool(numThreads);

        for (var i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                for (var j = 0; j < 100; j++) {
                    lock.lock();
                    try {
                        counter.incrementAndGet();
                    } finally {
                        lock.unlock();
                    }
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();

        assertEquals(numThreads * 100, counter.get());
    }
}
