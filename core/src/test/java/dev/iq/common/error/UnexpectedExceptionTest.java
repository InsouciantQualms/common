/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.error;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.text.ParseException;

/**
 * Tests for the UnexpectedException custom exception class.
 */
final class UnexpectedExceptionTest {

    @Test
    public void testConstructorWithMessage() {

        final var message = "Test unexpected error message";
        final var exception = new UnexpectedException(message);

        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertNull(exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstructorWithMessageAndCause() {

        final var message = "Test unexpected error with cause";
        final var cause = new NullPointerException("Original NPE");
        final var exception = new UnexpectedException(message, cause);

        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstructorWithCause() {

        final var cause = new ArrayIndexOutOfBoundsException("Array index error");
        final var exception = new UnexpectedException(cause);

        Assertions.assertEquals(cause, exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
        // Message should be the toString of the cause
        Assertions.assertEquals(cause.toString(), exception.getMessage());
    }

    @Test
    public void testConstructorWithNullMessage() {

        final var exception = new UnexpectedException((String) null);

        Assertions.assertNull(exception.getMessage());
        Assertions.assertNull(exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstructorWithEmptyMessage() {

        final var message = "";
        final var exception = new UnexpectedException(message);

        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertNull(exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstructorWithNullCause() {

        final var exception = new UnexpectedException((Throwable) null);

        Assertions.assertNull(exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstructorWithMessageAndNullCause() {

        final var message = "Test message with null cause";
        final var exception = new UnexpectedException(message, null);

        Assertions.assertEquals(message, exception.getMessage());
        Assertions.assertNull(exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testConstructorWithNullMessageAndCause() {

        final var cause = new IllegalArgumentException("Invalid argument");
        final var exception = new UnexpectedException(null, cause);

        Assertions.assertNull(exception.getMessage());
        Assertions.assertEquals(cause, exception.getCause());
        Assertions.assertTrue(exception instanceof RuntimeException);
    }

    @Test
    public void testExceptionChaining() {

        final var rootCause = new NumberFormatException("Invalid number format");
        final var intermediateCause = new IllegalArgumentException("Invalid argument", rootCause);
        final var exception = new UnexpectedException("Final message", intermediateCause);

        Assertions.assertEquals("Final message", exception.getMessage());
        Assertions.assertEquals(intermediateCause, exception.getCause());
        Assertions.assertEquals(rootCause, exception.getCause().getCause());
    }

    @Test
    public void testSerializationConstantExists() {

        // Verify that the serialVersionUID field exists and is accessible
        try {
            final var field = UnexpectedException.class.getDeclaredField("serialVersionUID");
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

        final var exception = new UnexpectedException("Test message");

        Assertions.assertTrue(exception instanceof RuntimeException);
        Assertions.assertFalse(exception instanceof Exception && !(exception instanceof RuntimeException));
    }

    @Test
    public void testWithTypicalUnexpectedCauses() {

        // Test with various cause types that would typically be unexpected
        final var nullPointerCause = new NullPointerException("Null pointer");
        final var arrayIndexCause = new ArrayIndexOutOfBoundsException("Array index");
        final var parseCause = new ParseException("Parse error", 0);
        final var uriCause = new URISyntaxException("invalid uri", "Bad URI syntax");

        final var npeException = new UnexpectedException("NPE wrapper", nullPointerCause);
        final var arrayException = new UnexpectedException("Array wrapper", arrayIndexCause);
        final var parseException = new UnexpectedException("Parse wrapper", parseCause);
        final var uriException = new UnexpectedException("URI wrapper", uriCause);

        Assertions.assertEquals(nullPointerCause, npeException.getCause());
        Assertions.assertEquals(arrayIndexCause, arrayException.getCause());
        Assertions.assertEquals(parseCause, parseException.getCause());
        Assertions.assertEquals(uriCause, uriException.getCause());
    }

    @Test
    public void testStackTracePreservation() {

        final var originalException = new IllegalStateException("Original illegal state");
        final var unexpectedException = new UnexpectedException("Wrapped unexpected error", originalException);

        final var stackTrace = unexpectedException.getStackTrace();
        Assertions.assertNotNull(stackTrace);
        Assertions.assertTrue(stackTrace.length > 0);
        
        // Verify the cause stack trace is preserved
        Assertions.assertNotNull(unexpectedException.getCause().getStackTrace());
        Assertions.assertTrue(unexpectedException.getCause().getStackTrace().length > 0);
    }

    @Test
    public void testDifferentFromIoException() {

        final var message = "Test message";
        final var cause = new RuntimeException("Cause");
        
        final var unexpectedException = new UnexpectedException(message, cause);
        final var ioException = new IoException(message, cause);

        // Both should be runtime exceptions but different types
        Assertions.assertTrue(unexpectedException instanceof RuntimeException);
        Assertions.assertTrue(ioException instanceof RuntimeException);
        Assertions.assertNotEquals(unexpectedException.getClass(), ioException.getClass());
    }

    @Test
    public void testWithProgrammingErrors() {

        // Test with typical programming errors that should be unexpected
        final var classNotFoundCause = new ClassNotFoundException("Class not found");
        final var illegalAccessCause = new IllegalAccessException("Illegal access");
        final var instantiationCause = new InstantiationException("Cannot instantiate");

        final var classNotFoundException = new UnexpectedException("Class not found wrapper", classNotFoundCause);
        final var illegalAccessException = new UnexpectedException("Illegal access wrapper", illegalAccessCause);
        final var instantiationException = new UnexpectedException("Instantiation wrapper", instantiationCause);

        Assertions.assertEquals(classNotFoundCause, classNotFoundException.getCause());
        Assertions.assertEquals(illegalAccessCause, illegalAccessException.getCause());
        Assertions.assertEquals(instantiationCause, instantiationException.getCause());
    }
}