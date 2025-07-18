/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.pipe;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import dev.iq.common.fp.Fn0;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

/** Tests for BytesSupplierPipe covering lazy evaluation of stream suppliers. */
public final class BytesSupplierPipeTest {

    @Test
    public void testReadFromSupplierInputStream() {

        final var pipe = new BytesSupplierPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final Fn0<ByteArrayInputStream> inputStreamSupplier = () -> new ByteArrayInputStream(testData);

        final var result = pipe.read(inputStreamSupplier);

        assertArrayEquals(testData, result);
    }

    @Test
    public void testReadFromEmptySupplierInputStream() {

        final var pipe = new BytesSupplierPipe();
        final Fn0<ByteArrayInputStream> inputStreamSupplier = () -> new ByteArrayInputStream(new byte[0]);

        final var result = pipe.read(inputStreamSupplier);

        assertEquals(0, result.length);
    }

    @Test
    public void testWriteToSupplierOutputStream() {

        final var pipe = new BytesSupplierPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var outputStream = new ByteArrayOutputStream();
        final Fn0<ByteArrayOutputStream> outputStreamSupplier = () -> outputStream;

        pipe.write(testData, outputStreamSupplier);

        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testWriteEmptyByteArrayToSupplier() {

        final var pipe = new BytesSupplierPipe();
        final var testData = new byte[0];
        final var outputStream = new ByteArrayOutputStream();
        final Fn0<ByteArrayOutputStream> outputStreamSupplier = () -> outputStream;

        pipe.write(testData, outputStreamSupplier);

        assertEquals(0, outputStream.toByteArray().length);
    }

    @Test
    public void testGoWithSuppliers() {

        final var pipe = new BytesSupplierPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final Fn0<ByteArrayInputStream> inputStreamSupplier = () -> new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();
        final Fn0<ByteArrayOutputStream> outputStreamSupplier = () -> outputStream;

        final var bytesProcessed = pipe.go(inputStreamSupplier, outputStreamSupplier);

        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testGoWithSuppliersAndCustomBufferSize() {

        final var pipe = new BytesSupplierPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final Fn0<ByteArrayInputStream> inputStreamSupplier = () -> new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();
        final Fn0<ByteArrayOutputStream> outputStreamSupplier = () -> outputStream;

        final var bytesProcessed = pipe.go(inputStreamSupplier, outputStreamSupplier, 4);

        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testGoWithLargeDataAndSuppliers() {

        final var pipe = new BytesSupplierPipe();
        final var testData = new byte[10000];
        for (var i = 0; i < testData.length; i++) {
            testData[i] = (byte) (i % 256);
        }
        final Fn0<ByteArrayInputStream> inputStreamSupplier = () -> new ByteArrayInputStream(testData);
        final var outputStream = new ByteArrayOutputStream();
        final Fn0<ByteArrayOutputStream> outputStreamSupplier = () -> outputStream;

        final var bytesProcessed = pipe.go(inputStreamSupplier, outputStreamSupplier, 512);

        assertEquals(testData.length, bytesProcessed);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    @Test
    public void testGoWithEmptyStreamSuppliers() {

        final var pipe = new BytesSupplierPipe();
        final Fn0<ByteArrayInputStream> inputStreamSupplier = () -> new ByteArrayInputStream(new byte[0]);
        final var outputStream = new ByteArrayOutputStream();
        final Fn0<ByteArrayOutputStream> outputStreamSupplier = () -> outputStream;

        final var bytesProcessed = pipe.go(inputStreamSupplier, outputStreamSupplier);

        assertEquals(0, bytesProcessed);
        assertEquals(0, outputStream.toByteArray().length);
    }

    @Test
    public void testStreamClosedBySupplier() {

        final var pipe = new BytesSupplierPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new TestInputStream(testData);
        final var outputStream = new TestOutputStream();
        final Fn0<TestInputStream> inputStreamSupplier = () -> inputStream;
        final Fn0<ByteArrayOutputStream> outputStreamSupplier = () -> outputStream;

        pipe.go(inputStreamSupplier, outputStreamSupplier);

        assertTrue(inputStream.wasClosed());
        assertTrue(outputStream.wasClosed());
    }

    @Test
    public void testLazyEvaluationOfInputSupplier() {

        final var pipe = new BytesSupplierPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var callCounter = new int[1];
        final Fn0<ByteArrayInputStream> inputStreamSupplier = () -> {
            callCounter[0]++;
            return new ByteArrayInputStream(testData);
        };

        // Supplier should not be called until read is invoked
        assertEquals(0, callCounter[0]);

        final var result = pipe.read(inputStreamSupplier);

        assertEquals(1, callCounter[0]);
        assertArrayEquals(testData, result);
    }

    @Test
    public void testLazyEvaluationOfOutputSupplier() {

        final var pipe = new BytesSupplierPipe();
        final var testData = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var callCounter = new int[1];
        final var outputStream = new ByteArrayOutputStream();
        final Fn0<ByteArrayOutputStream> outputStreamSupplier = () -> {
            callCounter[0]++;
            return outputStream;
        };

        // Supplier should not be called until write is invoked
        assertEquals(0, callCounter[0]);

        pipe.write(testData, outputStreamSupplier);

        assertEquals(1, callCounter[0]);
        assertArrayEquals(testData, outputStream.toByteArray());
    }

    private static final class TestInputStream extends ByteArrayInputStream {
        private boolean closed = false;

        TestInputStream(final byte[] buf) {
            super(buf);
        }

        @Override
        public void close() throws IOException {
            closed = true;
            super.close();
        }

        public boolean wasClosed() {
            return closed;
        }
    }

    private static final class TestOutputStream extends ByteArrayOutputStream {
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
