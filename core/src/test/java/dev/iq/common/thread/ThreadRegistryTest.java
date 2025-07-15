/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.thread;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the ThreadRegistry enum covering thread registration functionality.
 */
public final class ThreadRegistryTest {

    @Test
    public void testSingletonInstance() {

        final var instance1 = ThreadRegistry.INSTANCE;
        final var instance2 = ThreadRegistry.INSTANCE;
        
        assertSame(instance1, instance2);
    }

    @Test
    public void testRegisterSimpleRunnable() {

        final var runnable = new TestSimpleRunnable(() -> false);
        final var thread = ThreadRegistry.INSTANCE.register(runnable);
        
        assertNotNull(thread);
        assertTrue(thread instanceof SimpleThread);
        assertFalse(thread.isAlive());
    }

    @Test
    public void testRegisteredThreadName() {

        final var runnable = new TestSimpleRunnable(() -> false);
        final var thread = ThreadRegistry.INSTANCE.register(runnable);
        
        assertNotNull(thread.getName());
        assertTrue(thread.getName().contains("ThreadRegistry"));
        assertTrue(thread.getName().contains("TestSimpleRunnable"));
    }

    @Test
    public void testRegisteredThreadGroup() {

        final var runnable = new TestSimpleRunnable(() -> false);
        final var thread = ThreadRegistry.INSTANCE.register(runnable);
        
        assertNotNull(thread.getThreadGroup());
        assertTrue(thread.getThreadGroup().getName().contains("RootGroup"));
    }

    @Test
    public void testMultipleRegistrations() {

        final var runnable1 = new TestSimpleRunnable(() -> false);
        final var runnable2 = new TestSimpleRunnable(() -> false);
        
        final var thread1 = ThreadRegistry.INSTANCE.register(runnable1);
        final var thread2 = ThreadRegistry.INSTANCE.register(runnable2);
        
        assertNotNull(thread1);
        assertNotNull(thread2);
        assertNotSame(thread1, thread2);
        assertEquals(thread1.getThreadGroup(), thread2.getThreadGroup());
    }

    @Test
    public void testRegisterAndStartThread() throws InterruptedException {

        final var executed = new AtomicBoolean(false);
        final var runnable = new TestSimpleRunnable(() -> {
            executed.set(true);
            return false;
        });
        
        final var thread = ThreadRegistry.INSTANCE.register(runnable);
        thread.start();
        thread.join(1000);
        
        assertTrue(executed.get());
        assertFalse(thread.isAlive());
    }

    @Test
    public void testKill9WithNoActiveThreads() {

        assertDoesNotThrow(() -> {
            ThreadRegistry.INSTANCE.kill9();
        });
    }

