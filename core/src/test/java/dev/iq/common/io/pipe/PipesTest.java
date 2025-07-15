/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.pipe;

import org.junit.jupiter.api.Test;

import dev.iq.common.fp.Fn0;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Pipes factory class covering all factory methods.
 */
public final class PipesTest {

    @Test
    public void testBytesFactory() {

        final var pipe = Pipes.bytes();
        
        assertNotNull(pipe);
        assertInstanceOf(BytesPipe.class, pipe);
    }

    @Test
    public void testBytesFactoryFunctionality() {

        final var pipe = Pipes.bytes();
        final var testData = "Hello, World!".getBytes();
        final var inputStream = new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();
        
        final var bytesProcessed = pipe.go(inputStream, outputStream);
        
        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testBytesSupplierFactory() {

        final var pipe = Pipes.bytesSupplier();
        
        assertNotNull(pipe);
        assertInstanceOf(BytesSupplierPipe.class, pipe);
    }

    @Test
    public void testBytesSupplierFactoryFunctionality() {

        final var pipe = Pipes.bytesSupplier();
        final var testData = "Hello, World!".getBytes();
        final Fn0<ByteArrayInputStream> inputStreamSupplier = () -> new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();
        final Fn0<ByteArrayOutputStream> outputStreamSupplier = () -> outputStream;
        
        final var bytesProcessed = pipe.go(inputStreamSupplier, outputStreamSupplier);
        
        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testCharsFactory() {

        final var pipe = Pipes.chars();
        
        assertNotNull(pipe);
        assertInstanceOf(StringPipe.class, pipe);
    }

    @Test
    public void testCharsFactoryFunctionality() {

        final var pipe = Pipes.chars();
        final var testData = "Hello, World!";
        final var reader = new StringReader(testData);
        final var writer = new StringWriter();
        
        final var charsProcessed = pipe.go(reader, writer);
        
        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testCharsSupplierFactory() {

        final var pipe = Pipes.charsSupplier();
        
        assertNotNull(pipe);
        assertInstanceOf(StringSupplierPipe.class, pipe);
    }

    @Test
    public void testCharsSupplierFactoryFunctionality() {

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
    public void testReverseFactory() {

        final var pipe = Pipes.reverse();
        
        assertNotNull(pipe);
        assertInstanceOf(ReversePipe.class, pipe);
    }

    @Test
    public void testReverseFactoryFunctionality() {

        final var pipe = Pipes.reverse();
        final var testData = "Hello, World!";
        
        final var result = pipe.read(outputStream -> {
            try {
                outputStream.write(testData.getBytes());
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        });
        
        assertArrayEquals(testData.getBytes(), result);
    }

    @Test
    public void testMultiplePipeInstances() {

        final var pipe1 = Pipes.bytes();
        final var pipe2 = Pipes.bytes();
        
        assertNotNull(pipe1);
        assertNotNull(pipe2);
        assertNotSame(pipe1, pipe2);
    }

    @Test
    public void testMultipleSupplierPipeInstances() {

        final var pipe1 = Pipes.bytesSupplier();
        final var pipe2 = Pipes.bytesSupplier();
        
        assertNotNull(pipe1);
        assertNotNull(pipe2);
        assertNotSame(pipe1, pipe2);
    }

    @Test
    public void testMultipleCharsPipeInstances() {

        final var pipe1 = Pipes.chars();
        final var pipe2 = Pipes.chars();
        
        assertNotNull(pipe1);
        assertNotNull(pipe2);
        assertNotSame(pipe1, pipe2);
    }

    @Test
    public void testMultipleCharsSupplierPipeInstances() {

        final var pipe1 = Pipes.charsSupplier();
        final var pipe2 = Pipes.charsSupplier();
        
        assertNotNull(pipe1);
        assertNotNull(pipe2);
        assertNotSame(pipe1, pipe2);
    }

    @Test
    public void testMultipleReversePipeInstances() {

        final var pipe1 = Pipes.reverse();
        final var pipe2 = Pipes.reverse();
        
        assertNotNull(pipe1);
        assertNotNull(pipe2);
        assertNotSame(pipe1, pipe2);
    }

    @Test
    public void testFactoryMethodsReturnCorrectTypes() {

        final var bytesPipe = Pipes.bytes();
        final var bytesSupplierPipe = Pipes.bytesSupplier();
        final var charsPipe = Pipes.chars();
        final var charsSupplierPipe = Pipes.charsSupplier();
        final var reversePipe = Pipes.reverse();
        
        assertTrue(bytesPipe instanceof Pipe);
        assertTrue(bytesSupplierPipe instanceof Pipe);
        assertTrue(charsPipe instanceof Pipe);
        assertTrue(charsSupplierPipe instanceof Pipe);
        assertTrue(reversePipe instanceof Pipe);
    }
}