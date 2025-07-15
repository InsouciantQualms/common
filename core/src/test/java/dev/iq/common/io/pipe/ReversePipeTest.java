/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.pipe;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ReversePipe covering basic functionality.
 */
public final class ReversePipeTest {

    @Test
    public void testReadWithSimpleOutput() {

        final var pipe = new ReversePipe();
        final var testData = "Hello, World!";
        
        final var result = pipe.read(outputStream -> {
            try {
                outputStream.write(testData.getBytes());
                outputStream.close();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        assertArrayEquals(testData.getBytes(), result);
    }

    @Test
    public void testReadWithEmptyOutput() {

        final var pipe = new ReversePipe();
        
        final var result = pipe.read(outputStream -> {
            try {
                outputStream.close();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        assertEquals(0, result.length);
    }

    @Test
    public void testPipeCreation() {

        final var pipe = new ReversePipe();
        
        assertNotNull(pipe);
    }

    @Test
    public void testReadWithMultipleWrites() {

        final var pipe = new ReversePipe();
        final var testData1 = "First part ";
        final var testData2 = "Second part";
        final var expectedData = testData1 + testData2;
        
        final var result = pipe.read(outputStream -> {
            try {
                outputStream.write(testData1.getBytes());
                outputStream.write(testData2.getBytes());
                outputStream.close();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        assertArrayEquals(expectedData.getBytes(), result);
    }

    @Test
    public void testReadWithLargeData() {

        final var pipe = new ReversePipe();
        final var testData = "x".repeat(1000);
        
        final var result = pipe.read(outputStream -> {
            try {
                outputStream.write(testData.getBytes());
                outputStream.close();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        assertArrayEquals(testData.getBytes(), result);
    }
}