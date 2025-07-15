/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.pipe;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for Pipe interface default methods and behavior.
 */
public final class PipeTest {

    @Test
    public void testDefaultGoMethod() {

        final var pipe = new BytesPipe();
        final var testData = "Hello, World!".getBytes();
        final var inputStream = new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();
        
        final var bytesProcessed = pipe.go(inputStream, outputStream);
        
        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testDefaultGoMethodWithEmptyStream() {

        final var pipe = new BytesPipe();
        final var inputStream = new ByteArrayInputStream(new byte[0]);
        final var outputStream = new ByteArrayOutputStream();
        
        final var bytesProcessed = pipe.go(inputStream, outputStream);
        
        assertEquals(0, bytesProcessed);
        assertEquals(0, outputStream.toByteArray().length);
    }

    @Test
    public void testDefaultGoMethodWithLargeData() {

        final var pipe = new BytesPipe();
        final var testData = new byte[5000];
        for (int i = 0; i < testData.length; i++) {
            testData[i] = (byte) (i % 256);
        }
        final var inputStream = new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();
        
        final var bytesProcessed = pipe.go(inputStream, outputStream);
        
        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testPipeInterfaceWithStringPipe() {

        final Pipe<String, java.io.Reader, java.io.Writer> pipe = new StringPipe();
        final var testData = "Hello, World!";
        final var reader = new java.io.StringReader(testData);
        final var writer = new java.io.StringWriter();
        
        final var charsProcessed = pipe.go(reader, writer);
        
        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testPipeInterfacePolymorphism() {

        final Pipe<byte[], java.io.InputStream, java.io.OutputStream> pipe = new BytesPipe();
        final var testData = "Polymorphism test".getBytes();
        final var inputStream = new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();
        
        final var result = pipe.read(inputStream);
        
        assertArrayEquals(testData, result);
        
        pipe.write(testData, outputStream);
        
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testPipeInterfaceWithCustomBufferSize() {

        final Pipe<byte[], java.io.InputStream, java.io.OutputStream> pipe = new BytesPipe();
        final var testData = "Custom buffer size test".getBytes();
        final var inputStream = new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();
        
        final var bytesProcessed = pipe.go(inputStream, outputStream, 8);
        
        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testPipeInterfaceReadAndWrite() {

        final Pipe<byte[], java.io.InputStream, java.io.OutputStream> pipe = new BytesPipe();
        final var testData = "Read and write test".getBytes();
        final var inputStream = new ByteArrayInputStream(testData);
        
        final var readResult = pipe.read(inputStream);
        
        assertArrayEquals(testData, readResult);
        
        final var outputStream = new ByteArrayOutputStream();
        pipe.write(readResult, outputStream);
        
        assertArrayEquals(testData, outputStream.toByteArray());
    }
}