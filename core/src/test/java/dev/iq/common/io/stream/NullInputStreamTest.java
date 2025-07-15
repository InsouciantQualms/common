/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.stream;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for NullInputStream covering null stream behavior that always returns zero.
 */
public final class NullInputStreamTest {

    @Test
    public void testReadSingleByteReturnsZero() throws IOException {

        final var nullStream = new NullInputStream();
        
        final var result = nullStream.read();
        
        assertEquals(0, result);
    }

    @Test
    public void testReadMultipleSingleBytesReturnsZero() throws IOException {

        final var nullStream = new NullInputStream();
        
        assertEquals(0, nullStream.read());
        assertEquals(0, nullStream.read());
        assertEquals(0, nullStream.read());
    }

    @Test
    public void testReadBufferReturnsDataSize() throws IOException {

        final var nullStream = new NullInputStream();
        final var buffer = new byte[10];
        
        final var result = nullStream.read(buffer);
        
        assertEquals(10, result);
        // Buffer should be filled with zeros
        final var expectedBuffer = new byte[10];
        assertArrayEquals(expectedBuffer, buffer);
    }

    @Test
    public void testReadBufferWithOffsetReturnsRequestedLength() throws IOException {

        final var nullStream = new NullInputStream();
        final var buffer = new byte[10];
        
        final var result = nullStream.read(buffer, 2, 5);
        
        assertEquals(5, result);
        // Only bytes 2-6 should be written to (with zeros)
        final var expectedBuffer = new byte[10];
        assertArrayEquals(expectedBuffer, buffer);
    }

    @Test
    public void testReadBufferModifiesBuffer() throws IOException {

        final var nullStream = new NullInputStream();
        final var buffer = new byte[10];
        java.util.Arrays.fill(buffer, (byte) 'X');
        
        nullStream.read(buffer);
        
        // Buffer should be filled with zeros
        final var expectedBuffer = new byte[10];
        assertArrayEquals(expectedBuffer, buffer);
    }

    @Test
    public void testReadBufferWithOffsetModifiesBuffer() throws IOException {

        final var nullStream = new NullInputStream();
        final var buffer = new byte[10];
        java.util.Arrays.fill(buffer, (byte) 'X');
        
        nullStream.read(buffer, 2, 5);
        
        // Only bytes 2-6 should be modified (set to 0)
        final var expectedBuffer = new byte[10];
        java.util.Arrays.fill(expectedBuffer, (byte) 'X');
        java.util.Arrays.fill(expectedBuffer, 2, 7, (byte) 0);
        assertArrayEquals(expectedBuffer, buffer);
    }

    @Test
    public void testReadEmptyBuffer() throws IOException {

        final var nullStream = new NullInputStream();
        final var buffer = new byte[0];
        
        final var result = nullStream.read(buffer);
        
        assertEquals(0, result);
    }

    @Test
    public void testReadBufferWithZeroLength() throws IOException {

        final var nullStream = new NullInputStream();
        final var buffer = new byte[10];
        
        final var result = nullStream.read(buffer, 0, 0);
        
        assertEquals(0, result);
    }

    @Test
    public void testReadBufferWithLargeBuffer() throws IOException {

        final var nullStream = new NullInputStream();
        final var buffer = new byte[10000];
        
        final var result = nullStream.read(buffer);
        
        assertEquals(10000, result);
        // Buffer should be filled with zeros
        final var expectedBuffer = new byte[10000];
        assertArrayEquals(expectedBuffer, buffer);
    }

    @Test
    public void testAvailableReturnsZero() throws IOException {

        final var nullStream = new NullInputStream();
        
        final var result = nullStream.available();
        
        assertEquals(0, result);
    }

    @Test
    public void testMarkSupported() {

        final var nullStream = new NullInputStream();
        
        final var result = nullStream.markSupported();
        
        assertFalse(result);
    }

    @Test
    public void testMarkDoesNotThrow() {

        final var nullStream = new NullInputStream();
        
        assertDoesNotThrow(() -> nullStream.mark(100));
    }

