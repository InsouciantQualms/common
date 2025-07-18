/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Tests for the HttpException class covering HTTP error functionality. */
public final class HttpExceptionTest {

    @Test
    public void testConstructorWithStatusAndBody() {

        final var exception = new HttpException(404, "Not Found");

        assertEquals(404, exception.getStatusCode());
        assertEquals("Not Found", exception.getBody());
    }

    @Test
    public void testConstructorWithNullBody() {

        final var exception = new HttpException(500, null);

        assertEquals(500, exception.getStatusCode());
        assertNull(exception.getBody());
    }

    @Test
    public void testConstructorWithEmptyBody() {

        final var exception = new HttpException(400, "");

        assertEquals(400, exception.getStatusCode());
        assertEquals("", exception.getBody());
    }

    @Test
    public void testGetStatusCode() {

        final var exception = new HttpException(200, "OK");

        assertEquals(200, exception.getStatusCode());
    }

    @Test
    public void testGetBody() {

        final var exception = new HttpException(403, "Forbidden");

        assertEquals("Forbidden", exception.getBody());
    }

    @Test
    public void testGetMessageWithValidBody() {

        final var exception = new HttpException(401, "Unauthorized");

        assertEquals("401 - Unauthorized", exception.getMessage());
    }

    @Test
    public void testGetMessageWithNullBody() {

        final var exception = new HttpException(500, null);

        assertEquals("500 - null", exception.getMessage());
    }

    @Test
    public void testGetMessageWithEmptyBody() {

        final var exception = new HttpException(204, "");

        assertEquals("204 - ", exception.getMessage());
    }

    @Test
    public void testGetMessageWithLongBody() {

        final var longBody =
                "This is a very long error message that contains multiple words and sentences to test the formatting.";
        final var exception = new HttpException(422, longBody);

        assertEquals("422 - " + longBody, exception.getMessage());
    }

    @Test
    public void testExceptionInheritance() {

        final var exception = new HttpException(404, "Not Found");

        assertInstanceOf(RuntimeException.class, exception);
        assertInstanceOf(Exception.class, exception);
        assertInstanceOf(Throwable.class, exception);
    }

    @Test
    public void testThrowingException() {

        final var exception = new HttpException(500, "Internal Server Error");

        assertThrows(HttpException.class, () -> {
            throw exception;
        });
    }

    @Test
    public void testCatchingException() {

        try {
            throw new HttpException(400, "Bad Request");
        } catch (final HttpException e) {
            assertEquals(400, e.getStatusCode());
            assertEquals("Bad Request", e.getBody());
            assertEquals("400 - Bad Request", e.getMessage());
        }
    }

    @Test
    public void testDifferentStatusCodes() {

        final var clientError = new HttpException(400, "Bad Request");
        final var serverError = new HttpException(500, "Internal Server Error");
        final var notFound = new HttpException(404, "Not Found");

        assertEquals(400, clientError.getStatusCode());
        assertEquals(500, serverError.getStatusCode());
        assertEquals(404, notFound.getStatusCode());
    }

    @Test
    public void testBodyWithSpecialCharacters() {

        final var body = "Error: Invalid JSON {\"error\": \"parsing failed\"}";
        final var exception = new HttpException(400, body);

        assertEquals(body, exception.getBody());
        assertEquals("400 - " + body, exception.getMessage());
    }

    @Test
    public void testBodyWithNewlines() {

        final var body = "Error line 1\nError line 2\nError line 3";
        final var exception = new HttpException(500, body);

        assertEquals(body, exception.getBody());
        assertEquals("500 - " + body, exception.getMessage());
    }

    @Test
    public void testStatusCodeBoundaries() {

        final var minCode = new HttpException(100, "Continue");
        final var maxCode = new HttpException(599, "Network Error");

        assertEquals(100, minCode.getStatusCode());
        assertEquals(599, maxCode.getStatusCode());
    }

    @Test
    public void testExceptionEquality() {

        final var exception1 = new HttpException(404, "Not Found");
        final var exception2 = new HttpException(404, "Not Found");

        assertEquals(exception1.getStatusCode(), exception2.getStatusCode());
        assertEquals(exception1.getBody(), exception2.getBody());
        assertEquals(exception1.getMessage(), exception2.getMessage());
    }
}
