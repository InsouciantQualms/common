/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.error;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Tests for the Catch utility class exception handling methods.
 */
final class CatchTest {

    @Test
    public void testWithVoidHandlesException() {

        final var handlerCalled = new AtomicBoolean(false);
        final var exception = new RuntimeException("test exception");

        Catch.withVoid(exception, throwable -> {
            handlerCalled.set(true);
            Assertions.assertEquals(exception, throwable);
        });

        Assertions.assertTrue(handlerCalled.get());
    }

    @Test
    public void testWithVoidRethrowsError() {

        final var error = new OutOfMemoryError("test error");
        final var handlerCalled = new AtomicBoolean(false);

        Assertions.assertThrows(OutOfMemoryError.class, () -> 
            Catch.withVoid(error, throwable -> {
                handlerCalled.set(true);
                Assertions.assertEquals(error, throwable);
            })
        );

        Assertions.assertTrue(handlerCalled.get());
    }

    @Test
    public void testWithVoidHandlesChainedException() {

        final var originalException = new RuntimeException("original");
        final var chainedException = new IllegalArgumentException("chained");

        Assertions.assertThrows(RuntimeException.class, () -> 
            Catch.withVoid(originalException, throwable -> {
                throw chainedException;
            })
        );
    }

    @Test
    public void testWithReturnHandlesException() {

        final var exception = new RuntimeException("test exception");
        final var expectedResult = "handled";

        final var result = Catch.withReturn(exception, throwable -> {
            Assertions.assertEquals(exception, throwable);
            return expectedResult;
        });

        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testWithReturnRethrowsError() {

        final var error = new OutOfMemoryError("test error");

        Assertions.assertThrows(OutOfMemoryError.class, () -> 
            Catch.withReturn(error, throwable -> {
                Assertions.assertEquals(error, throwable);
                return "should not reach here";
            })
        );
    }

    @Test
    public void testWithReturnHandlesChainedException() {

        final var originalException = new RuntimeException("original");
        final var chainedException = new IllegalArgumentException("chained");

        Assertions.assertThrows(RuntimeException.class, () -> 
            Catch.withReturn(originalException, throwable -> {
                throw chainedException;
            })
        );
    }

    @Test
    public void testWithReturnHandlesRuntimeException() {

        final var runtimeException = new IllegalStateException("runtime");
        final var expectedResult = "handled runtime";

        final var result = Catch.withReturn(runtimeException, throwable -> {
            Assertions.assertEquals(runtimeException, throwable);
            return expectedResult;
        });

        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testWithVoidHandlesRuntimeException() {

        final var runtimeException = new IllegalStateException("runtime");
        final var handlerCalled = new AtomicBoolean(false);

        Catch.withVoid(runtimeException, throwable -> {
            handlerCalled.set(true);
            Assertions.assertEquals(runtimeException, throwable);
        });

        Assertions.assertTrue(handlerCalled.get());
    }

    @Test
    public void testWithReturnHandlesCheckedException() {

        final var checkedException = new Exception("checked");
        final var expectedResult = "handled checked";

        final var result = Catch.withReturn(checkedException, throwable -> {
            Assertions.assertEquals(checkedException, throwable);
            return expectedResult;
        });

        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void testWithVoidHandlesCheckedException() {

        final var checkedException = new Exception("checked");
        final var handlerCalled = new AtomicBoolean(false);

        Catch.withVoid(checkedException, throwable -> {
            handlerCalled.set(true);
            Assertions.assertEquals(checkedException, throwable);
        });

        Assertions.assertTrue(handlerCalled.get());
    }

    @Test
    public void testWithReturnConvertsUnknownThrowableToUnexpectedException() {

        final var unknownThrowable = new CustomThrowable("unknown");

        Assertions.assertThrows(UnexpectedException.class, () -> 
            Catch.withReturn(unknownThrowable, throwable -> {
                Assertions.assertEquals(unknownThrowable, throwable);
                return "should not reach here";
            })
        );
    }

    @Test
    public void testWithVoidConvertsUnknownThrowableToUnexpectedException() {

        final var unknownThrowable = new CustomThrowable("unknown");

        Assertions.assertThrows(UnexpectedException.class, () -> 
            Catch.withVoid(unknownThrowable, throwable -> {
                Assertions.assertEquals(unknownThrowable, throwable);
            })
        );
    }

    /**
     * Custom throwable class for testing unknown throwable handling.
     */
    private static final class CustomThrowable extends Throwable {
        
        /**
         * Creates a custom throwable with the specified message.
         */
        public CustomThrowable(final String message) {
            
            super(message);
        }
    }
}