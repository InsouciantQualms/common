/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.fp;

import dev.iq.common.error.IoException;
import dev.iq.common.error.UnexpectedException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Proc0 functional interface covering runnable functionality.
 */
public final class Proc0Test {

    @Test
    public void testRunAsTryWithSuccessfulRunnable() {

        final var executed = new AtomicBoolean(false);
        final var runnable = (Proc0) () -> executed.set(true);
        
        assertDoesNotThrow(() -> Proc0.runAsTry(runnable));
        assertTrue(executed.get());
    }

    @Test
    public void testRunAsTryWithExceptionThrowingRunnable() {

        final var runnable = (Proc0) () -> {
            throw new IOException("Test exception");
        };
        
        assertThrows(UnexpectedException.class, () -> {
            Proc0.runAsTry(runnable);
        });
    }

    @Test
    public void testRunAsTryWithMultipleOperations() {

        final var counter = new AtomicInteger(0);
        final var runnable = (Proc0) () -> {
            counter.incrementAndGet();
            counter.incrementAndGet();
            counter.incrementAndGet();
        };
        
        assertDoesNotThrow(() -> Proc0.runAsTry(runnable));
        assertEquals(3, counter.get());
    }

    @Test
    public void testRunAsIoWithSuccessfulRunnable() {

        final var executed = new AtomicBoolean(false);
        final var runnable = (Proc0) () -> executed.set(true);
        
        assertDoesNotThrow(() -> Proc0.runAsIo(runnable));
        assertTrue(executed.get());
    }

    @Test
    public void testRunAsIoWithExceptionThrowingRunnable() {

        final var runnable = (Proc0) () -> {
            throw new IOException("Test exception");
        };
        
        assertThrows(IoException.class, () -> {
            Proc0.runAsIo(runnable);
        });
    }

    @Test
    public void testRunAsIoWithMultipleOperations() {

        final var counter = new AtomicInteger(0);
        final var runnable = (Proc0) () -> {
            counter.incrementAndGet();
            counter.incrementAndGet();
            counter.incrementAndGet();
        };
        
        assertDoesNotThrow(() -> Proc0.runAsIo(runnable));
        assertEquals(3, counter.get());
    }

    @Test
    public void testRunAsTryExceptionWrapping() {

        final var runnable = (Proc0) () -> {
            throw new RuntimeException("Original exception");
        };
        
        final var thrown = assertThrows(UnexpectedException.class, () -> {
            Proc0.runAsTry(runnable);
        });
        
        assertNotNull(thrown.getCause());
        assertEquals("Original exception", thrown.getCause().getMessage());
    }

    @Test
    public void testRunAsIoExceptionWrapping() {

        final var runnable = (Proc0) () -> {
            throw new RuntimeException("Original exception");
        };
        
        final var thrown = assertThrows(IoException.class, () -> {
            Proc0.runAsIo(runnable);
        });
        
        assertNotNull(thrown.getCause());
        assertEquals("Original exception", thrown.getCause().getMessage());
    }

    @Test
    public void testDirectProc0Usage() {

        final var executed = new AtomicBoolean(false);
        final var proc0 = new Proc0() {
            @Override
            public void run() throws Exception {
                executed.set(true);
            }
        };
        
        assertDoesNotThrow(() -> {
            proc0.run();
        });
        assertTrue(executed.get());
    }

    @Test
    public void testDirectProc0WithException() {

        final var proc0 = new Proc0() {
            @Override
            public void run() throws Exception {
                throw new Exception("Direct exception");
            }
        };
        
        assertThrows(Exception.class, () -> {
            proc0.run();
        });
    }

    @Test
    public void testRunAsTryWithDifferentExceptionTypes() {

        final var ioRunnable = (Proc0) () -> {
            throw new IOException("IO exception");
        };
        
        final var runtimeRunnable = (Proc0) () -> {
            throw new RuntimeException("Runtime exception");
        };
        
        final var checkedRunnable = (Proc0) () -> {
            throw new Exception("Checked exception");
        };
        
        assertThrows(UnexpectedException.class, () -> Proc0.runAsTry(ioRunnable));
        assertThrows(UnexpectedException.class, () -> Proc0.runAsTry(runtimeRunnable));
        assertThrows(UnexpectedException.class, () -> Proc0.runAsTry(checkedRunnable));
    }

    @Test
    public void testRunAsIoWithDifferentExceptionTypes() {

        final var ioRunnable = (Proc0) () -> {
            throw new IOException("IO exception");
        };
        
        final var runtimeRunnable = (Proc0) () -> {
            throw new RuntimeException("Runtime exception");
        };
        
        final var checkedRunnable = (Proc0) () -> {
            throw new Exception("Checked exception");
        };
        
        assertThrows(IoException.class, () -> Proc0.runAsIo(ioRunnable));
        assertThrows(IoException.class, () -> Proc0.runAsIo(runtimeRunnable));
        assertThrows(IoException.class, () -> Proc0.runAsIo(checkedRunnable));
    }

    @Test
    public void testRunAsTryWithComplexOperation() {

        final var sb = new StringBuilder();
        final var runnable = (Proc0) () -> {
            sb.append("Hello");
            sb.append(" ");
            sb.append("World");
        };
        
        assertDoesNotThrow(() -> Proc0.runAsTry(runnable));
        assertEquals("Hello World", sb.toString());
    }

    @Test
    public void testRunAsIoWithComplexOperation() {

        final var sb = new StringBuilder();
        final var runnable = (Proc0) () -> {
            sb.append("Hello");
            sb.append(" ");
            sb.append("World");
        };
        
        assertDoesNotThrow(() -> Proc0.runAsIo(runnable));
        assertEquals("Hello World", sb.toString());
    }

    @Test
    public void testRunAsTryNoSideEffectsOnException() {

        final var counter = new AtomicInteger(0);
        final var runnable = (Proc0) () -> {
            counter.incrementAndGet();
            throw new RuntimeException("Exception after increment");
        };
        
        assertThrows(UnexpectedException.class, () -> {
            Proc0.runAsTry(runnable);
        });
        assertEquals(1, counter.get());
    }

    @Test
    public void testRunAsIoNoSideEffectsOnException() {

        final var counter = new AtomicInteger(0);
        final var runnable = (Proc0) () -> {
            counter.incrementAndGet();
            throw new RuntimeException("Exception after increment");
        };
        
        assertThrows(IoException.class, () -> {
            Proc0.runAsIo(runnable);
        });
        assertEquals(1, counter.get());
    }
}