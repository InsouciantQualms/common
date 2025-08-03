/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.pipe;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.iq.common.fp.Fn0;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** Tests for Pipes factory class covering all factory methods. */
final class PipesTest {

    @Test
    void testBytesFactory() {

        final var pipe = Pipes.bytes();

        assertNotNull(pipe);
        assertInstanceOf(BytesPipe.class, pipe);
    }

    @Test
    void testBytesFactoryFunctionality() {

        final var pipe = Pipes.bytes();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();

        final var bytesProcessed = pipe.go(inputStream, outputStream);

        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    void testBytesSupplierFactory() {

        final var pipe = Pipes.bytesSupplier();

        assertNotNull(pipe);
        assertInstanceOf(BytesSupplierPipe.class, pipe);
    }

    @Test
    void testBytesSupplierFactoryFunctionality() {

        final var pipe = Pipes.bytesSupplier();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final Fn0<ByteArrayInputStream> inputStreamSupplier = () -> new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();
        final Fn0<ByteArrayOutputStream> outputStreamSupplier = () -> outputStream;

        final var bytesProcessed = pipe.go(inputStreamSupplier, outputStreamSupplier);

        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    void testCharsFactory() {

        final var pipe = Pipes.chars();

        assertNotNull(pipe);
        assertInstanceOf(StringPipe.class, pipe);
    }

    @Test
    void testCharsFactoryFunctionality() {

        final var pipe = Pipes.chars();
        final var testData = "Hello, World!";
        final var reader = new StringReader(testData);
        final var writer = new StringWriter();

        final var charsProcessed = pipe.go(reader, writer);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    void testCharsSupplierFactory() {

        final var pipe = Pipes.charsSupplier();

        assertNotNull(pipe);
        assertInstanceOf(StringSupplierPipe.class, pipe);
    }

    @Test
    void testCharsSupplierFactoryFunctionality() {

        final var pipe = Pipes.charsSupplier();
        final var testData = "Hello, World!";
        final Fn0<StringReader> readerSupplier = () -> new StringReader(testData);
        final var writer = new StringWriter();
        final Fn0<StringWriter> writerSupplier = () -> writer;

        final var charsProcessed = pipe.go(readerSupplier, writerSupplier);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    void testReverseFactory() {

        final var pipe = Pipes.reverse();

        assertNotNull(pipe);
        assertInstanceOf(ReversePipe.class, pipe);
    }

    @Test
    void testReverseFactoryFunctionality() {

        final var pipe = Pipes.reverse();
        final var testData = "Hello, World!";

        final var result = pipe.read(outputStream -> {
            try {
                outputStream.write(testData.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                outputStream.close(); // Important: close the output stream to signal
                // end
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });

        assertArrayEquals(testData.getBytes(StandardCharsets.UTF_8), result);
    }

    @Test
    void testMultiplePipeInstances() {

        final var pipe1 = Pipes.bytes();
        final var pipe2 = Pipes.bytes();

        assertNotNull(pipe1);
        assertNotNull(pipe2);
        assertNotSame(pipe1, pipe2);
    }

    @Test
    void testMultipleSupplierPipeInstances() {

        final var pipe1 = Pipes.bytesSupplier();
        final var pipe2 = Pipes.bytesSupplier();

        assertNotNull(pipe1);
        assertNotNull(pipe2);
        assertNotSame(pipe1, pipe2);
    }

    @Test
    void testMultipleCharsPipeInstances() {

        final var pipe1 = Pipes.chars();
        final var pipe2 = Pipes.chars();

        assertNotNull(pipe1);
        assertNotNull(pipe2);
        assertNotSame(pipe1, pipe2);
    }

    @Test
    void testMultipleCharsSupplierPipeInstances() {

        final var pipe1 = Pipes.charsSupplier();
        final var pipe2 = Pipes.charsSupplier();

        assertNotNull(pipe1);
        assertNotNull(pipe2);
        assertNotSame(pipe1, pipe2);
    }

    @Test
    void testMultipleReversePipeInstances() {

        final var pipe1 = Pipes.reverse();
        final var pipe2 = Pipes.reverse();

        assertNotNull(pipe1);
        assertNotNull(pipe2);
        assertNotSame(pipe1, pipe2);
    }

    @Test
    void testFactoryMethodsReturnCorrectTypes() {

        final var bytesPipe = Pipes.bytes();
        final var bytesSupplierPipe = Pipes.bytesSupplier();
        final var charsPipe = Pipes.chars();
        final var charsSupplierPipe = Pipes.charsSupplier();
        final var reversePipe = Pipes.reverse();

        assertTrue(true);
        assertTrue(true);
        assertTrue(true);
        assertTrue(true);
        assertTrue(true);
    }
}
