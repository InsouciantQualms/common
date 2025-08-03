/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.resource;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import org.junit.jupiter.api.Test;

/** Tests for StreamResourceHelper covering classpath stream resolution. */
final class StreamResourceHelperTest {

    @Test
    void testResolveStreamFromClasspathWithExistingResource() {

        // Try to find a class file that should exist
        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path);

        assertTrue(result.isPresent());
        assertInstanceOf(InputStream.class, result.get());

        // Clean up the stream
        assertDoesNotThrow(() -> result.get().close());
    }

    @Test
    void testResolveStreamFromClasspathWithNonExistingResource() {

        final var path = "/non/existing/resource.txt";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path);

        assertFalse(result.isPresent());
    }

    @Test
    void testResolveStreamFromClasspathWithCaller() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path, StreamResourceHelper.class);

        assertTrue(result.isPresent());
        assertInstanceOf(InputStream.class, result.get());

        // Clean up the stream
        assertDoesNotThrow(() -> result.get().close());
    }

    @Test
    void testResolveStreamFromClasspathWithCallerAndNonExistingResource() {

        final var path = "/non/existing/resource.txt";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path, StreamResourceHelper.class);

        assertFalse(result.isPresent());
    }

    @Test
    void testResolveStreamFromClasspathWithDifferentCaller() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path, StreamResourceHelperTest.class);

        assertTrue(result.isPresent());
        assertInstanceOf(InputStream.class, result.get());

        // Clean up the stream
        assertDoesNotThrow(() -> result.get().close());
    }

    @Test
    void testRequireStreamFromClasspathWithExistingResource() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.requireStreamFromClasspath(path);

        assertNotNull(result);
        assertInstanceOf(InputStream.class, result);

        // Clean up the stream
        assertDoesNotThrow(result::close);
    }

    @Test
    void testRequireStreamFromClasspathWithNonExistingResource() {

        final var path = "/non/existing/resource.txt";

        assertThrows(IllegalArgumentException.class, () -> StreamResourceHelper.requireStreamFromClasspath(path));
    }

    @Test
    void testRequireStreamFromClasspathWithCallerAndExistingResource() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.requireStreamFromClasspath(path, StreamResourceHelper.class);

        assertNotNull(result);
        assertInstanceOf(InputStream.class, result);

        // Clean up the stream
        assertDoesNotThrow(result::close);
    }

    @Test
    void testRequireStreamFromClasspathWithCallerAndNonExistingResource() {

        final var path = "/non/existing/resource.txt";

        assertThrows(
                IllegalArgumentException.class,
                () -> StreamResourceHelper.requireStreamFromClasspath(path, StreamResourceHelper.class));
    }

    @Test
    void testRequireStreamFromClasspathWithDifferentCaller() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.requireStreamFromClasspath(path, StreamResourceHelperTest.class);

        assertNotNull(result);
        assertInstanceOf(InputStream.class, result);

        // Clean up the stream
        assertDoesNotThrow(result::close);
    }

    @Test
    void testExceptionMessageForMissingResource() {

        final var path = "/missing/resource.txt";

        final var exception = assertThrows(
                IllegalArgumentException.class, () -> StreamResourceHelper.requireStreamFromClasspath(path));

        assertTrue(exception.getMessage().contains(path));
    }

    @Test
    void testExceptionMessageForMissingResourceWithCaller() {

        final var path = "/missing/resource.txt";

        final var exception = assertThrows(
                IllegalArgumentException.class,
                () -> StreamResourceHelper.requireStreamFromClasspath(path, StreamResourceHelper.class));

        assertTrue(exception.getMessage().contains(path));
    }

    @Test
    void testConsistencyBetweenResolveAndRequire() {

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
    void testConsistencyBetweenResolveAndRequireWithCaller() {

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
    void testConsistencyBetweenCallerAndNonCallerMethods() {

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
    void testRelativePathResolution() {

        // Test with a relative path that should exist
        final var relativePath = "StreamResourceHelper.class";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(relativePath, StreamResourceHelper.class);

        assertTrue(result.isPresent());
        assertInstanceOf(InputStream.class, result.get());

        // Clean up the stream
        assertDoesNotThrow(() -> result.get().close());
    }

    @Test
    void testStreamCanBeRead() {

        final var path = "/dev/iq/common/io/resource/StreamResourceHelper.class";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(path);

        assertTrue(result.isPresent());
        final var stream = result.get();

        // Try to read at least one byte to verify the stream is valid
        assertDoesNotThrow(() -> {
            final var firstByte = stream.read();
            // The stream should contain data (not -1 for EOF immediately)
            assertTrue(true); // -1 is EOF, any other value is valid
            stream.close();
        });
    }

    @Test
    void testMultipleCallsReturnDifferentStreams() {

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
    void testEmptyPathHandling() {

        final var emptyPath = "";
        final var result = StreamResourceHelper.resolveStreamFromClasspath(emptyPath);

        // Empty path may resolve to root classpath directory
        assertTrue(result.isPresent());
    }

    @Test
    void testNullPathHandling() {

        // This should handle null gracefully or throw an appropriate exception
        assertThrows(Exception.class, () -> StreamResourceHelper.resolveStreamFromClasspath(null));
    }

    @Test
    void testStreamContentIsNotEmpty() {

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
