/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.pipe;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ReversePipe covering reverse piping operations with threading.
 */
public final class ReversePipeTest {

    @Test
    public void testReadWithOutputConsumer() {

        final var pipe = new ReversePipe();
        final var testData = "Hello, World!";
        
        final var result = pipe.read(outputStream -> {
            try {
                outputStream.write(testData.getBytes());
                outputStream.flush();
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
            // Write nothing
        });
        
        assertEquals(0, result.length);
    }

    @Test
    public void testReadWithLargeData() {

        final var pipe = new ReversePipe();
        final var testData = new byte[10000];
        for (int i = 0; i < testData.length; i++) {
            testData[i] = (byte) (i % 256);
        }
        
        final var result = pipe.read(outputStream -> {
            try {
                outputStream.write(testData);
                outputStream.flush();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        assertArrayEquals(testData, result);
    }

    @Test
    public void testWriteWithInputFunction() {

        final var pipe = new ReversePipe();
        final var testData = "Hello, World!".getBytes();
        final var output = new ByteArrayOutputStream();
        
        pipe.write(testData, inputStream -> {
            try {
                final var buffer = new byte[1024];
                int bytesRead;
                long totalBytes = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }
                return totalBytes;
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        assertArrayEquals(testData, output.toByteArray());
    }

    @Test
    public void testWriteWithEmptyData() {

        final var pipe = new ReversePipe();
        final var testData = new byte[0];
        final var output = new ByteArrayOutputStream();
        
        pipe.write(testData, inputStream -> {
            try {
                final var buffer = new byte[1024];
                int bytesRead;
                long totalBytes = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }
                return totalBytes;
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        assertEquals(0, output.toByteArray().length);
    }

    @Test
    public void testGoWithOutputConsumerAndInputFunction() {

        final var pipe = new ReversePipe();
        final var testData = "Hello, World!";
        final var output = new ByteArrayOutputStream();
        
        final var bytesProcessed = pipe.go(
            outputStream -> {
                try {
                    outputStream.write(testData.getBytes());
                    outputStream.flush();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            },
            inputStream -> {
                try {
                    final var buffer = new byte[1024];
                    int bytesRead;
                    long totalBytes = 0;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                        totalBytes += bytesRead;
                    }
                    return totalBytes;
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        );
        
        assertEquals(testData.length(), bytesProcessed);
        assertArrayEquals(testData.getBytes(), output.toByteArray());
    }

    @Test
    public void testGoWithCustomBufferSize() {

        final var pipe = new ReversePipe();
        final var testData = "Hello, World!";
        final var output = new ByteArrayOutputStream();
        
        final var bytesProcessed = pipe.go(
            outputStream -> {
                try {
                    outputStream.write(testData.getBytes());
                    outputStream.flush();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            },
            inputStream -> {
                try {
                    final var buffer = new byte[4]; // Small buffer
                    int bytesRead;
                    long totalBytes = 0;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                        totalBytes += bytesRead;
                    }
                    return totalBytes;
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            },
            4
        );
        
        assertEquals(testData.length(), bytesProcessed);
        assertArrayEquals(testData.getBytes(), output.toByteArray());
    }

    @Test
    public void testGoWithDefaultBufferSize() {

        final var pipe = new ReversePipe();
        final var testData = "Hello, World!";
        final var output = new ByteArrayOutputStream();
        
        final var bytesProcessed = pipe.go(
            outputStream -> {
                try {
                    outputStream.write(testData.getBytes());
                    outputStream.flush();
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            },
            inputStream -> {
                try {
                    final var buffer = new byte[1024];
                    int bytesRead;
                    long totalBytes = 0;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                        totalBytes += bytesRead;
                    }
                    return totalBytes;
                } catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        );
        
        assertEquals(testData.length(), bytesProcessed);
        assertArrayEquals(testData.getBytes(), output.toByteArray());
    }

    @Test
    public void testMultipleOperationsOnSameInstance() {

        final var pipe = new ReversePipe();
        
        // First operation
        final var testData1 = "First test";
        final var result1 = pipe.read(outputStream -> {
            try {
                outputStream.write(testData1.getBytes());
                outputStream.flush();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        assertArrayEquals(testData1.getBytes(), result1);
        
        // Second operation
        final var testData2 = "Second test";
        final var result2 = pipe.read(outputStream -> {
            try {
                outputStream.write(testData2.getBytes());
                outputStream.flush();
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        });
        
        assertArrayEquals(testData2.getBytes(), result2);
    }

    @Test
    public void testThreadSafety() {

        final var pipe = new ReversePipe();
        final var testData = "Thread safety test";
        
        // Test that multiple threads can use the same pipe instance
        final var threads = new Thread[5];
        final var results = new byte[5][];
        
        for (int i = 0; i < threads.length; i++) {
            final var index = i;
            threads[i] = new Thread(() -> {
                results[index] = pipe.read(outputStream -> {
                    try {
                        outputStream.write((testData + index).getBytes());
                        outputStream.flush();
                    } catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            });
        }
        
        for (final var thread : threads) {
            thread.start();
        }
        
        for (final var thread : threads) {
            try {
                thread.join();
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                fail("Thread was interrupted");
            }
        }
        
        for (int i = 0; i < results.length; i++) {
            assertArrayEquals((testData + i).getBytes(), results[i]);
        }
    }
}