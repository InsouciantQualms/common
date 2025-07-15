/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.resource;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PathResourceHelper covering classpath path resolution.
 */
public final class PathResourceHelperTest {

    @Test
    public void testResolvePathFromClasspathWithExistingResource() {

        // Try to find a class file that should exist
        final var path = "/dev/iq/common/io/resource/PathResourceHelper.class";
        final var result = PathResourceHelper.resolvePathFromClasspath(path);
        
        assertTrue(result.isPresent());
        assertInstanceOf(Path.class, result.get());
    }

    @Test
    public void testResolvePathFromClasspathWithNonExistingResource() {

        final var path = "/non/existing/resource.txt";
        final var result = PathResourceHelper.resolvePathFromClasspath(path);
        
        assertFalse(result.isPresent());
    }

    @Test
    public void testResolvePathFromClasspathWithCaller() {

        final var path = "/dev/iq/common/io/resource/PathResourceHelper.class";
        final var result = PathResourceHelper.resolvePathFromClasspath(path, PathResourceHelper.class);
        
        assertTrue(result.isPresent());
        assertInstanceOf(Path.class, result.get());
    }

    @Test
    public void testResolvePathFromClasspathWithCallerAndNonExistingResource() {

        final var path = "/non/existing/resource.txt";
        final var result = PathResourceHelper.resolvePathFromClasspath(path, PathResourceHelper.class);
        
        assertFalse(result.isPresent());
    }

    @Test
    public void testResolvePathFromClasspathWithDifferentCaller() {

        final var path = "/dev/iq/common/io/resource/PathResourceHelper.class";
        final var result = PathResourceHelper.resolvePathFromClasspath(path, PathResourceHelperTest.class);
        
        assertTrue(result.isPresent());
        assertInstanceOf(Path.class, result.get());
    }

    @Test
    public void testRequirePathFromClasspathWithExistingResource() {

        final var path = "/dev/iq/common/io/resource/PathResourceHelper.class";
        final var result = PathResourceHelper.requirePathFromClasspath(path);
        
        assertNotNull(result);
        assertInstanceOf(Path.class, result);
    }

    @Test
    public void testRequirePathFromClasspathWithNonExistingResource() {

        final var path = "/non/existing/resource.txt";
        
        assertThrows(IllegalArgumentException.class, () -> {
            PathResourceHelper.requirePathFromClasspath(path);
        });
    }

    @Test
    public void testRequirePathFromClasspathWithCallerAndExistingResource() {

        final var path = "/dev/iq/common/io/resource/PathResourceHelper.class";
        final var result = PathResourceHelper.requirePathFromClasspath(path, PathResourceHelper.class);
        
        assertNotNull(result);
        assertInstanceOf(Path.class, result);
    }

    @Test
    public void testRequirePathFromClasspathWithCallerAndNonExistingResource() {

        final var path = "/non/existing/resource.txt";
        
        assertThrows(IllegalArgumentException.class, () -> {
            PathResourceHelper.requirePathFromClasspath(path, PathResourceHelper.class);
        });
    }

    @Test
    public void testRequirePathFromClasspathWithDifferentCaller() {

        final var path = "/dev/iq/common/io/resource/PathResourceHelper.class";
        final var result = PathResourceHelper.requirePathFromClasspath(path, PathResourceHelperTest.class);
        
        assertNotNull(result);
        assertInstanceOf(Path.class, result);
    }

    @Test
    public void testExceptionMessageForMissingResource() {

        final var path = "/missing/resource.txt";
        
        final var exception = assertThrows(IllegalArgumentException.class, () -> {
            PathResourceHelper.requirePathFromClasspath(path);
        });
        
        assertTrue(exception.getMessage().contains(path));
    }

    @Test
    public void testExceptionMessageForMissingResourceWithCaller() {

        final var path = "/missing/resource.txt";
        
        final var exception = assertThrows(IllegalArgumentException.class, () -> {
            PathResourceHelper.requirePathFromClasspath(path, PathResourceHelper.class);
        });
        
        assertTrue(exception.getMessage().contains(path));
    }

    @Test
    public void testConsistencyBetweenResolveAndRequire() {

        final var path = "/dev/iq/common/io/resource/PathResourceHelper.class";
        
        final var resolvedPath = PathResourceHelper.resolvePathFromClasspath(path);
        final var requiredPath = PathResourceHelper.requirePathFromClasspath(path);
        
        assertTrue(resolvedPath.isPresent());
        assertEquals(resolvedPath.get(), requiredPath);
    }

    @Test
    public void testConsistencyBetweenResolveAndRequireWithCaller() {

        final var path = "/dev/iq/common/io/resource/PathResourceHelper.class";
        final var caller = PathResourceHelper.class;
        
        final var resolvedPath = PathResourceHelper.resolvePathFromClasspath(path, caller);
        final var requiredPath = PathResourceHelper.requirePathFromClasspath(path, caller);
        
        assertTrue(resolvedPath.isPresent());
        assertEquals(resolvedPath.get(), requiredPath);
    }

    @Test
    public void testConsistencyBetweenCallerAndNonCallerMethods() {

        final var path = "/dev/iq/common/io/resource/PathResourceHelper.class";
        
        final var pathWithoutCaller = PathResourceHelper.resolvePathFromClasspath(path);
        final var pathWithCaller = PathResourceHelper.resolvePathFromClasspath(path, CodeSourceHelper.class);
        
        // Both should return the same result when using the same effective caller
        assertEquals(pathWithoutCaller.isPresent(), pathWithCaller.isPresent());
        
        if (pathWithoutCaller.isPresent() && pathWithCaller.isPresent()) {
            assertEquals(pathWithoutCaller.get(), pathWithCaller.get());
        }
    }

    @Test
    public void testRelativePathResolution() {

        // Test with a relative path that should exist
        final var relativePath = "PathResourceHelper.class";
        final var result = PathResourceHelper.resolvePathFromClasspath(relativePath, PathResourceHelper.class);
        
        assertTrue(result.isPresent());
        assertInstanceOf(Path.class, result.get());
    }

    @Test
    public void testMultipleCallsReturnSameResult() {

        final var path = "/dev/iq/common/io/resource/PathResourceHelper.class";
        
        final var result1 = PathResourceHelper.resolvePathFromClasspath(path);
        final var result2 = PathResourceHelper.resolvePathFromClasspath(path);
        
        assertEquals(result1.isPresent(), result2.isPresent());
        
        if (result1.isPresent() && result2.isPresent()) {
            assertEquals(result1.get(), result2.get());
        }
    }

    @Test
    public void testEmptyPathHandling() {

        final var emptyPath = "";
        final var result = PathResourceHelper.resolvePathFromClasspath(emptyPath);
        
        // Empty path should not resolve to a valid resource
        assertFalse(result.isPresent());
    }

    @Test
    public void testNullPathHandling() {

        // This should handle null gracefully or throw an appropriate exception
        assertThrows(Exception.class, () -> {
            PathResourceHelper.resolvePathFromClasspath(null);
        });
    }
}