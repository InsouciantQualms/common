/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/** Tests for UriResourceHelper covering classpath URI resolution and require functionality. */
final class UriResourceHelperTest {

    @Test
    void testResolveUriFromClasspathWithExistingResource() {

        // Try to find a class file that should exist
        final var path = "/dev/iq/common/io/resource/UriResourceHelper.class";
        final var result = UriResourceHelper.resolveUriFromClasspath(path);

        assertTrue(result.isPresent());
        assertInstanceOf(URI.class, result.get());
        assertNotNull(result.get().toString());
        assertFalse(result.get().toString().isEmpty());
    }

    @Test
    void testResolveUriFromClasspathWithNonExistingResource() {

        final var path = "/non/existing/resource.txt";
        final var result = UriResourceHelper.resolveUriFromClasspath(path);

        assertFalse(result.isPresent());
    }

    @Test
    void testResolveUriFromClasspathWithCaller() {

        final var path = "/dev/iq/common/io/resource/UriResourceHelper.class";
        final var result = UriResourceHelper.resolveUriFromClasspath(path, UriResourceHelper.class);

        assertTrue(result.isPresent());
        assertInstanceOf(URI.class, result.get());
        assertNotNull(result.get().toString());
        assertFalse(result.get().toString().isEmpty());
    }

    @Test
    void testResolveUriFromClasspathWithCallerAndNonExistingResource() {

        final var path = "/non/existing/resource.txt";
        final var result = UriResourceHelper.resolveUriFromClasspath(path, UriResourceHelper.class);

        assertFalse(result.isPresent());
    }

    @Test
    void testResolveUriFromClasspathWithDifferentCaller() {

        final var path = "/dev/iq/common/io/resource/UriResourceHelper.class";
        final var result = UriResourceHelper.resolveUriFromClasspath(path, UriResourceHelperTest.class);

        assertTrue(result.isPresent());
        assertInstanceOf(URI.class, result.get());
        assertNotNull(result.get().toString());
        assertFalse(result.get().toString().isEmpty());
    }

    @Test
    void testRequireUriFromClasspathWithExistingResource() {

        final var path = "/dev/iq/common/io/resource/UriResourceHelper.class";
        final var result = UriResourceHelper.requireUriFromClasspath(path);

        assertNotNull(result);
        assertInstanceOf(URI.class, result);
        assertNotNull(result.toString());
        assertFalse(result.toString().isEmpty());
    }

    @Test
    void testRequireUriFromClasspathWithNonExistingResource() {

        final var path = "/non/existing/resource.txt";

        assertThrows(IllegalArgumentException.class, () -> UriResourceHelper.requireUriFromClasspath(path));
    }

    @Test
    void testRequireUriFromClasspathWithCallerAndExistingResource() {

        final var path = "/dev/iq/common/io/resource/UriResourceHelper.class";
        final var result = UriResourceHelper.requireUriFromClasspath(path, UriResourceHelper.class);

        assertNotNull(result);
        assertInstanceOf(URI.class, result);
        assertNotNull(result.toString());
        assertFalse(result.toString().isEmpty());
    }

    @Test
    void testRequireUriFromClasspathWithCallerAndNonExistingResource() {

        final var path = "/non/existing/resource.txt";

        assertThrows(
                IllegalArgumentException.class,
                () -> UriResourceHelper.requireUriFromClasspath(path, UriResourceHelper.class));
    }

    @Test
    void testRequireUriFromClasspathWithDifferentCaller() {

        final var path = "/dev/iq/common/io/resource/UriResourceHelper.class";
        final var result = UriResourceHelper.requireUriFromClasspath(path, UriResourceHelperTest.class);

        assertNotNull(result);
        assertInstanceOf(URI.class, result);
        assertNotNull(result.toString());
        assertFalse(result.toString().isEmpty());
    }

    @Test
    void testExceptionMessageForMissingResource() {

        final var path = "/missing/resource.txt";

        final var exception =
                assertThrows(IllegalArgumentException.class, () -> UriResourceHelper.requireUriFromClasspath(path));

        assertTrue(exception.getMessage().contains(path));
    }

    @Test
    void testExceptionMessageForMissingResourceWithCaller() {

        final var path = "/missing/resource.txt";

        final var exception = assertThrows(
                IllegalArgumentException.class,
                () -> UriResourceHelper.requireUriFromClasspath(path, UriResourceHelper.class));

        assertTrue(exception.getMessage().contains(path));
    }

    @Test
    void testConsistencyBetweenResolveAndRequire() {

        final var path = "/dev/iq/common/io/resource/UriResourceHelper.class";

        final var resolvedUri = UriResourceHelper.resolveUriFromClasspath(path);
        final var requiredUri = UriResourceHelper.requireUriFromClasspath(path);

        assertTrue(resolvedUri.isPresent());
        assertEquals(resolvedUri.get(), requiredUri);
    }