    @Test
    public void testResetThrowsException() {

        final var nullStream = new NullInputStream();
        
        assertThrows(IOException.class, () -> nullStream.reset());
    }

    @Test
    public void testSkipReturnsRequestedAmount() throws IOException {

        final var nullStream = new NullInputStream();
        
        final var result = nullStream.skip(100);
        
        assertEquals(100, result);
    }

    @Test
    public void testCloseDoesNotThrow() {

        final var nullStream = new NullInputStream();
        
        assertDoesNotThrow(() -> nullStream.close());
    }

    @Test
    public void testReadAfterCloseStillReturnsZero() throws IOException {

        final var nullStream = new NullInputStream();
        
        nullStream.close();
        
        final var result = nullStream.read();
        
        assertEquals(0, result);
    }

    @Test
    public void testConsistentBehaviorAcrossOperations() throws IOException {

        final var nullStream = new NullInputStream();
        
        // Single byte read should return 0
        assertEquals(0, nullStream.read());
        // Buffer reads should return buffer size
        assertEquals(10, nullStream.read(new byte[10]));
        assertEquals(5, nullStream.read(new byte[10], 0, 5));
        // Available should return 0 (no data available)
        assertEquals(0, nullStream.available());
        // Skip should return requested amount
        assertEquals(100, nullStream.skip(100));
    }

    @Test
    public void testNeverReturnsEOF() throws IOException {

        final var nullStream = new NullInputStream();
        
        // Should never return -1 (EOF)
        for (int i = 0; i < 1000; i++) {
            assertEquals(0, nullStream.read());
        }
    }

    @Test
    public void testInfiniteReadability() throws IOException {

        final var nullStream = new NullInputStream();
        final var buffer = new byte[1];
        
        // Should be able to read indefinitely
        for (int i = 0; i < 1000; i++) {
            assertEquals(1, nullStream.read(buffer));
        }
    }

    @Test
    public void testNullBufferHandling() {

        final var nullStream = new NullInputStream();
        
        // Should handle null buffer gracefully or throw appropriate exception
        assertThrows(Exception.class, () -> nullStream.read(null));
        assertThrows(Exception.class, () -> nullStream.read(null, 0, 5));
    }

    @Test
    public void testInvalidOffsetAndLength() {

        final var nullStream = new NullInputStream();
        final var buffer = new byte[10];
        
        // Should handle invalid offset/length appropriately
        assertThrows(Exception.class, () -> nullStream.read(buffer, -1, 5));
        assertThrows(Exception.class, () -> nullStream.read(buffer, 0, -1));
        assertThrows(Exception.class, () -> nullStream.read(buffer, 15, 5));
        assertThrows(Exception.class, () -> nullStream.read(buffer, 0, 15));
    }

    @Test
    public void testReadWithValidOffsetAndLength() throws IOException {

        final var nullStream = new NullInputStream();
        final var buffer = new byte[10];
        
        // Valid offset and length combinations
        assertEquals(10, nullStream.read(buffer, 0, 10));
        assertEquals(5, nullStream.read(buffer, 5, 5));
        assertEquals(1, nullStream.read(buffer, 9, 1));
    }

    @Test
    public void testMultipleInstancesIndependent() throws IOException {

        final var nullStream1 = new NullInputStream();
        final var nullStream2 = new NullInputStream();
        
        assertEquals(0, nullStream1.read());
        assertEquals(0, nullStream2.read());
        
        // Both should behave the same
        assertEquals(nullStream1.read(), nullStream2.read());
    }

    @Test
    public void testStreamBehaviorMatchesDocumentation() throws IOException {

        final var nullStream = new NullInputStream();
        
        // According to the documentation, it "reads nothing and always indicates more data is present"
        // Single byte read returns 0 (byte value, not EOF)
        assertEquals(0, nullStream.read());
        // Buffer reads return the amount requested/available
        assertEquals(100, nullStream.read(new byte[100]));
        assertEquals(50, nullStream.read(new byte[100], 0, 50));
    }
}