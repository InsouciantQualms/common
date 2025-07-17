/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.pipe;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests for StringPipe covering string operations with readers and writers.
 */
public final class StringPipeTest {

    @Test
    public void testReadFromReader() {

        final var pipe = new StringPipe();
        final var testData = "Hello, World!";
        final var reader = new StringReader(testData);

        final var result = pipe.read(reader);

        assertEquals(testData, result);
    }

    @Test
    public void testReadFromEmptyReader() {

        final var pipe = new StringPipe();
        final var reader = new StringReader("");

        final var result = pipe.read(reader);

        assertEquals("", result);
    }

    @Test
    public void testWriteToWriter() {

        final var pipe = new StringPipe();
        final var testData = "Hello, World!";
        final var writer = new StringWriter();

        pipe.write(testData, writer);

        assertEquals(testData, writer.toString());
    }

    @Test
    public void testWriteEmptyString() {

        final var pipe = new StringPipe();
        final var testData = "";
        final var writer = new StringWriter();

        pipe.write(testData, writer);

        assertEquals("", writer.toString());
    }

    @Test
    public void testGoWithDefaultBufferSize() {

        final var pipe = new StringPipe();
        final var testData = "Hello, World!";
        final var reader = new StringReader(testData);
        final var writer = new StringWriter();

        final var charsProcessed = pipe.go(reader, writer);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testGoWithCustomBufferSize() {

        final var pipe = new StringPipe();
        final var testData = "Hello, World!";
        final var reader = new StringReader(testData);
        final var writer = new StringWriter();

        final var charsProcessed = pipe.go(reader, writer, 4);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testGoWithLargeData() {

        final var pipe = new StringPipe();
        final var testData = "A".repeat(10000);
        final var reader = new StringReader(testData);
        final var writer = new StringWriter();

        final var charsProcessed = pipe.go(reader, writer, 512);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testGoWithEmptyString() {

        final var pipe = new StringPipe();
        final var reader = new StringReader("");
        final var writer = new StringWriter();

        final var charsProcessed = pipe.go(reader, writer);

        assertEquals(0, charsProcessed);
        assertEquals("", writer.toString());
    }

    @Test
    public void testGoWithSmallBufferSize() {

        final var pipe = new StringPipe();
        final var testData = "Hello, World!";
        final var reader = new StringReader(testData);
        final var writer = new StringWriter();

        final var charsProcessed = pipe.go(reader, writer, 1);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testGoWithMultilineString() {

        final var pipe = new StringPipe();
        final var testData = "Line 1\nLine 2\nLine 3";
        final var reader = new StringReader(testData);
        final var writer = new StringWriter();

        final var charsProcessed = pipe.go(reader, writer);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testGoWithUnicodeCharacters() {

        final var pipe = new StringPipe();
        final var testData = "Hello 世界 🌍";
        final var reader = new StringReader(testData);
        final var writer = new StringWriter();

        final var charsProcessed = pipe.go(reader, writer);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testStreamNotClosed() {

        final var pipe = new StringPipe();
        final var testData = "Hello, World!";
        final var reader = new TestReader(testData);
        final var writer = new TestWriter();

        pipe.go(reader, writer);

        assertFalse(reader.wasClosed());
        assertFalse(writer.wasClosed());
    }

    @Test
    public void testReadAndWriteRoundTrip() {

        final var pipe = new StringPipe();
        final var testData = "Round trip test data";
        final var reader = new StringReader(testData);

        final var readResult = pipe.read(reader);

        assertEquals(testData, readResult);

        final var writer = new StringWriter();
        pipe.write(readResult, writer);

        assertEquals(testData, writer.toString());
    }

    @Test
    public void testGoWithSpecialCharacters() {

        final var pipe = new StringPipe();
        final var testData = "Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        final var reader = new StringReader(testData);
        final var writer = new StringWriter();

        final var charsProcessed = pipe.go(reader, writer);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    private static final class TestReader extends StringReader {
        private boolean closed = false;

        public TestReader(final String s) {
            super(s);
        }

        @Override
        public void close() {
            closed = true;
            super.close();
        }

        public boolean wasClosed() {
            return closed;
        }
    }

    private static final class TestWriter extends StringWriter {
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