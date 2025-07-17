/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.stream;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Tee covering multi-stream output operations.
 */
public final class TeeTest {

    @Test
    public void testWriteSingleByteToMultipleStreams() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2);

        tee.write('H');

        assertEquals("H", stream1.toString());
        assertEquals("H", stream2.toString());
    }

    @Test
    public void testWriteMultipleSingleBytesToMultipleStreams() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2);

        tee.write('H');
        tee.write('e');
        tee.write('l');

        assertEquals("Hel", stream1.toString());
        assertEquals("Hel", stream2.toString());
    }

    @Test
    public void testWriteBufferToMultipleStreams() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2);
        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        tee.write(data);

        assertEquals("Hello, World!", stream1.toString());
        assertEquals("Hello, World!", stream2.toString());
    }

    @Test
    public void testWriteBufferWithOffsetAndLengthToMultipleStreams() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2);
        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        tee.write(data, 2, 5);

        assertEquals("llo, ", stream1.toString());
        assertEquals("llo, ", stream2.toString());
    }

    @Test
    public void testWriteToSingleStream() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1);

        tee.write('H');

        assertEquals("H", stream1.toString());
    }

    @Test
    public void testWriteToThreeStreams() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new ByteArrayOutputStream();
        final var stream3 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2, stream3);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        tee.write(data);

        assertEquals("Hello", stream1.toString());
        assertEquals("Hello", stream2.toString());
        assertEquals("Hello", stream3.toString());
    }

    @Test
    public void testWriteToNoStreams() {

        final var tee = new Tee();

        // Should not throw an exception
        assertDoesNotThrow(() -> tee.write('H'));
        assertDoesNotThrow(() -> tee.write("Hello".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    public void testFlushMultipleStreams() throws IOException {

        final var stream1 = new TestOutputStream();
        final var stream2 = new TestOutputStream();
        final var tee = new Tee(stream1, stream2);

        tee.write('H');
        tee.flush();

        assertTrue(stream1.wasFlushed());
        assertTrue(stream2.wasFlushed());
    }

    @Test
    public void testCloseMultipleStreams() throws IOException {

        final var stream1 = new TestOutputStream();
        final var stream2 = new TestOutputStream();
        final var tee = new Tee(stream1, stream2);

        tee.close();

        assertTrue(stream1.wasClosed());
        assertTrue(stream2.wasClosed());
    }

    @Test
    public void testMixedWriteOperations() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2);
        final var data = "World!".getBytes(StandardCharsets.UTF_8);

        tee.write('H');
        tee.write("ello, ".getBytes(StandardCharsets.UTF_8));
        tee.write(data, 0, 5);

        assertEquals("Hello, World", stream1.toString());
        assertEquals("Hello, World", stream2.toString());
    }

    @Test
    public void testWriteEmptyBuffer() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2);
        final var data = new byte[0];

        tee.write(data);

        assertEquals("", stream1.toString());
        assertEquals("", stream2.toString());
    }

    @Test
    public void testWriteBufferWithZeroLength() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        tee.write(data, 2, 0);

        assertEquals("", stream1.toString());
        assertEquals("", stream2.toString());
    }

    @Test
    public void testWriteLargeData() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2);
        final var data = new byte[10000];
        Arrays.fill(data, (byte) 'A');

        tee.write(data);

        assertArrayEquals(data, stream1.toByteArray());
        assertArrayEquals(data, stream2.toByteArray());
    }

    @Test
    public void testWriteBinaryData() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2);
        final var data = new byte[]{0x00, 0x01, 0x02, (byte) 0xFF, 0x7F, (byte) 0x80};

        tee.write(data);

        assertArrayEquals(data, stream1.toByteArray());
        assertArrayEquals(data, stream2.toByteArray());
    }

    @Test
    public void testExceptionInOneStreamDoesNotAffectOthers() {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new FailingOutputStream();
        final var stream3 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2, stream3);

        assertThrows(IOException.class, () -> tee.write('H'));

        // First stream should still have received the data
        assertEquals("H", stream1.toString());
        // Third stream should not have received the data due to the exception
        assertEquals("", stream3.toString());
    }

    @Test
    public void testCloseWithExceptionsInMultipleStreams() {

        final var stream1 = new FailingOutputStream();
        final var stream2 = new FailingOutputStream();
        final var tee = new Tee(stream1, stream2);

        final var exception = assertThrows(IOException.class, tee::close);

        assertEquals("Error closing one or more streams", exception.getMessage());
        assertEquals(2, exception.getSuppressed().length);
    }

    @Test
    public void testCloseWithExceptionInOneStream() {

        final var stream1 = new TestOutputStream();
        final var stream2 = new FailingOutputStream();
        final var tee = new Tee(stream1, stream2);

        final var exception = assertThrows(IOException.class, tee::close);

        assertEquals("Error closing one or more streams", exception.getMessage());
        assertEquals(1, exception.getSuppressed().length);
        assertTrue(stream1.wasClosed());
    }

    @Test
    public void testCloseWithNoExceptions() {

        final var stream1 = new TestOutputStream();
        final var stream2 = new TestOutputStream();
        final var tee = new Tee(stream1, stream2);

        assertDoesNotThrow(tee::close);

        assertTrue(stream1.wasClosed());
        assertTrue(stream2.wasClosed());
    }

    @Test
    public void testFlushWithExceptionInOneStream() {

        final var stream1 = new TestOutputStream();
        final var stream2 = new FailingOutputStream();
        final var tee = new Tee(stream1, stream2);

        assertThrows(IOException.class, tee::flush);

        assertTrue(stream1.wasFlushed());
    }

    @Test
    public void testWriteAfterClose() throws IOException {

        final var stream1 = new TestOutputStream();
        final var stream2 = new TestOutputStream();
        final var tee = new Tee(stream1, stream2);

        tee.close();

        // Writing after close should still work (depends on underlying stream behavior)
        assertDoesNotThrow(() -> tee.write('H'));
    }

    @Test
    public void testDifferentStreamTypes() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new NullOutputStream();
        final var tee = new Tee(stream1, stream2);
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        tee.write(data);

        assertEquals("Hello", stream1.toString());
        // NullOutputStream consumes everything, so no verification needed
    }

    @Test
    public void testSequentialWriteOrder() throws IOException {

        final var stream1 = new OrderTrackingOutputStream();
        final var stream2 = new OrderTrackingOutputStream();
        final var tee = new Tee(stream1, stream2);

        tee.write('A');
        tee.write('B');

        assertEquals("AB", stream1.getData());
        assertEquals("AB", stream2.getData());
    }

    @Test
    public void testWriteNegativeValue() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2);

        tee.write(-1);

        assertEquals(1, stream1.toByteArray().length);
        assertEquals(1, stream2.toByteArray().length);
        assertEquals((byte) 0xFF, stream1.toByteArray()[0]);
        assertEquals((byte) 0xFF, stream2.toByteArray()[0]);
    }

    @Test
    public void testWriteLargeValue() throws IOException {

        final var stream1 = new ByteArrayOutputStream();
        final var stream2 = new ByteArrayOutputStream();
        final var tee = new Tee(stream1, stream2);

        tee.write(256); // Should be truncated to 0

        assertEquals(1, stream1.toByteArray().length);
        assertEquals(1, stream2.toByteArray().length);
        assertEquals((byte) 0x00, stream1.toByteArray()[0]);
        assertEquals((byte) 0x00, stream2.toByteArray()[0]);
    }

    private static final class TestOutputStream extends ByteArrayOutputStream {
        private boolean flushed = false;
        private boolean closed = false;

        @Override
        public void flush() throws IOException {
            flushed = true;
            super.flush();
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }

        public boolean wasFlushed() {
            return flushed;
        }

        public boolean wasClosed() {
            return closed;
        }
    }

    private static final class FailingOutputStream extends OutputStream {
        @Override
        public void write(final int b) throws IOException {
            throw new IOException("Simulated failure");
        }

        @Override
        public void flush() throws IOException {
            throw new IOException("Simulated flush failure");
        }

        @Override
        public void close() throws IOException {
            throw new IOException("Simulated close failure");
        }
    }

    private static final class OrderTrackingOutputStream extends OutputStream {
        private final StringBuilder data = new StringBuilder();

        @Override
        public void write(final int b) {
            data.append((char) b);
        }

        public String getData() {
            return data.toString();
        }
    }
}