    @Test
    void testConsistencyBetweenResolveAndRequireWithCaller() {

        final var path = "/dev/iq/common/io/resource/UriResourceHelper.class";
        final var caller = UriResourceHelper.class;

        final var resolvedUri = UriResourceHelper.resolveUriFromClasspath(path, caller);
        final var requiredUri = UriResourceHelper.requireUriFromClasspath(path, caller);

        assertTrue(resolvedUri.isPresent());
        assertEquals(resolvedUri.get(), requiredUri);
    }

    @Test
    void testConsistencyBetweenCallerAndNonCallerMethods() {

        final var path = "/dev/iq/common/io/resource/UriResourceHelper.class";

        final var uriWithoutCaller = UriResourceHelper.resolveUriFromClasspath(path);
        final var uriWithCaller = UriResourceHelper.resolveUriFromClasspath(path, CodeSourceHelper.class);

        // Both should return the same result when using the same effective caller
        assertEquals(uriWithoutCaller.isPresent(), uriWithCaller.isPresent());

        if (uriWithoutCaller.isPresent() && uriWithCaller.isPresent()) {
            assertEquals(uriWithoutCaller.get(), uriWithCaller.get());
        }
    }

    @Test
    void testRelativePathResolution() {

        // Test with a relative path that should exist
        final var relativePath = "UriResourceHelper.class";
        final var result = UriResourceHelper.resolveUriFromClasspath(relativePath, UriResourceHelper.class);

        assertTrue(result.isPresent());
        assertInstanceOf(URI.class, result.get());
        assertNotNull(result.get().toString());
        assertFalse(result.get().toString().isEmpty());
    }

    @Test
    void testUriIsValid() {

        final var path = "/dev/iq/common/io/resource/UriResourceHelper.class";
        final var result = UriResourceHelper.resolveUriFromClasspath(path);

        assertTrue(result.isPresent());
        final var uri = result.get();

        assertNotNull(uri.getScheme());
        assertFalse(uri.getScheme().isEmpty());

        // Common schemes for resources are file, jar, etc.
        assertTrue(uri.getScheme().equals("file") || uri.getScheme().equals("jar"));
    }

    @Test
    void testMultipleCallsReturnSameResult() {

        final var path = "/dev/iq/common/io/resource/UriResourceHelper.class";

        final var result1 = UriResourceHelper.resolveUriFromClasspath(path);
        final var result2 = UriResourceHelper.resolveUriFromClasspath(path);

        assertEquals(result1.isPresent(), result2.isPresent());

        if (result1.isPresent() && result2.isPresent()) {
            assertEquals(result1.get(), result2.get());
        }
    }

    @Test
    void testEmptyPathHandling() {

        final var emptyPath = "";
        final var result = UriResourceHelper.resolveUriFromClasspath(emptyPath);

        // Empty path may resolve to root classpath directory
        assertTrue(result.isPresent());
    }

    @Test
    void testNullPathHandling() {

        // This should handle null gracefully or throw an appropriate exception
        assertThrows(Exception.class, () -> UriResourceHelper.resolveUriFromClasspath(null));
    }

    @Test
    void testRequireHelperWithPresentOptional() {

        final var value = "test value";
        final var optional = Optional.of(value);
        final var path = "/test/path";

        final var result = UriResourceHelper.require(path, optional);

        assertEquals(value, result);
    }

    @Test
    void testRequireHelperWithEmptyOptional() {

        final var optional = Optional.<String>empty();
        final var path = "/test/path";

        final var exception =
                assertThrows(IllegalArgumentException.class, () -> UriResourceHelper.require(path, optional));

        assertTrue(exception.getMessage().contains(path));
    }

    @Test
    void testRequireHelperWithNullPath() {

        final var optional = Optional.of("test value");

        // This should pass without throwing an exception since the optional has a value
        final var result = UriResourceHelper.require(null, optional);
        assertEquals("test value", result);
    }

    @Test
    void testRequireHelperWithDifferentTypes() {

        final var intValue = 42;
        final var intOptional = Optional.of(intValue);
        final var path = "/test/path";

        final var result = UriResourceHelper.require(path, intOptional);

        assertEquals(intValue, result);
    }

    @Test
    void testRequireHelperExceptionMessageFormat() {

        final var path = "/missing/resource.txt";
        final var optional = Optional.<String>empty();

        final var exception =
                assertThrows(IllegalArgumentException.class, () -> UriResourceHelper.require(path, optional));

        assertEquals("Missing resource /missing/resource.txt", exception.getMessage());
    }
}
