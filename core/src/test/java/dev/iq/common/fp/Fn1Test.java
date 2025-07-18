/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.fp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.iq.common.error.IoException;
import dev.iq.common.error.UnexpectedException;
import java.io.IOException;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** Tests for the Fn1 functional interface covering function functionality. */
public final class Fn1Test {

    @Test
    public void testAsTryWithSuccessfulFunction() {

        final var function = (Fn1<String, Integer>) String::length;
        final var result = Fn1.asTry(function);

        assertNotNull(result);
        assertInstanceOf(Function.class, result);
        assertEquals(5, result.apply("hello"));
    }

    @Test
    public void testAsTryWithExceptionThrowingFunction() {

        final var function = (Fn1<String, Integer>) (s) -> {
            throw new IOException("Test exception");
        };

        final var wrappedFunction = Fn1.asTry(function);

        assertThrows(UnexpectedException.class, () -> wrappedFunction.apply("test"));
    }

    @Test
    public void testAsTryWithNullInput() {

        final var function = (Fn1<String, Integer>) (s) -> (s == null) ? 0 : s.length();
        final var result = Fn1.asTry(function);

        assertNotNull(result);
        assertEquals(0, result.apply(null));
    }

    @Test
    public void testAsTryWithNullReturn() {

        final var function = (Fn1<String, String>) (s) -> null;
        final var result = Fn1.asTry(function);

        assertNotNull(result);
        assertNull(result.apply("test"));
    }

    @Test
    public void testAsIoWithSuccessfulFunction() {

        final var function = (Fn1<String, Integer>) String::length;
        final var result = Fn1.asIo(function);

        assertNotNull(result);
        assertInstanceOf(Function.class, result);
        assertEquals(5, result.apply("hello"));
    }

    @Test
    public void testAsIoWithExceptionThrowingFunction() {

        final var function = (Fn1<String, Integer>) (s) -> {
            throw new IOException("Test exception");
        };

        final var wrappedFunction = Fn1.asIo(function);

        assertThrows(IoException.class, () -> wrappedFunction.apply("test"));
    }

    @Test
    public void testAsIoWithNullInput() {

        final var function = (Fn1<String, Integer>) (s) -> (s == null) ? 0 : s.length();
        final var result = Fn1.asIo(function);

        assertNotNull(result);
        assertEquals(0, result.apply(null));
    }

    @Test
    public void testAsIoWithNullReturn() {

        final var function = (Fn1<String, String>) (s) -> null;
        final var result = Fn1.asIo(function);

        assertNotNull(result);
        assertNull(result.apply("test"));
    }

    @Test
    public void testAsTryExceptionWrapping() {

        final var function = (Fn1<String, Integer>) (s) -> {
            throw new RuntimeException("Original exception");
        };

        final var wrappedFunction = Fn1.asTry(function);
        final var thrown = assertThrows(UnexpectedException.class, () -> wrappedFunction.apply("test"));

        assertNotNull(thrown.getCause());
        assertEquals("Original exception", thrown.getCause().getMessage());
    }

    @Test
    public void testAsIoExceptionWrapping() {

        final var function = (Fn1<String, Integer>) (s) -> {
            throw new RuntimeException("Original exception");
        };

        final var wrappedFunction = Fn1.asIo(function);
        final var thrown = assertThrows(IoException.class, () -> wrappedFunction.apply("test"));

        assertNotNull(thrown.getCause());
        assertEquals("Original exception", thrown.getCause().getMessage());
    }

    @Test
    public void testDirectFn1Usage() {

        final var fn1 = new Fn1<String, Integer>() {
            @Override
            public Integer apply(final String s) {
                return s.length();
            }
        };

        assertDoesNotThrow(() -> {
            final var result = fn1.apply("test");
            assertEquals(4, result);
        });
    }

    @Test
    public void testDirectFn1WithException() {

        final var fn1 = new Fn1<String, Integer>() {
            @Override
            public Integer apply(final String s) throws Exception {
                throw new Exception("Direct exception");
            }
        };

        assertThrows(Exception.class, () -> fn1.apply("test"));
    }

    @Test
    public void testAsTryWithDifferentTypes() {

        final var intToString = (Fn1<Integer, String>) Object::toString;
        final var stringToBoolean = (Fn1<String, Boolean>) Boolean::parseBoolean;

        final var intResult = Fn1.asTry(intToString);
        final var boolResult = Fn1.asTry(stringToBoolean);

        assertEquals("42", intResult.apply(42));
        assertTrue(boolResult.apply("true"));
        assertFalse(boolResult.apply("false"));
    }

    @Test
    public void testAsIoWithDifferentTypes() {

        final var intToString = (Fn1<Integer, String>) Object::toString;
        final var stringToBoolean = (Fn1<String, Boolean>) Boolean::parseBoolean;

        final var intResult = Fn1.asIo(intToString);
        final var boolResult = Fn1.asIo(stringToBoolean);

        assertEquals("42", intResult.apply(42));
        assertTrue(boolResult.apply("true"));
        assertFalse(boolResult.apply("false"));
    }

    @Test
    public void testAsTryWithDifferentExceptionTypes() {

        final var ioFunction = (Fn1<String, Integer>) (s) -> {
            throw new IOException("IO exception");
        };

        final var runtimeFunction = (Fn1<String, Integer>) (s) -> {
            throw new RuntimeException("Runtime exception");
        };

        final var checkedFunction = (Fn1<String, Integer>) (s) -> {
            throw new Exception("Checked exception");
        };

        final var ioResult = Fn1.asTry(ioFunction);
        final var runtimeResult = Fn1.asTry(runtimeFunction);
        final var checkedResult = Fn1.asTry(checkedFunction);

        assertThrows(UnexpectedException.class, () -> ioResult.apply("test"));
        assertThrows(UnexpectedException.class, () -> runtimeResult.apply("test"));
        assertThrows(UnexpectedException.class, () -> checkedResult.apply("test"));
    }

    @Test
    public void testAsIoWithDifferentExceptionTypes() {

        final var ioFunction = (Fn1<String, Integer>) (s) -> {
            throw new IOException("IO exception");
        };

        final var runtimeFunction = (Fn1<String, Integer>) (s) -> {
            throw new RuntimeException("Runtime exception");
        };

        final var checkedFunction = (Fn1<String, Integer>) (s) -> {
            throw new Exception("Checked exception");
        };

        final var ioResult = Fn1.asIo(ioFunction);
        final var runtimeResult = Fn1.asIo(runtimeFunction);
        final var checkedResult = Fn1.asIo(checkedFunction);

        assertThrows(IoException.class, () -> ioResult.apply("test"));
        assertThrows(IoException.class, () -> runtimeResult.apply("test"));
        assertThrows(IoException.class, () -> checkedResult.apply("test"));
    }
}
