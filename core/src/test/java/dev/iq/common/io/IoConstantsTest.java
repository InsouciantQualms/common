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
final class IoConstantsTest {

    @Test
    void testDefaultBufferLength() {

        assertEquals(1024, IoConstants.DEFAULT_BUFFER_LENGTH);
    }

    @Test
    void testDefaultLobBufferLength() {

        assertEquals(1024 * 1024 * 64, IoConstants.DEFAULT_LOB_BUFFER_LENGTH);
        assertEquals(67108864, IoConstants.DEFAULT_LOB_BUFFER_LENGTH);
    }

    @Test
    void testIntMask() {

        assertEquals(0xff, IoConstants.INT_MASK);
        assertEquals(255, IoConstants.INT_MASK);
    }

    @Test
    void testZeroByte() {

        assertEquals(0, IoConstants.ZERO_BYTE);
        assertEquals((byte) 0, IoConstants.ZERO_BYTE);
    }

    @Test
    void testEmptyByteArray() {

        assertNotNull(IoConstants.EMPTY_BYTE_ARRAY);
        assertEquals(0, IoConstants.EMPTY_BYTE_ARRAY.length);
        assertInstanceOf(byte[].class, IoConstants.EMPTY_BYTE_ARRAY);
    }

    @Test
    void testEmptyByteArrayImmutability() {

        final var array1 = IoConstants.EMPTY_BYTE_ARRAY;
        final var array2 = IoConstants.EMPTY_BYTE_ARRAY;

        assertSame(array1, array2);
    }

    @Test
    void testConstantsArePublicStatic() {

        final var defaultBuffer = IoConstants.DEFAULT_BUFFER_LENGTH;

        assertEquals(1024, defaultBuffer);
        final var defaultLobBuffer = IoConstants.DEFAULT_LOB_BUFFER_LENGTH;
        assertEquals(67108864, defaultLobBuffer);
        final var intMask = IoConstants.INT_MASK;
        assertEquals(255, intMask);
        final var zeroByte = IoConstants.ZERO_BYTE;
        assertEquals(0, zeroByte);
        final var emptyArray = IoConstants.EMPTY_BYTE_ARRAY;
        assertEquals(0, emptyArray.length);
    }

    @Test
    @SuppressWarnings({"ConstantValue", "ConstantAssertArgument"})
    void testBufferSizeRelationships() {

        assertTrue(IoConstants.DEFAULT_LOB_BUFFER_LENGTH > IoConstants.DEFAULT_BUFFER_LENGTH);
        assertEquals(IoConstants.DEFAULT_BUFFER_LENGTH * 64 * 1024, IoConstants.DEFAULT_LOB_BUFFER_LENGTH);
    }

    @Test
    void testIntMaskUsage() {

        final var testByte = (byte) 0xFF;
        final var masked = testByte & IoConstants.INT_MASK;

        assertEquals(255, masked);
    }

    @Test
    void testConstantValues() {

        assertEquals(1024, IoConstants.DEFAULT_BUFFER_LENGTH);
        assertEquals(67108864, IoConstants.DEFAULT_LOB_BUFFER_LENGTH);
        assertEquals(255, IoConstants.INT_MASK);
        assertEquals(0, IoConstants.ZERO_BYTE);
        assertArrayEquals(new byte[0], IoConstants.EMPTY_BYTE_ARRAY);
    }

    @Test
    void testBufferSizesPowerOfTwo() {

        assertTrue(isPowerOfTwo(IoConstants.DEFAULT_BUFFER_LENGTH));
        assertTrue(isPowerOfTwo(IoConstants.DEFAULT_LOB_BUFFER_LENGTH));
    }

    @Test
    void testIntMaskBinaryRepresentation() {

        assertEquals(0xFF, IoConstants.INT_MASK);
        assertEquals(0b11111111, IoConstants.INT_MASK);
    }

    @Test
    void testZeroByteEquality() {

        final var zero = (byte) 0;
        assertEquals(zero, IoConstants.ZERO_BYTE);
    }

    @Test
    void testConstantImmutability() {

        final var originalLength = IoConstants.EMPTY_BYTE_ARRAY.length;

        assertEquals(0, originalLength);
        assertEquals(0, IoConstants.EMPTY_BYTE_ARRAY.length);
    }

    private static boolean isPowerOfTwo(final int n) {
        return (n > 0) && ((n & (n - 1)) == 0);
    }
}