    @Test
    public void testKill9WithActiveThread() throws InterruptedException {

        final var counter = new AtomicInteger(0);
        final var runnable = new TestSimpleRunnable(() -> {
            counter.incrementAndGet();
            try {
                Thread.sleep(50);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            return true;
        });
        
        final var thread = ThreadRegistry.INSTANCE.register(runnable);
        thread.start();
        
        Thread.sleep(100);
        ThreadRegistry.INSTANCE.kill9();
        thread.join(1000);
        
        final var finalCount = counter.get();
        assertTrue(finalCount > 0);
        
        Thread.sleep(100);
        assertEquals(finalCount, counter.get());
    }

    @Test
    public void testKill9WithMultipleActiveThreads() throws InterruptedException {

        final var counter1 = new AtomicInteger(0);
        final var counter2 = new AtomicInteger(0);
        
        final var runnable1 = new TestSimpleRunnable(() -> {
            counter1.incrementAndGet();
            try {
                Thread.sleep(50);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            return true;
        });
        
        final var runnable2 = new TestSimpleRunnable(() -> {
            counter2.incrementAndGet();
            try {
                Thread.sleep(50);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
            return true;
        });
        
        final var thread1 = ThreadRegistry.INSTANCE.register(runnable1);
        final var thread2 = ThreadRegistry.INSTANCE.register(runnable2);
        
        thread1.start();
        thread2.start();
        
        Thread.sleep(100);
        ThreadRegistry.INSTANCE.kill9();
        
        thread1.join(1000);
        thread2.join(1000);
        
        final var finalCount1 = counter1.get();
        final var finalCount2 = counter2.get();
        
        assertTrue(finalCount1 > 0);
        assertTrue(finalCount2 > 0);
        
        Thread.sleep(100);
        assertEquals(finalCount1, counter1.get());
        assertEquals(finalCount2, counter2.get());
    }

    @Test
    public void testRegisterNullRunnable() {

        assertThrows(NullPointerException.class, () -> {
            ThreadRegistry.INSTANCE.register(null);
        });
    }

    @Test
    public void testThreadGroupConsistency() {

        final var runnable1 = new TestSimpleRunnable(() -> false);
        final var runnable2 = new TestSimpleRunnable(() -> false);
        final var runnable3 = new TestSimpleRunnable(() -> false);
        
        final var thread1 = ThreadRegistry.INSTANCE.register(runnable1);
        final var thread2 = ThreadRegistry.INSTANCE.register(runnable2);
        final var thread3 = ThreadRegistry.INSTANCE.register(runnable3);
        
        final var group1 = thread1.getThreadGroup();
        final var group2 = thread2.getThreadGroup();
        final var group3 = thread3.getThreadGroup();
        
        assertSame(group1, group2);
        assertSame(group2, group3);
        assertSame(group1, group3);
    }

    @Test
    public void testKill9GracefulShutdown() throws InterruptedException {

        final var gracefulShutdown = new AtomicBoolean(false);
        final var runnable = new TestSimpleRunnable(() -> {
            if (!gracefulShutdown.get()) {
                gracefulShutdown.set(true);
                return false;
            }
            return true;
        });
        
        final var thread = ThreadRegistry.INSTANCE.register(runnable);
        thread.start();
        
        Thread.sleep(50);
        ThreadRegistry.INSTANCE.kill9();
        thread.join(1000);
        
        assertTrue(gracefulShutdown.get());
    }

    @Test
    public void testThreadNamingConvention() {

        final var runnable = new TestSimpleRunnable(() -> false);
        final var thread = ThreadRegistry.INSTANCE.register(runnable);
        
        final var name = thread.getName();
        assertTrue(name.contains("ThreadRegistry"));
        assertTrue(name.contains(":"));
        assertTrue(name.contains("TestSimpleRunnable"));
    }

    @Test
    public void testRegisterAfterKill9() throws InterruptedException {

        final var runnable1 = new TestSimpleRunnable(() -> true);
        final var thread1 = ThreadRegistry.INSTANCE.register(runnable1);
        thread1.start();
        
        Thread.sleep(50);
        ThreadRegistry.INSTANCE.kill9();
        thread1.join(1000);
        
        final var executed = new AtomicBoolean(false);
        final var runnable2 = new TestSimpleRunnable(() -> {
            executed.set(true);
            return false;
        });
        
        final var thread2 = ThreadRegistry.INSTANCE.register(runnable2);
        thread2.start();
        thread2.join(1000);
        
        assertTrue(executed.get());
    }

    @Test
    public void testSynchronizedAccess() throws InterruptedException {

        final var registrationComplete = new AtomicBoolean(false);
        final var registrationThread = new Thread(() -> {
            final var runnable = new TestSimpleRunnable(() -> false);
            ThreadRegistry.INSTANCE.register(runnable);
            registrationComplete.set(true);
        });
        
        registrationThread.start();
        
        ThreadRegistry.INSTANCE.kill9();
        
        registrationThread.join(1000);
        assertTrue(registrationComplete.get());
    }

    @Test
    public void testMultipleKill9Calls() {

        assertDoesNotThrow(() -> {
            ThreadRegistry.INSTANCE.kill9();
            ThreadRegistry.INSTANCE.kill9();
            ThreadRegistry.INSTANCE.kill9();
        });
    }

    private static final class TestSimpleRunnable extends SimpleRunnable {
        private final java.util.function.Supplier<Boolean> goSupplier;

        TestSimpleRunnable(final java.util.function.Supplier<Boolean> goSupplier) {
            this.goSupplier = goSupplier;
        }

        @Override
        protected boolean go() {
            return goSupplier.get();
        }
    }
}