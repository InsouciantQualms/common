/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests for FiniteInputStream covering finite reading with byte limits. */
final class FiniteInputStreamTest {

    @Test
    void testReadSingleByteWithinLimit() throws IOException {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 10);

        final var result = finiteStream.read();

        assertEquals('H', result);
    }

    @Test
    void testReadSingleByteAtLimit() throws IOException {

        final var data = "H".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 1);

        final var result = finiteStream.read();

        assertEquals('H', result);
    }

    @Test
    void testReadSingleByteExceedsLimit() throws IOException {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        try (var finiteStream = new FiniteInputStream(inputStream, 1)) {

            finiteStream.read(); // Read one byte

            assertThrows(EOFException.class, finiteStream::read);
        }
    }

    @Test
    void testReadSingleByteZeroLimit() throws IOException {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        try (var finiteStream = new FiniteInputStream(inputStream, 0)) {

            assertThrows(EOFException.class, finiteStream::read);
        }
    }

    @Test
    void testReadBufferWithinLimit() throws IOException {

        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 20);
        final var buffer = new byte[10];

        final var bytesRead = finiteStream.read(buffer);

        assertEquals(10, bytesRead);
        Assertions.assertArrayEquals("Hello, Wor".getBytes(StandardCharsets.UTF_8), buffer);
    }

    @Test
    void testReadBufferAtLimit() throws IOException {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 5);
        final var buffer = new byte[10];

        final var bytesRead = finiteStream.read(buffer);

        assertEquals(5, bytesRead);
        Assertions.assertArrayEquals("Hello".getBytes(StandardCharsets.UTF_8), Arrays.copyOf(buffer, 5));
    }

    @Test
    void testReadBufferExceedsLimit() throws IOException {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 2);
        final var buffer = new byte[10];

        final var bytesRead = finiteStream.read(buffer);

        assertEquals(2, bytesRead);
        Assertions.assertArrayEquals("He".getBytes(StandardCharsets.UTF_8), Arrays.copyOf(buffer, 2));
    }

    @Test
    void testReadBufferZeroLimit() {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 0);
        final var buffer = new byte[10];

        assertThrows(EOFException.class, () -> finiteStream.read(buffer));
    }

    @Test
    void testReadBufferWithOffsetAndLengthWithinLimit() throws IOException {

        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 20);
        final var buffer = new byte[10];

        final var bytesRead = finiteStream.read(buffer, 2, 5);

        assertEquals(5, bytesRead);
        Assertions.assertArrayEquals("Hello".getBytes(StandardCharsets.UTF_8), Arrays.copyOfRange(buffer, 2, 7));
    }

    @Test
    void testReadBufferWithOffsetAndLengthAtLimit() throws IOException {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 3);
        final var buffer = new byte[10];

        final var bytesRead = finiteStream.read(buffer, 2, 5);

        assertEquals(3, bytesRead);
        Assertions.assertArrayEquals("Hel".getBytes(StandardCharsets.UTF_8), Arrays.copyOfRange(buffer, 2, 5));
    }

    @Test
    void testReadBufferWithOffsetAndLengthExceedsLimit() throws IOException {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 2);
        final var buffer = new byte[10];

        final var bytesRead = finiteStream.read(buffer, 2, 5);

        assertEquals(2, bytesRead);
        Assertions.assertArrayEquals("He".getBytes(StandardCharsets.UTF_8), Arrays.copyOfRange(buffer, 2, 4));
    }

    @Test
    void testReadBufferWithOffsetAndLengthZeroLimit() {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 0);
        final var buffer = new byte[10];

        assertThrows(EOFException.class, () -> finiteStream.read(buffer, 2, 5));
    }

    @Test
    void testDefaultConstructorUsesLobBufferLength() throws IOException {

        final var data = new byte[1000];
        Arrays.fill(data, (byte) 'A');
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream);

        // Should be able to read the first 1000 bytes without issues
        final var buffer = new byte[1000];
        final var bytesRead = finiteStream.read(buffer);

        assertEquals(1000, bytesRead);
        Assertions.assertArrayEquals(data, buffer);
    }

    @Test
    void testMultipleReadsWithinLimit() throws IOException {

        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 10);

        final var first = finiteStream.read();
        final var second = finiteStream.read();

        assertEquals('H', first);
        assertEquals('e', second);
    }

    @Test
    void testMultipleReadsExceedLimit() throws IOException {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 2);

        assertEquals('H', finiteStream.read());
        assertEquals('e', finiteStream.read());

        assertThrows(EOFException.class, finiteStream::read);
    }

    @Test
    void testReadEofFromUnderlyingStream() throws IOException {

        final var data = "Hi".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 10);

        assertEquals('H', finiteStream.read());
        assertEquals('i', finiteStream.read());
        assertEquals(-1, finiteStream.read()); // EOF from underlying stream
    }

    @Test
    void testReadBufferEofFromUnderlyingStream() throws IOException {

        final var data = "Hi".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 10);
        final var buffer = new byte[10];

        final var bytesRead = finiteStream.read(buffer);

        assertEquals(2, bytesRead);
        Assertions.assertArrayEquals("Hi".getBytes(StandardCharsets.UTF_8), Arrays.copyOf(buffer, 2));

        final var nextRead = finiteStream.read(buffer);
        assertEquals(-1, nextRead);
    }

    @Test
    void testExceptionMessageForSingleByte() {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 0);

        final var exception = assertThrows(EOFException.class, finiteStream::read);
        assertEquals("Maximum number of bytes read", exception.getMessage());
    }

    @Test
    void testExceptionMessageForBuffer() {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 0);
        final var buffer = new byte[10];

        final var exception = assertThrows(EOFException.class, () -> finiteStream.read(buffer));
        assertEquals("Maximum number of bytes read", exception.getMessage());
    }

    @Test
    void testExceptionMessageForBufferWithOffset() {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, 0);
        final var buffer = new byte[10];

        final var exception = assertThrows(EOFException.class, () -> finiteStream.read(buffer, 0, 5));
        assertEquals("Maximum number of bytes read", exception.getMessage());
    }

    @Test
    void testLargeLimitValue() throws IOException {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, Long.MAX_VALUE);

        final var buffer = new byte[10];
        final var bytesRead = finiteStream.read(buffer);

        assertEquals(5, bytesRead);
        Assertions.assertArrayEquals("Hello".getBytes(StandardCharsets.UTF_8), Arrays.copyOf(buffer, 5));
    }

    @Test
    void testNegativeLimitValue() {

        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var finiteStream = new FiniteInputStream(inputStream, -1);

        assertThrows(EOFException.class, finiteStream::read);
    }
}
