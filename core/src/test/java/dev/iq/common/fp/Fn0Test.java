/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.fp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import dev.iq.common.error.IoException;
import dev.iq.common.error.UnexpectedException;
import java.io.IOException;
import org.junit.jupiter.api.Test;

/** Tests for the Fn0 functional interface covering supplier functionality. */
final class Fn0Test {

    @Test
    void testAsTryWithSuccessfulSupplier() {

        final var supplier = (Fn0<String>) () -> "success";
        final var result = Fn0.asTry(supplier);

        assertNotNull(result);
        assertEquals("success", result);
    }

    @Test
    void testAsTryWithExceptionThrowingSupplier() {

        final var supplier = (Fn0<String>) () -> {
            throw new IOException("Test exception");
        };

        assertThrows(UnexpectedException.class, () -> Fn0.asTry(supplier));
    }

    @Test
    void testAsTryWithNullReturn() {

        final var supplier = (Fn0<String>) () -> null;
        final var result = Fn0.asTry(supplier);

        assertNull(result);
    }

    @Test
    void testAsTryWithIntegerReturn() {

        final var supplier = (Fn0<Integer>) () -> 42;
        final var result = Fn0.asTry(supplier);

        assertNotNull(result);
        assertEquals(42, result);
    }

    @Test
    void testAsIoWithSuccessfulSupplier() {

        final var supplier = (Fn0<String>) () -> "success";
        final var result = Fn0.asIo(supplier);

        assertNotNull(result);
        assertEquals("success", result);
    }

    @Test
    void testAsIoWithExceptionThrowingSupplier() {

        final var supplier = (Fn0<String>) () -> {
            throw new IOException("Test exception");
        };

        assertThrows(IoException.class, () -> Fn0.asIo(supplier));
    }

    @Test
    void testAsIoWithNullReturn() {

        final var supplier = (Fn0<String>) () -> null;
        final var result = Fn0.asIo(supplier);

        assertNull(result);
    }

    @Test
    void testAsIoWithIntegerReturn() {

        final var supplier = (Fn0<Integer>) () -> 42;
        final var result = Fn0.asIo(supplier);

        assertNotNull(result);
        assertEquals(42, result);
    }

    @Test
    void testAsTryExceptionWrapping() {

        final var supplier = (Fn0<String>) () -> {
            throw new RuntimeException("Original exception");
        };

        final var thrown = assertThrows(UnexpectedException.class, () -> Fn0.asTry(supplier));

        assertNotNull(thrown.getCause());
        assertEquals("Original exception", thrown.getCause().getMessage());
    }

    @Test
    void testAsIoExceptionWrapping() {

        final var supplier = (Fn0<String>) () -> {
            throw new RuntimeException("Original exception");
        };

        final var thrown = assertThrows(IoException.class, () -> Fn0.asIo(supplier));

        assertNotNull(thrown.getCause());
        assertEquals("Original exception", thrown.getCause().getMessage());
    }

    @Test
    void testDirectFn0Usage() {

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
    void testDirectFn0WithException() {

        final var fn0 = new Fn0<String>() {
            @Override
            public String get() throws Exception {
                throw new Exception("Direct exception");
            }
        };

        assertThrows(Exception.class, fn0::get);
    }

    @Test
    void testAsTryWithDifferentExceptionTypes() {

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
    void testAsIoWithDifferentExceptionTypes() {

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
