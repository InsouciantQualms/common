/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

/** Tests for NullOutputStream covering null stream behavior that consumes all writes. */
public final class NullOutputStreamTest {

    @Test
    public void testWriteSingleByte() {

        final var nullStream = new NullOutputStream();

        assertDoesNotThrow(() -> nullStream.write('H'));
    }

    @Test
    public void testWriteMultipleSingleBytes() {

        final var nullStream = new NullOutputStream();

        assertDoesNotThrow(() -> {
            nullStream.write('H');
            nullStream.write('e');
            nullStream.write('l');
            nullStream.write('l');
            nullStream.write('o');
        });
    }

    @Test
    public void testWriteBuffer() {

        final var nullStream = new NullOutputStream();
        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        assertDoesNotThrow(() -> nullStream.write(data));
    }

    @Test
    public void testWriteBufferWithOffsetAndLength() {

        final var nullStream = new NullOutputStream();
        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        assertDoesNotThrow(() -> nullStream.write(data, 2, 5));
    }

    @Test
    public void testWriteEmptyBuffer() {

        final var nullStream = new NullOutputStream();
        final var data = new byte[0];

        assertDoesNotThrow(() -> nullStream.write(data));
    }

    @Test
    public void testWriteBufferWithZeroLength() {

        final var nullStream = new NullOutputStream();
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        assertDoesNotThrow(() -> nullStream.write(data, 0, 0));
    }

    @Test
    public void testWriteLargeBuffer() {

        final var nullStream = new NullOutputStream();
        final var data = new byte[10000];
        Arrays.fill(data, (byte) 'A');

        assertDoesNotThrow(() -> nullStream.write(data));
    }

    @Test
    public void testWriteBinaryData() {

        final var nullStream = new NullOutputStream();
        final var data = new byte[] {0x00, 0x01, 0x02, (byte) 0xFF, 0x7F, (byte) 0x80};

        assertDoesNotThrow(() -> nullStream.write(data));
    }

    @Test
    public void testFlushDoesNotThrow() {

        final var nullStream = new NullOutputStream();

        assertDoesNotThrow(nullStream::flush);
    }

    @Test
    public void testCloseDoesNotThrow() {

        final var nullStream = new NullOutputStream();

        assertDoesNotThrow(nullStream::close);
    }

    @Test
    public void testWriteAfterClose() throws IOException {

        final var nullStream = new NullOutputStream();

        nullStream.close();

        // Should still be able to write after close
        assertDoesNotThrow(() -> nullStream.write('H'));
    }

    @Test
    public void testWriteBufferAfterClose() throws IOException {

        final var nullStream = new NullOutputStream();
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);

        nullStream.close();

