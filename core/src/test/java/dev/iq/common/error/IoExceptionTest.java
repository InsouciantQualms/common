/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.error;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Tests for the IoException custom exception class.
 */
final class IoExceptionTest {

    @Test
    public void testConstructorWithMessage() {

        final var message = "Test IO error message";
        final var exception = new IoException(message);

        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertNull(exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstructorWithMessageAndCause() {

        final var message = "Test IO error with cause";
        final var cause = new IOException("Original IO error");
        final var exception = new IoException(message, cause);

        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstructorWithCause() {

        final var cause = new SQLException("Database connection failed");
        final var exception = new IoException(cause);

        Assertions.assertEquals(cause, exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
        // Message should be the toString of the cause
        Assertions.assertEquals(cause.toString(), exception.getMessage());
    }

    @Test
    public void testConstructorWithNullMessage() {

        final var exception = new IoException((String) null);

        Assertions.assertNull(exception.getMessage());
        Assertions.assertNull(exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstructorWithEmptyMessage() {

        final var message = "";
        final var exception = new IoException(message);

        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertNull(exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstructorWithNullCause() {

        final var exception = new IoException((Throwable) null);

        Assertions.assertNull(exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstructorWithMessageAndNullCause() {

        final var message = "Test message with null cause";
        final var exception = new IoException(message, null);

        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertNull(exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstructorWithNullMessageAndCause() {

        final var cause = new IOException("IO error");
        final var exception = new IoException(null, cause);

        Assertions.assertNull(exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testExceptionChaining() {

        final var rootCause = new IOException("Root cause");
        final var intermediateCause = new SQLException("Intermediate cause", rootCause);
        final var exception = new IoException("Final message", intermediateCause);

        Assertions.assertEquals("Final message", exception.getMessage());
        Assertions.assertEquals(intermediateCause, exception.getCause());
        Assertions.assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    public void testSerializationConstantExists() {

        // Verify that the serialVersionUID field exists and is accessible
        try {
            final var field = IoException.class.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            final var serialVersionUID = field.get(null);
            Assertions.assertNotNull(serialVersionUID);
            Assertions.assertTrue(serialVersionUID instanceof Long);
        } catch (final Exception e) {
            Assertions.fail("serialVersionUID field should exist and be accessible");
        }
    }

    @Test
    public void testIsRuntimeException() {

        final var exception = new IoException("Test message");

        Assertions.assertTrue(exception instanceof RuntimeException);
        Assertions.assertFalse(exception instanceof Exception && !(exception instanceof RuntimeException));
    }

    @Test
    public void testWithDifferentCauseTypes() {

        // Test with various cause types that might be used in IO operations
        final var ioCause = new IOException("IO failure");
        final var sqlCause = new SQLException("SQL failure");
        final var runtimeCause = new RuntimeException("Runtime failure");

        final var ioException = new IoException("IO wrapper", ioCause);
        final var sqlException = new IoException("SQL wrapper", sqlCause);
        final var runtimeException = new IoException("Runtime wrapper", runtimeCause);

        Assertions.assertEquals(ioCause, ioException.getCause());
        Assertions.assertEquals(sqlCause, sqlException.getCause());
        Assertions.assertEquals(runtimeCause, runtimeException.getCause());
    }

    @Test
    public void testStackTracePreservation() {

        final var originalException = new IOException("Original IO error");
        final var ioException = new IoException("Wrapped IO error", originalException);

        final var stackTrace = ioException.getStackTrace();
        Assertions.assertNotNull(stackTrace);
        Assertions.assertTrue(stackTrace.length > 0);
        
        // Verify the cause stack trace is preserved
        Assertions.assertNotNull(ioException.getCause().getStackTrace());
        Assertions.assertTrue(ioException.getCause().getStackTrace().length > 0);
    }
}