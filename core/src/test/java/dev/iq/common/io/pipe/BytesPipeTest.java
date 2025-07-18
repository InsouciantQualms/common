/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.pipe;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** Tests for BytesPipe covering byte array operations with input and output streams. */
public final class BytesPipeTest {

    @Test
    public void testReadFromInputStream() {

        final var pipe = new BytesPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(testData);

        final var result = pipe.read(inputStream);

        assertArrayEquals(testData, result);
    }

    @Test
    public void testReadFromEmptyInputStream() {

        final var pipe = new BytesPipe();
        final var inputStream = new ByteArrayInputStream(new byte[0]);

        final var result = pipe.read(inputStream);

        assertEquals(0, result.length);
    }

    @Test
    public void testWriteToOutputStream() {

        final var pipe = new BytesPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var outputStream = new ByteArrayOutputStream();

        pipe.write(testData, outputStream);

        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testWriteEmptyByteArray() {

        final var pipe = new BytesPipe();
        final var testData = new byte[0];
        final var outputStream = new ByteArrayOutputStream();

        pipe.write(testData, outputStream);

        assertEquals(0, outputStream.toByteArray().length);
    }

    @Test
    public void testGoWithDefaultBufferSize() {

        final var pipe = new BytesPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();

        final var bytesProcessed = pipe.go(inputStream, outputStream);

        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testGoWithCustomBufferSize() {

        final var pipe = new BytesPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();

        final var bytesProcessed = pipe.go(inputStream, outputStream, 4);

        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testGoWithLargeData() {

        final var pipe = new BytesPipe();
        final var testData = new byte[10000];
        for (var i = 0; i < testData.length; i++) {
            testData[i] = (byte) (i % 256);
        }
        final var inputStream = new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();

        final var bytesProcessed = pipe.go(inputStream, outputStream, 512);

        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testGoWithEmptyStream() {

        final var pipe = new BytesPipe();
        final var inputStream = new ByteArrayInputStream(new byte[0]);
        final var outputStream = new ByteArrayOutputStream();

        final var bytesProcessed = pipe.go(inputStream, outputStream);

        assertEquals(0, bytesProcessed);
        assertEquals(0, outputStream.toByteArray().length);
    }

    @Test
    public void testGoWithSmallBufferSize() {

        final var pipe = new BytesPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();

        final var bytesProcessed = pipe.go(inputStream, outputStream, 1);

        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testStreamNotClosed() {

        final var pipe = new BytesPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new TestInputStream(testData);
        final var outputStream = new TestOutputStream();

        pipe.go(inputStream, outputStream);

        assertFalse(inputStream.wasClosed());
        assertFalse(outputStream.wasClosed());
    }

    private static final class TestInputStream extends ByteArrayInputStream {
        private boolean closed = false;

        TestInputStream(final byte[] buf) {
            super(buf);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }

        public boolean wasClosed() {
            return closed;
        }
    }

    private static final class TestOutputStream extends ByteArrayOutputStream {
        private boolean closed = false;

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }

        public boolean wasClosed() {
            return closed;
        }
    }
}
