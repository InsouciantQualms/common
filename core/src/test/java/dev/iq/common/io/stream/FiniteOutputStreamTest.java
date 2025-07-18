/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** Tests for FiniteOutputStream covering finite writing with byte limits. */
public final class FiniteOutputStreamTest {

    @Test
    public void testWriteSingleByteWithinLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 10);

        finiteStream.write('H');

        assertEquals("H", outputStream.toString());
    }

    @Test
    public void testWriteSingleByteAtLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 1);

        finiteStream.write('H');

        assertEquals("H", outputStream.toString());
    }

    @Test
    public void testWriteSingleByteExceedsLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 1);

        finiteStream.write('H');

        assertThrows(EOFException.class, () -> finiteStream.write('e'));
    }

    @Test
    public void testWriteSingleByteZeroLimit() {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 0);

        assertThrows(EOFException.class, () -> finiteStream.write('H'));
    }

    @Test
    public void testWriteBufferWithinLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 20);
        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        finiteStream.write(data);

        assertEquals("Hello, World!", outputStream.toString());
    }

    @Test
    public void testWriteBufferAtLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 5);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        finiteStream.write(data);

        assertEquals("Hello", outputStream.toString());
    }

    @Test
    public void testWriteBufferExceedsLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 2);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        finiteStream.write(data);

        assertEquals("He", outputStream.toString());
    }

    @Test
    public void testWriteBufferZeroLimit() {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 0);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        assertThrows(EOFException.class, () -> finiteStream.write(data));
    }

    @Test
    public void testWriteBufferWithOffsetAndLengthWithinLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 20);
        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        finiteStream.write(data, 2, 5);

        assertEquals("llo, ", outputStream.toString());
    }

    @Test
    public void testWriteBufferWithOffsetAndLengthAtLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 3);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        finiteStream.write(data, 1, 5);

        assertEquals("ell", outputStream.toString());
    }

    @Test
    public void testWriteBufferWithOffsetAndLengthExceedsLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 2);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        finiteStream.write(data, 1, 5);

        assertEquals("el", outputStream.toString());
    }

    @Test
    public void testWriteBufferWithOffsetAndLengthZeroLimit() {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 0);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        assertThrows(EOFException.class, () -> finiteStream.write(data, 1, 3));
    }

    @Test
    public void testMultipleWritesWithinLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 10);

        finiteStream.write('H');
        finiteStream.write('e');
        finiteStream.write('l');

        assertEquals("Hel", outputStream.toString());
    }

    @Test
    public void testMultipleWritesExceedLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 2);

        finiteStream.write('H');
        finiteStream.write('e');

        assertThrows(EOFException.class, () -> finiteStream.write('l'));
    }

    @Test
    public void testWriteEmptyBuffer() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 5);
        final var data = new byte[0];

        finiteStream.write(data);

        assertEquals("", outputStream.toString());
    }

    @Test
    public void testWriteEmptyBufferWithOffset() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 5);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        finiteStream.write(data, 2, 0);

        assertEquals("", outputStream.toString());
    }

    @Test
    public void testExceptionMessageForSingleByte() {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 0);

        final var exception = assertThrows(EOFException.class, () -> finiteStream.write('H'));
        assertEquals("Maximum number of bytes written", exception.getMessage());
    }

    @Test
    public void testExceptionMessageForBuffer() {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 0);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        final var exception = assertThrows(EOFException.class, () -> finiteStream.write(data));
        assertEquals("Maximum number of bytes written", exception.getMessage());
    }

    @Test
    public void testExceptionMessageForBufferWithOffset() {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 0);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        final var exception = assertThrows(EOFException.class, () -> finiteStream.write(data, 0, 3));
        assertEquals("Maximum number of bytes written", exception.getMessage());
    }

    @Test
    public void testLargeLimitValue() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, Long.MAX_VALUE);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        finiteStream.write(data);

        assertEquals("Hello", outputStream.toString());
    }

    @Test
    public void testNegativeLimitValue() {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, -1);

        assertThrows(EOFException.class, () -> finiteStream.write('H'));
    }

    @Test
    public void testMixedWriteOperations() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 10);

        finiteStream.write('H');
        finiteStream.write("ello".getBytes(StandardCharsets.UTF_8));
        finiteStream.write(", World!".getBytes(StandardCharsets.UTF_8), 0, 5);

        assertEquals("Hello,", outputStream.toString());
    }

    @Test
    public void testMixedWriteOperationsExceedLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 5);

        finiteStream.write('H');
        finiteStream.write("ello".getBytes(StandardCharsets.UTF_8));

        assertThrows(EOFException.class, () -> finiteStream.write('!'));
    }

    @Test
    public void testPartialWriteWhenLimitReached() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 3);

        finiteStream.write('H');
        finiteStream.write("ello, World!".getBytes(StandardCharsets.UTF_8));

        assertEquals("Hel", outputStream.toString());
    }

    @Test
    public void testPartialWriteWithOffsetWhenLimitReached() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 3);

        finiteStream.write('H');
        finiteStream.write("ello, World!".getBytes(StandardCharsets.UTF_8), 1, 10);

        assertEquals("Hll", outputStream.toString());
    }

    @Test
    public void testFlushAndCloseOperations() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 5);

        finiteStream.write("Hello".getBytes(StandardCharsets.UTF_8));
        finiteStream.flush();

        assertEquals("Hello", outputStream.toString());

        assertDoesNotThrow(finiteStream::close);
    }

    @Test
    public void testWriteAfterReachingLimit() throws IOException {

        final var outputStream = new ByteArrayOutputStream();
        final var finiteStream = new FiniteOutputStream(outputStream, 2);

        finiteStream.write("Hello".getBytes(StandardCharsets.UTF_8));

        assertEquals("He", outputStream.toString());

        // Further writes should fail
        assertThrows(EOFException.class, () -> finiteStream.write('l'));
    }
}
