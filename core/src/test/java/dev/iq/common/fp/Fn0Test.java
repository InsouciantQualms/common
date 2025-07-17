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

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Fn0 functional interface covering supplier functionality.
 */
public final class Fn0Test {

    @Test
    public void testAsTryWithSuccessfulSupplier() {

        final var supplier = (Fn0<String>) () -> "success";
        final var result = Fn0.asTry(supplier);

        assertNotNull(result);
        assertEquals("success", result);
    }

    @Test
    public void testAsTryWithExceptionThrowingSupplier() {

        final var supplier = (Fn0<String>) () -> {
            throw new IOException("Test exception");
        };

        assertThrows(UnexpectedException.class, () -> Fn0.asTry(supplier)
        );
    }

    @Test
    public void testAsTryWithNullReturn() {

        final var supplier = (Fn0<String>) () -> null;
        final var result = Fn0.asTry(supplier);

        assertNull(result);
    }

    @Test
    public void testAsTryWithIntegerReturn() {

        final var supplier = (Fn0<Integer>) () -> 42;
        final var result = Fn0.asTry(supplier);

        assertNotNull(result);
        assertEquals(42, result);
    }

    @Test
    public void testAsIoWithSuccessfulSupplier() {

        final var supplier = (Fn0<String>) () -> "success";
        final var result = Fn0.asIo(supplier);

        assertNotNull(result);
        assertEquals("success", result);
    }

    @Test
    public void testAsIoWithExceptionThrowingSupplier() {

        final var supplier = (Fn0<String>) () -> {
            throw new IOException("Test exception");
        };

        assertThrows(IoException.class, () -> Fn0.asIo(supplier)
        );
    }

    @Test
    public void testAsIoWithNullReturn() {

        final var supplier = (Fn0<String>) () -> null;
        final var result = Fn0.asIo(supplier);

        assertNull(result);
    }

    @Test
    public void testAsIoWithIntegerReturn() {

        final var supplier = (Fn0<Integer>) () -> 42;
        final var result = Fn0.asIo(supplier);

        assertNotNull(result);
        assertEquals(42, result);
    }

    @Test
    public void testAsTryExceptionWrapping() {

        final var supplier = (Fn0<String>) () -> {
            throw new RuntimeException("Original exception");
        };

        final var thrown = assertThrows(UnexpectedException.class, () -> Fn0.asTry(supplier)
        );

        assertNotNull(thrown.getCause());
        assertEquals("Original exception", thrown.getCause().getMessage());
    }

    @Test
    public void testAsIoExceptionWrapping() {

        final var supplier = (Fn0<String>) () -> {
            throw new RuntimeException("Original exception");
        };

        final var thrown = assertThrows(IoException.class, () -> Fn0.asIo(supplier)
        );

        assertNotNull(thrown.getCause());
        assertEquals("Original exception", thrown.getCause().getMessage());
    }

    @Test
    public void testDirectFn0Usage() {

        final var fn0 = new Fn0<String>() {
            @Override
            public String get() {
                return "direct";
            }
        };

        assertDoesNotThrow(() -> {
            final var result = fn0.get();
            assertEquals("direct", result);
        });
    }

    @Test
    public void testDirectFn0WithException() {

        final var fn0 = new Fn0<String>() {
            @Override
            public String get() throws Exception {
                throw new Exception("Direct exception");
            }
        };

        assertThrows(Exception.class, fn0::get
        );
    }

    @Test
    public void testAsTryWithDifferentExceptionTypes() {

        final var ioSupplier = (Fn0<String>) () -> {
            throw new IOException("IO exception");
        };

        final var runtimeSupplier = (Fn0<String>) () -> {
            throw new RuntimeException("Runtime exception");
        };

        final var checkedException = (Fn0<String>) () -> {
            throw new Exception("Checked exception");
        };

        assertThrows(UnexpectedException.class, () -> Fn0.asTry(ioSupplier));
        assertThrows(UnexpectedException.class, () -> Fn0.asTry(runtimeSupplier));
        assertThrows(UnexpectedException.class, () -> Fn0.asTry(checkedException));
    }

    @Test
    public void testAsIoWithDifferentExceptionTypes() {

        final var ioSupplier = (Fn0<String>) () -> {
            throw new IOException("IO exception");
        };

        final var runtimeSupplier = (Fn0<String>) () -> {
            throw new RuntimeException("Runtime exception");
        };

        final var checkedException = (Fn0<String>) () -> {
            throw new Exception("Checked exception");
        };

        assertThrows(IoException.class, () -> Fn0.asIo(ioSupplier));
        assertThrows(IoException.class, () -> Fn0.asIo(runtimeSupplier));
        assertThrows(IoException.class, () -> Fn0.asIo(checkedException));
    }
}