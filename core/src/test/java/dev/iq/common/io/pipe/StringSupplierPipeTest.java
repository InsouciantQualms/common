/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.pipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.iq.common.fp.Fn0;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.junit.jupiter.api.Test;

/** Tests for StringSupplierPipe covering lazy evaluation of reader and writer suppliers. */
public final class StringSupplierPipeTest {

    @Test
    public void testReadFromSupplierReader() {

        final var pipe = new StringSupplierPipe();
        final var testData = "Hello, World!";
        final Fn0<StringReader> readerSupplier = () -> new StringReader(testData);

        final var result = pipe.read(readerSupplier);

        assertEquals(testData, result);
    }

    @Test
    public void testReadFromEmptySupplierReader() {

        final var pipe = new StringSupplierPipe();
        final Fn0<StringReader> readerSupplier = () -> new StringReader("");

        final var result = pipe.read(readerSupplier);

        assertEquals("", result);
    }

    @Test
    public void testWriteToSupplierWriter() {

        final var pipe = new StringSupplierPipe();
        final var testData = "Hello, World!";
        final var writer = new StringWriter();
        final Fn0<StringWriter> writerSupplier = () -> writer;

        pipe.write(testData, writerSupplier);

        assertEquals(testData, writer.toString());
    }

    @Test
    public void testWriteEmptyStringToSupplier() {

        final var pipe = new StringSupplierPipe();
        final var testData = "";
        final var writer = new StringWriter();
        final Fn0<StringWriter> writerSupplier = () -> writer;

        pipe.write(testData, writerSupplier);

        assertEquals("", writer.toString());
    }

    @Test
    public void testGoWithSuppliers() {

        final var pipe = new StringSupplierPipe();
        final var testData = "Hello, World!";
        final Fn0<StringReader> readerSupplier = () -> new StringReader(testData);
        final var writer = new StringWriter();
        final Fn0<StringWriter> writerSupplier = () -> writer;

        final var charsProcessed = pipe.go(readerSupplier, writerSupplier);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testGoWithSuppliersAndCustomBufferSize() {

        final var pipe = new StringSupplierPipe();
        final var testData = "Hello, World!";
        final Fn0<StringReader> readerSupplier = () -> new StringReader(testData);
        final var writer = new StringWriter();
        final Fn0<StringWriter> writerSupplier = () -> writer;

        final var charsProcessed = pipe.go(readerSupplier, writerSupplier, 4);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testGoWithLargeDataAndSuppliers() {

        final var pipe = new StringSupplierPipe();
        final var testData = "A".repeat(10000);
        final Fn0<StringReader> readerSupplier = () -> new StringReader(testData);
        final var writer = new StringWriter();
        final Fn0<StringWriter> writerSupplier = () -> writer;

        final var charsProcessed = pipe.go(readerSupplier, writerSupplier, 512);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testGoWithEmptyStringSuppliers() {

        final var pipe = new StringSupplierPipe();
        final Fn0<StringReader> readerSupplier = () -> new StringReader("");
        final var writer = new StringWriter();
        final Fn0<StringWriter> writerSupplier = () -> writer;

        final var charsProcessed = pipe.go(readerSupplier, writerSupplier);

        assertEquals(0, charsProcessed);
        assertEquals("", writer.toString());
    }

    @Test
    public void testStreamClosedBySupplier() {

        final var pipe = new StringSupplierPipe();
        final var testData = "Hello, World!";
        final var reader = new TestReader(testData);
        final var writer = new TestWriter();
        final Fn0<TestReader> readerSupplier = () -> reader;
        final Fn0<StringWriter> writerSupplier = () -> writer;

        pipe.go(readerSupplier, writerSupplier);

        assertTrue(reader.wasClosed());
        assertTrue(writer.wasClosed());
    }

    @Test
    public void testLazyEvaluationOfReaderSupplier() {

        final var pipe = new StringSupplierPipe();
        final var testData = "Hello, World!";
        final var callCounter = new int[1];
        final Fn0<StringReader> readerSupplier = () -> {
            callCounter[0]++;
            return new StringReader(testData);
        };

        // Supplier should not be called until read is invoked
        assertEquals(0, callCounter[0]);

        final var result = pipe.read(readerSupplier);

        assertEquals(1, callCounter[0]);
        assertEquals(testData, result);
    }

    @Test
    public void testLazyEvaluationOfWriterSupplier() {

        final var pipe = new StringSupplierPipe();
        final var testData = "Hello, World!";
        final var callCounter = new int[1];
        final var writer = new StringWriter();
        final Fn0<StringWriter> writerSupplier = () -> {
            callCounter[0]++;
            return writer;
        };

        // Supplier should not be called until write is invoked
        assertEquals(0, callCounter[0]);

        pipe.write(testData, writerSupplier);

        assertEquals(1, callCounter[0]);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testGoWithMultilineStringSuppliers() {

        final var pipe = new StringSupplierPipe();
        final var testData = "Line 1\nLine 2\nLine 3";
        final Fn0<StringReader> readerSupplier = () -> new StringReader(testData);
        final var writer = new StringWriter();
        final Fn0<StringWriter> writerSupplier = () -> writer;

        final var charsProcessed = pipe.go(readerSupplier, writerSupplier);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testGoWithUnicodeCharactersSuppliers() {

        final var pipe = new StringSupplierPipe();
        final var testData = "Hello 世界 🌍";
        final Fn0<StringReader> readerSupplier = () -> new StringReader(testData);
        final var writer = new StringWriter();
        final Fn0<StringWriter> writerSupplier = () -> writer;

        final var charsProcessed = pipe.go(readerSupplier, writerSupplier);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    @Test
    public void testReadAndWriteRoundTripWithSuppliers() {

        final var pipe = new StringSupplierPipe();
        final var testData = "Round trip test data";
        final Fn0<StringReader> readerSupplier = () -> new StringReader(testData);

        final var readResult = pipe.read(readerSupplier);

        assertEquals(testData, readResult);

        final var writer = new StringWriter();
        final Fn0<StringWriter> writerSupplier = () -> writer;
        pipe.write(readResult, writerSupplier);

        assertEquals(testData, writer.toString());
    }

    @Test
    public void testGoWithSpecialCharactersSuppliers() {

        final var pipe = new StringSupplierPipe();
        final var testData = "Special chars: !@#$%^&*()_+-=[]{}|;':\",./<>?";
        final Fn0<StringReader> readerSupplier = () -> new StringReader(testData);
        final var writer = new StringWriter();
        final Fn0<StringWriter> writerSupplier = () -> writer;

        final var charsProcessed = pipe.go(readerSupplier, writerSupplier);

        assertEquals(testData.length(), charsProcessed);
        assertEquals(testData, writer.toString());
    }

    private static final class TestReader extends StringReader {
        private boolean closed = false;

        TestReader(final String s) {
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
