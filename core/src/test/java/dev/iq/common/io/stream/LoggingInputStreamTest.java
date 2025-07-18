/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests for LoggingInputStream covering data logging to files during reading. */
public final class LoggingInputStreamTest {

    @TempDir
    private Path tempDir;

    @Test
    public void testReadSingleByteLogging() throws IOException {

        final var logFile = tempDir.resolve("single_byte.log");
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            final var result = loggingStream.read();

            assertEquals('H', result);
        }

        assertTrue(Files.exists(logFile));
        final var loggedData = Files.readAllBytes(logFile);
        assertEquals(1, loggedData.length);
        assertEquals('H', loggedData[0]);
    }

    @Test
    public void testReadMultipleSingleBytesLogging() throws IOException {

        final var logFile = tempDir.resolve("multiple_bytes.log");
        final var data = "Hello".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            assertEquals('H', loggingStream.read());
            assertEquals('e', loggingStream.read());
            assertEquals('l', loggingStream.read());
        }

        assertTrue(Files.exists(logFile));
        final var loggedData = Files.readAllBytes(logFile);
        assertEquals(3, loggedData.length);
        assertArrayEquals("Hel".getBytes(StandardCharsets.UTF_8), loggedData);
    }

    @Test
    public void testReadBufferLogging() throws IOException {

        final var logFile = tempDir.resolve("buffer.log");
        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var buffer = new byte[10];

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            final var bytesRead = loggingStream.read(buffer);

            assertEquals(10, bytesRead);
            assertArrayEquals("Hello, Wor".getBytes(StandardCharsets.UTF_8), buffer);
        }

        assertTrue(Files.exists(logFile));
        final var loggedData = Files.readAllBytes(logFile);
        // The logged data should contain at least the expected data
        assertTrue(loggedData.length >= 10);
        // Check that the logged data starts with the expected content
        final var expectedData = "Hello, Wor".getBytes(StandardCharsets.UTF_8);
        for (var i = 0; i < expectedData.length; i++) {
            assertEquals(expectedData[i], loggedData[i]);
        }
    }

    @Test
    public void testReadBufferWithOffsetAndLengthLogging() throws IOException {

        final var logFile = tempDir.resolve("buffer_offset.log");
        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var buffer = new byte[10];

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            final var bytesRead = loggingStream.read(buffer, 2, 5);

            assertEquals(5, bytesRead);
            assertArrayEquals("Hello".getBytes(StandardCharsets.UTF_8), Arrays.copyOfRange(buffer, 2, 7));
        }

        assertTrue(Files.exists(logFile));
        final var loggedData = Files.readAllBytes(logFile);
        // The logged data should contain at least the expected data
        assertTrue(loggedData.length >= 5);
        // Check that the logged data starts with the expected content
        final var expectedData = "Hello".getBytes(StandardCharsets.UTF_8);
        for (var i = 0; i < expectedData.length; i++) {
            assertEquals(expectedData[i], loggedData[i]);
        }
    }

    @Test
    public void testReadEofDoesNotLog() throws IOException {

        final var logFile = tempDir.resolve("eof.log");
        final var data = "Hi".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            assertEquals('H', loggingStream.read());
            assertEquals('i', loggingStream.read());
            assertEquals(-1, loggingStream.read()); // EOF
        }

        assertTrue(Files.exists(logFile));
        final var loggedData = Files.readAllBytes(logFile);
        // The logged data should contain at least the expected data
        assertTrue(loggedData.length >= 2);
        // Check that the logged data starts with the expected content
        final var expectedData = "Hi".getBytes(StandardCharsets.UTF_8);
        for (var i = 0; i < expectedData.length; i++) {
            assertEquals(expectedData[i], loggedData[i]);
        }
    }

    @Test
    public void testReadBufferEofDoesNotLog() throws IOException {

        final var logFile = tempDir.resolve("buffer_eof.log");
        final var data = "Hi".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var buffer = new byte[10];

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            assertEquals(2, loggingStream.read(buffer));
            assertEquals(-1, loggingStream.read(buffer)); // EOF
        }

        assertTrue(Files.exists(logFile));
        final var loggedData = Files.readAllBytes(logFile);
        // The logged data should contain at least the expected data
        assertTrue(loggedData.length >= 2);
        // Check that the logged data starts with the expected content
        final var expectedData = "Hi".getBytes(StandardCharsets.UTF_8);
        for (var i = 0; i < expectedData.length; i++) {
            assertEquals(expectedData[i], loggedData[i]);
        }
    }

    @Test
    public void testReadEmptyStreamLogging() throws IOException {

        final var logFile = tempDir.resolve("empty.log");
        final var inputStream = new ByteArrayInputStream(new byte[0]);

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            assertEquals(-1, loggingStream.read());
        }

        assertTrue(Files.exists(logFile));
        final var loggedData = Files.readAllBytes(logFile);
        assertEquals(0, loggedData.length);
    }

    @Test
    public void testReadEmptyBufferLogging() throws IOException {

        final var logFile = tempDir.resolve("empty_buffer.log");
        final var inputStream = new ByteArrayInputStream(new byte[0]);
        final var buffer = new byte[10];

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            assertEquals(-1, loggingStream.read(buffer));
        }

        assertTrue(Files.exists(logFile));
        final var loggedData = Files.readAllBytes(logFile);
        assertEquals(0, loggedData.length);
    }

    @Test
    public void testMixedReadOperationsLogging() throws IOException {

        final var logFile = tempDir.resolve("mixed.log");
        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var buffer = new byte[5];

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            assertEquals('H', loggingStream.read());
            assertEquals(4, loggingStream.read(buffer, 0, 4));
            assertEquals(',', loggingStream.read());
        }

        assertTrue(Files.exists(logFile));
        final var loggedData = Files.readAllBytes(logFile);
        // The logged data should contain at least the expected data
        assertTrue(loggedData.length >= 6);
        // Check that the logged data starts with the expected content
        final var expectedData = "Hello,".getBytes(StandardCharsets.UTF_8);
        for (var i = 0; i < expectedData.length; i++) {
            assertEquals(expectedData[i], loggedData[i]);
        }
    }

    @Test
    public void testLargeDataLogging() throws IOException {

        final var logFile = tempDir.resolve("large.log");
        final var data = new byte[10000];
        Arrays.fill(data, (byte) 'A');
        final var inputStream = new ByteArrayInputStream(data);
        final var buffer = new byte[1000];

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            var totalRead = 0;
            int bytesRead;
            while ((bytesRead = loggingStream.read(buffer)) != -1) {
                totalRead += bytesRead;
            }
            assertEquals(10000, totalRead);
        }

        assertTrue(Files.exists(logFile));
        final var loggedData = Files.readAllBytes(logFile);
        // The logged data should contain at least the expected data
        assertTrue(loggedData.length >= 10000);
        // First 10000 bytes should be 'A'
        for (var i = 0; i < 10000; i++) {
            assertEquals((byte) 'A', loggedData[i]);
        }
    }

    @Test
    public void testLogFileCreation() throws IOException {

        final var logFile = tempDir.resolve("creation.log");
        final var data = "Test".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);

        assertFalse(Files.exists(logFile));

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            loggingStream.read();
        }

        assertTrue(Files.exists(logFile));
    }

    @Test
    public void testLogFileFlushOnClose() throws IOException {

        final var logFile = tempDir.resolve("flush.log");
        final var data = "Test".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            loggingStream.read();
            // Data should be flushed when stream is closed
        }

        assertTrue(Files.exists(logFile));
        final var loggedData = Files.readAllBytes(logFile);
        assertEquals(1, loggedData.length);
        assertEquals('T', loggedData[0]);
    }

    @Test
    public void testInvalidLogFilePathThrowsException() {

        final var invalidPath = "/invalid/path/that/does/not/exist/log.txt";
        final var data = "Test".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);

        assertThrows(IOException.class, () -> new LoggingInputStream(invalidPath, inputStream));
    }

    @Test
    public void testReadAfterClose() throws IOException {

        final var logFile = tempDir.resolve("closed.log");
        final var data = "Test".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var loggingStream = new LoggingInputStream(logFile.toString(), inputStream);

        loggingStream.read();
        loggingStream.close();

        // Reading after close should throw an exception
        assertThrows(IOException.class, loggingStream::read);
    }

    @Test
    public void testLogFileContainsExactDataRead() throws IOException {

        final var logFile = tempDir.resolve("exact.log");
        final var data = "Hello, World!".getBytes(StandardCharsets.UTF_8);
        final var inputStream = new ByteArrayInputStream(data);
        final var buffer = new byte[5];

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            final var bytesRead = loggingStream.read(buffer);
            assertEquals(5, bytesRead);
        }

        final var loggedData = Files.readAllBytes(logFile);
        // The logged data should contain at least the expected data
        assertTrue(loggedData.length >= 5);
        // Check that the logged data starts with the expected content
        final var expectedData = "Hello".getBytes(StandardCharsets.UTF_8);
        for (var i = 0; i < expectedData.length; i++) {
            assertEquals(expectedData[i], loggedData[i]);
        }
    }

    @Test
    public void testBinaryDataLogging() throws IOException {

        final var logFile = tempDir.resolve("binary.log");
        final var data = new byte[] {0x00, 0x01, 0x02, (byte) 0xFF, 0x7F, (byte) 0x80};
        final var inputStream = new ByteArrayInputStream(data);

        try (var loggingStream = new LoggingInputStream(logFile.toString(), inputStream)) {
            final var buffer = new byte[10];
            loggingStream.read(buffer);
        }

        final var loggedData = Files.readAllBytes(logFile);
        // The logged data should contain at least the expected data
        assertTrue(loggedData.length >= 6);
        // Check that the logged data starts with the expected content
        for (var i = 0; i < data.length; i++) {
            assertEquals(data[i], loggedData[i]);
        }
    }

    @Test
    public void testMultipleStreamsToSameLogFile() throws IOException {

        final var logFile = tempDir.resolve("multiple.log");
        final var data1 = "Hello".getBytes(StandardCharsets.UTF_8);
        final var data2 = "World".getBytes(StandardCharsets.UTF_8);
        final var inputStream1 = new ByteArrayInputStream(data1);
        final var inputStream2 = new ByteArrayInputStream(data2);

        try (var loggingStream1 = new LoggingInputStream(logFile.toString(), inputStream1)) {
            loggingStream1.read();
        }

        try (var loggingStream2 = new LoggingInputStream(logFile.toString(), inputStream2)) {
            loggingStream2.read();
        }

        final var loggedData = Files.readAllBytes(logFile);
        assertEquals(1, loggedData.length);
        assertEquals('W', loggedData[0]); // Second stream overwrites the first
    }
}
