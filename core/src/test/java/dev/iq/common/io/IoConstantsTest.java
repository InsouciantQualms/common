/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests for the IoConstants class covering IO constants functionality. */
public final class IoConstantsTest {

    @Test
    public void testDefaultBufferLength() {

        assertEquals(1024, IoConstants.DEFAULT_BUFFER_LENGTH);
    }

    @Test
    public void testDefaultLobBufferLength() {

        assertEquals(1024 * 1024 * 64, IoConstants.DEFAULT_LOB_BUFFER_LENGTH);
        assertEquals(67108864, IoConstants.DEFAULT_LOB_BUFFER_LENGTH);
    }

    @Test
    public void testIntMask() {

        assertEquals(0xff, IoConstants.INT_MASK);
        assertEquals(255, IoConstants.INT_MASK);
    }

    @Test
    public void testZeroByte() {

        assertEquals(0, IoConstants.ZERO_BYTE);
        assertEquals((byte) 0, IoConstants.ZERO_BYTE);
    }

    @Test
    public void testEmptyByteArray() {

        assertNotNull(IoConstants.EMPTY_BYTE_ARRAY);
        assertEquals(0, IoConstants.EMPTY_BYTE_ARRAY.length);
        assertInstanceOf(byte[].class, IoConstants.EMPTY_BYTE_ARRAY);
    }

    @Test
    public void testEmptyByteArrayImmutability() {

        final var array1 = IoConstants.EMPTY_BYTE_ARRAY;
        final var array2 = IoConstants.EMPTY_BYTE_ARRAY;

        assertSame(array1, array2);
    }

    @Test
    public void testConstantsArePublicStatic() {

        final var defaultBuffer = IoConstants.DEFAULT_BUFFER_LENGTH;
        final var defaultLobBuffer = IoConstants.DEFAULT_LOB_BUFFER_LENGTH;
        final var intMask = IoConstants.INT_MASK;
        final var zeroByte = IoConstants.ZERO_BYTE;
        final var emptyArray = IoConstants.EMPTY_BYTE_ARRAY;

        assertEquals(1024, defaultBuffer);
        assertEquals(67108864, defaultLobBuffer);
        assertEquals(255, intMask);
        assertEquals(0, zeroByte);
        assertEquals(0, emptyArray.length);
    }

    @Test
    public void testBufferSizeRelationships() {

        assertTrue(IoConstants.DEFAULT_LOB_BUFFER_LENGTH > IoConstants.DEFAULT_BUFFER_LENGTH);
        assertEquals(IoConstants.DEFAULT_BUFFER_LENGTH * 64 * 1024, IoConstants.DEFAULT_LOB_BUFFER_LENGTH);
    }

    @Test
    public void testIntMaskUsage() {

        final var testByte = (byte) 0xFF;
        final var masked = testByte & IoConstants.INT_MASK;

        assertEquals(255, masked);
    }

    @Test
    public void testZeroByteUsage() {

        final var testArray = new byte[5];
        for (var i = 0; i < testArray.length; i++) {
            testArray[i] = IoConstants.ZERO_BYTE;
        }

        for (final var b : testArray) {
            assertEquals(0, b);
        }
    }

    @Test
    public void testEmptyByteArrayUsage() {

        final var result = IoConstants.EMPTY_BYTE_ARRAY;

        assertNotNull(result);
        assertEquals(0, result.length);
        assertArrayEquals(new byte[0], result);
    }

    @Test
    public void testConstantValues() {

        assertEquals(1024, IoConstants.DEFAULT_BUFFER_LENGTH);
        assertEquals(67108864, IoConstants.DEFAULT_LOB_BUFFER_LENGTH);
        assertEquals(255, IoConstants.INT_MASK);
        assertEquals(0, IoConstants.ZERO_BYTE);
        assertArrayEquals(new byte[0], IoConstants.EMPTY_BYTE_ARRAY);
    }

    @Test
    public void testBufferSizesPowerOfTwo() {

        assertTrue(isPowerOfTwo(IoConstants.DEFAULT_BUFFER_LENGTH));
        assertTrue(isPowerOfTwo(IoConstants.DEFAULT_LOB_BUFFER_LENGTH));
    }

    @Test
    public void testIntMaskBinaryRepresentation() {

        assertEquals(0xFF, IoConstants.INT_MASK);
        assertEquals(0b11111111, IoConstants.INT_MASK);
    }

    @Test
    public void testZeroByteEquality() {

        final var zero = (byte) 0;
        assertEquals(zero, IoConstants.ZERO_BYTE);
    }

    @Test
    public void testEmptyByteArrayEquality() {

        final var empty = new byte[0];
        assertArrayEquals(empty, IoConstants.EMPTY_BYTE_ARRAY);
    }

    @Test
    public void testConstantImmutability() {

        final var originalLength = IoConstants.EMPTY_BYTE_ARRAY.length;

        assertEquals(0, originalLength);
        assertEquals(0, IoConstants.EMPTY_BYTE_ARRAY.length);
    }

    private static boolean isPowerOfTwo(final int n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }
}
