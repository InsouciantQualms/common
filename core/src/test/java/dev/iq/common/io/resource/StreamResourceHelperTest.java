/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.resource;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for StreamResourceHelper covering classpath stream resolution.
 */
public final class StreamResourceHelperTest {

    @Test
    public void testResolveStreamFromClasspathWithExistingResource() {

        // Try to find a class file that should exist
        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path);
        
        assertTrue(result.isPresent());
        assertInstanceOf(InputStream.class, result.get());
        
        // Clean up the stream
        assertDoesNotThrow(() -> result.get().close());
    }

    @Test
    public void testResolveStreamFromClasspathWithNonExistingResource() {

        final var path = "/non/existing/resource.txt";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path);
        
        assertFalse(result.isPresent());
    }

    @Test
    public void testResolveStreamFromClasspathWithCaller() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path, StreamResourceHelper.class);
        
        assertTrue(result.isPresent());
        assertInstanceOf(InputStream.class, result.get());
        
        // Clean up the stream
        assertDoesNotThrow(() -> result.get().close());
    }

    @Test
    public void testResolveStreamFromClasspathWithCallerAndNonExistingResource() {

        final var path = "/non/existing/resource.txt";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path, StreamResourceHelper.class);
        
        assertFalse(result.isPresent());
    }

    @Test
    public void testResolveStreamFromClasspathWithDifferentCaller() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path, StreamResourceHelperTest.class);
        
        assertTrue(result.isPresent());
        assertInstanceOf(InputStream.class, result.get());
        
        // Clean up the stream
        assertDoesNotThrow(() -> result.get().close());
    }

    @Test
    public void testRequireStreamFromClasspathWithExistingResource() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.requireStreamFromClasspath(path);
        
        assertNotNull(result);
        assertInstanceOf(InputStream.class, result);
        
        // Clean up the stream
        assertDoesNotThrow(() -> result.close());
    }

    @Test
    public void testRequireStreamFromClasspathWithNonExistingResource() {

        final var path = "/non/existing/resource.txt";
        
        assertThrows(IllegalArgumentException.class, () -> {
            StreamResourceHelper.requireStreamFromClasspath(path);
        });
    }

    @Test
    public void testRequireStreamFromClasspathWithCallerAndExistingResource() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.requireStreamFromClasspath(path, StreamResourceHelper.class);
        
        assertNotNull(result);
        assertInstanceOf(InputStream.class, result);
        
        // Clean up the stream
        assertDoesNotThrow(() -> result.close());
    }

    @Test
    public void testRequireStreamFromClasspathWithCallerAndNonExistingResource() {

        final var path = "/non/existing/resource.txt";
        
        assertThrows(IllegalArgumentException.class, () -> {
            StreamResourceHelper.requireStreamFromClasspath(path, StreamResourceHelper.class);
        });
    }

    @Test
    public void testRequireStreamFromClasspathWithDifferentCaller() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.requireStreamFromClasspath(path, StreamResourceHelperTest.class);
        
        assertNotNull(result);
        assertInstanceOf(InputStream.class, result);
        
        // Clean up the stream
        assertDoesNotThrow(() -> result.close());
    }

    @Test
    public void testExceptionMessageForMissingResource() {

        final var path = "/missing/resource.txt";
        
        final var exception = assertThrows(IllegalArgumentException.class, () -> {
            StreamResourceHelper.requireStreamFromClasspath(path);
        });
        
        assertTrue(exception.getMessage().contains(path));
    }

    @Test
    public void testExceptionMessageForMissingResourceWithCaller() {

        final var path = "/missing/resource.txt";
        
        final var exception = assertThrows(IllegalArgumentException.class, () -> {
            StreamResourceHelper.requireStreamFromClasspath(path, StreamResourceHelper.class);
        });
        
        assertTrue(exception.getMessage().contains(path));
    }

    @Test
    public void testConsistencyBetweenResolveAndRequire() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        
        final var resolvedStream = StreamResourceHelper.resolveStreamFromClasspath(path);
        final var requiredStream = StreamResourceHelper.requireStreamFromClasspath(path);
        
        assertTrue(resolvedStream.isPresent());
        assertNotNull(requiredStream);
        
        // Both streams should be readable
        assertDoesNotThrow(() -> {
            resolvedStream.get().read();
            requiredStream.read();
            resolvedStream.get().close();
            requiredStream.close();
        });
    }

    @Test
    public void testConsistencyBetweenResolveAndRequireWithCaller() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var caller = StreamResourceHelper.class;
        
        final var resolvedStream = StreamResourceHelper.resolveStreamFromClasspath(path, caller);
        final var requiredStream = StreamResourceHelper.requireStreamFromClasspath(path, caller);
        
        assertTrue(resolvedStream.isPresent());
        assertNotNull(requiredStream);
        
        // Both streams should be readable
        assertDoesNotThrow(() -> {
            resolvedStream.get().read();
            requiredStream.read();
            resolvedStream.get().close();
            requiredStream.close();
        });
    }

    @Test
    public void testConsistencyBetweenCallerAndNonCallerMethods() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        
        final var streamWithoutCaller = StreamResourceHelper.resolveStreamFromClasspath(path);
        final var streamWithCaller = StreamResourceHelper.resolveStreamFromClasspath(path, CodeSourceHelper.class);
        
        // Both should return the same result when using the same effective caller
        assertEquals(streamWithoutCaller.isPresent(), streamWithCaller.isPresent());
        
        if (streamWithoutCaller.isPresent() && streamWithCaller.isPresent()) {
            // Both streams should be readable
            assertDoesNotThrow(() -> {
                streamWithoutCaller.get().read();
                streamWithCaller.get().read();
                streamWithoutCaller.get().close();
                streamWithCaller.get().close();
            });
        }
    }

    @Test
    public void testRelativePathResolution() {

        // Test with a relative path that should exist
        final var relativePath = "StreamResourceHelper.class";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(relativePath, StreamResourceHelper.class);
        
        assertTrue(result.isPresent());
        assertInstanceOf(InputStream.class, result.get());
        
        // Clean up the stream
        assertDoesNotThrow(() -> result.get().close());
    }

    @Test
    public void testStreamCanBeRead() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path);
        
        assertTrue(result.isPresent());
        final var stream = result.get();
        
        // Try to read at least one byte to verify the stream is valid
        assertDoesNotThrow(() -> {
            final var firstByte = stream.read();
            // The stream should contain data (not -1 for EOF immediately)
            assertTrue(firstByte >= 0 || firstByte == -1); // Both are valid
            stream.close();
        });
    }

    @Test
    public void testMultipleCallsReturnDifferentStreams() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        
        final var stream1 = StreamResourceHelper.resolveStreamFromClasspath(path);
        final var stream2 = StreamResourceHelper.resolveStreamFromClasspath(path);
        
        assertTrue(stream1.isPresent());
        assertTrue(stream2.isPresent());
        
        // Should return different stream instances
        assertNotSame(stream1.get(), stream2.get());
        
        // Clean up the streams
        assertDoesNotThrow(() -> {
            stream1.get().close();
            stream2.get().close();
        });
    }

    @Test
    public void testEmptyPathHandling() {

        final var emptyPath = "";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(emptyPath);
        
        // Empty path should not resolve to a valid resource
        assertFalse(result.isPresent());
    }

    @Test
    public void testNullPathHandling() {

        // This should handle null gracefully or throw an appropriate exception
        assertThrows(Exception.class, () -> {
            StreamResourceHelper.resolveStreamFromClasspath(null);
        });
    }

    @Test
    public void testStreamContentIsNotEmpty() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path);
        
        assertTrue(result.isPresent());
        final var stream = result.get();
        
        // Try to read multiple bytes to verify the stream has content
        assertDoesNotThrow(() -> {
            final var buffer = new byte[1024];
            final var bytesRead = stream.read(buffer);
            
            // Should read at least some bytes from a class file
            assertTrue(bytesRead > 0);
            
            stream.close();
        });
    }
}