        // Should still be able to write buffer after close
        assertDoesNotThrow(() -> nullStream.write(data));
    }

    @Test
    public void testWriteNegativeValue() {

        final var nullStream = new NullOutputStream();

        assertDoesNotThrow(() -> nullStream.write(-1));
        assertDoesNotThrow(() -> nullStream.write(-128));
    }

    @Test
    public void testWriteLargeValue() {

        final var nullStream = new NullOutputStream();

        assertDoesNotThrow(() -> nullStream.write(256));
        assertDoesNotThrow(() -> nullStream.write(Integer.MAX_VALUE));
    }

    @Test
    public void testWriteAllByteValues() {

        final var nullStream = new NullOutputStream();

        // Test all possible byte values
        for (var i = 0; i < 256; i++) {
            final var value = i;
            assertDoesNotThrow(() -> nullStream.write(value));
        }
    }

    @Test
    public void testWriteNullBuffer() {

        final var nullStream = new NullOutputStream();

        // Should handle null buffer gracefully or throw appropriate exception
        assertThrows(Exception.class, () -> nullStream.write(null));
        assertThrows(Exception.class, () -> nullStream.write(null, 0, 5));
    }

    @Test
    public void testWriteInvalidOffsetAndLength() {

        final var nullStream = new NullOutputStream();
        final var buffer = new byte[10];

        // Should handle invalid offset/length appropriately
        assertThrows(Exception.class, () -> nullStream.write(buffer, -1, 5));
        assertThrows(Exception.class, () -> nullStream.write(buffer, 0, -1));
        assertThrows(Exception.class, () -> nullStream.write(buffer, 15, 5));
        assertThrows(Exception.class, () -> nullStream.write(buffer, 0, 15));
    }

    @Test
    public void testWriteWithValidOffsetAndLength() {

        final var nullStream = new NullOutputStream();
        final var buffer = new byte[10];
        Arrays.fill(buffer, (byte) 'A');

        // Valid offset and length combinations
        assertDoesNotThrow(() -> nullStream.write(buffer, 0, 10));
        assertDoesNotThrow(() -> nullStream.write(buffer, 5, 5));
        assertDoesNotThrow(() -> nullStream.write(buffer, 9, 1));
    }

    @Test
    public void testMixedWriteOperations() {

        final var nullStream = new NullOutputStream();
        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);

        assertDoesNotThrow(() -> {
            nullStream.write('H');
            nullStream.write(data);
            nullStream.write(data, 0, 5);
            nullStream.flush();
        });
    }

    @Test
    public void testPerformanceWithLargeData() {

        final var nullStream = new NullOutputStream();
        final var data = new byte[1000000]; // 1MB
        Arrays.fill(data, (byte) 'A');

        final var startTime = System.currentTimeMillis();

        assertDoesNotThrow(() -> nullStream.write(data));

        final var endTime = System.currentTimeMillis();

        // Should complete quickly since it's a no-op
        assertTrue((endTime - startTime) < 1000, "Write operation should be fast");
    }

    @Test
    public void testMultipleFlushOperations() {

        final var nullStream = new NullOutputStream();

        assertDoesNotThrow(() -> {
            nullStream.flush();
            nullStream.flush();
            nullStream.flush();
        });
    }

    @Test
    public void testMultipleCloseOperations() {

        final var nullStream = new NullOutputStream();

        assertDoesNotThrow(() -> {
            nullStream.close();
            nullStream.close();
            nullStream.close();
        });
    }

    @Test
    public void testMultipleInstancesIndependent() {

        final var nullStream1 = new NullOutputStream();
        final var nullStream2 = new NullOutputStream();

        assertDoesNotThrow(() -> {
            nullStream1.write('A');
            nullStream2.write('B');

            nullStream1.flush();
            nullStream2.flush();

            nullStream1.close();
            nullStream2.close();
        });
    }

    @Test
    public void testStreamBehaviorMatchesDocumentation() {

        final var nullStream = new NullOutputStream();

        // According to the documentation, it "performs no action but simply consumes any write
        // requests"
        // This means all write operations should succeed without throwing exceptions
        assertDoesNotThrow(() -> {
            nullStream.write(65); // 'A'
            nullStream.write("Hello".getBytes(StandardCharsets.UTF_8));
            nullStream.write("World".getBytes(StandardCharsets.UTF_8), 0, 5);
            nullStream.flush();
            nullStream.close();
        });
    }

    @Test
    public void testWriteOperationsHaveNoSideEffects() throws IOException {

        final var nullStream = new NullOutputStream();

        // These operations should have no observable side effects
        nullStream.write('A');
        nullStream.write("Hello".getBytes(StandardCharsets.UTF_8));
        nullStream.flush();

        // No way to verify the data was "consumed" since it's a null stream
        // The test passes if no exceptions are thrown
        assertTrue(true);
    }

    @Test
    public void testWriteWithEdgeCaseOffsets() {

        final var nullStream = new NullOutputStream();
        final var buffer = new byte[10];

        // Edge case: write from the last position
        assertDoesNotThrow(() -> nullStream.write(buffer, 9, 1));

        // Edge case: write from the first position
        assertDoesNotThrow(() -> nullStream.write(buffer, 0, 1));
    }

    @Test
    public void testWriteBufferBoundaryConditions() {

        final var nullStream = new NullOutputStream();
        final var buffer = new byte[1];

        // Single byte buffer
        assertDoesNotThrow(() -> nullStream.write(buffer));
        assertDoesNotThrow(() -> nullStream.write(buffer, 0, 1));
    }

    @Test
    public void testConcurrentWrites() throws InterruptedException {

        final var nullStream = new NullOutputStream();
        final var threads = new Thread[10];
        final var exceptions = new Exception[10];

        for (var i = 0; i < threads.length; i++) {
            final var index = i;
            threads[i] = new Thread(() -> {
                try {
                    for (var j = 0; j < 1000; j++) {
                        nullStream.write(j);
                    }
                } catch (final Exception e) {
                    exceptions[index] = e;
                }
            });
        }

        for (final var thread : threads) {
            thread.start();
        }

        for (final var thread : threads) {
            thread.join();
        }

        // No exceptions should occur
        for (final var exception : exceptions) {
            assertNull(exception);
        }
    }
}
