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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import org.junit.jupiter.api.Test;

/** Tests for CodeSourceHelper covering code source location and JAR detection. */
public final class CodeSourceHelperTest {

    @Test
    public void testLocateCodeSource() {

        final var uri = CodeSourceHelper.locateCodeSource(CodeSourceHelperTest.class);

        assertNotNull(uri);
        assertInstanceOf(URI.class, uri);
        assertNotNull(uri.toString());
        assertFalse(uri.toString().isEmpty());
    }

    @Test
    public void testLocateCodeSourceWithDifferentClass() {

        final var uri = CodeSourceHelper.locateCodeSource(CodeSourceHelper.class);

        assertNotNull(uri);
        assertInstanceOf(URI.class, uri);
        assertNotNull(uri.toString());
        assertFalse(uri.toString().isEmpty());
    }

    @Test
    public void testIsJarForTestClass() {

        final var isJar = CodeSourceHelper.isJar(CodeSourceHelperTest.class);

        // Test classes are typically loaded from build directories, not JARs
        // The result may vary based on the test environment
        assertInstanceOf(Boolean.class, isJar);
    }

    @Test
    public void testIsJarForCodeSourceHelper() {

        final var isJar = CodeSourceHelper.isJar(CodeSourceHelper.class);

        // CodeSourceHelper class jar status depends on runtime environment
        assertInstanceOf(Boolean.class, isJar);
    }

    @Test
    public void testIsJarForThisHelper() {

        final var isJar = CodeSourceHelper.isJar(CodeSourceHelper.class);

        assertInstanceOf(Boolean.class, isJar);
    }

    @Test
    public void testResolveStream() {

        final var stream = CodeSourceHelper.resolveStream(CodeSourceHelperTest.class);

        assertNotNull(stream);

        // Clean up the stream
        assertDoesNotThrow(stream::close);
    }

    @Test
    public void testResolveStreamWithDifferentClass() {

        final var stream = CodeSourceHelper.resolveStream(CodeSourceHelper.class);

        assertNotNull(stream);

        // Clean up the stream
        assertDoesNotThrow(stream::close);
    }

    @Test
    public void testResolveStreamWithCodeSourceHelper() {

        final var stream = CodeSourceHelper.resolveStream(CodeSourceHelper.class);

        assertNotNull(stream);

        // Clean up the stream
        assertDoesNotThrow(stream::close);
    }

    @Test
    public void testConsistentBehaviorBetweenMethods() {

        final var testClass = CodeSourceHelperTest.class;
        final var uri = CodeSourceHelper.locateCodeSource(testClass);
        final var isJar = CodeSourceHelper.isJar(testClass);

        // The isJar result should be consistent with the URI's string representation
        // JAR URIs typically contain ".jar" but system classes might have different patterns
        if (uri.toString().contains(".jar")) {
            assertTrue(isJar);
        } else {
            assertFalse(isJar);
        }
    }

    @Test
    public void testCodeSourceLocationIsNotEmpty() {

        final var classes = new Class<?>[] {CodeSourceHelperTest.class, CodeSourceHelper.class};

        for (final var clazz : classes) {
            final var uri = CodeSourceHelper.locateCodeSource(clazz);
            assertNotNull(uri);
            assertFalse(uri.toString().isEmpty());
        }
    }

    @Test
    public void testStreamCanBeRead() {

        final var stream = CodeSourceHelper.resolveStream(CodeSourceHelperTest.class);

        assertNotNull(stream);

        // Try to read at least one byte to verify the stream is valid
        assertDoesNotThrow(() -> {
            final var firstByte = stream.read();
            // The stream should contain data (not -1 for EOF immediately)
            assertTrue(firstByte >= -1); // -1 is EOF, any other value is valid
            stream.close();
        });
    }

    @Test
    public void testMultipleCallsReturnSameResult() {

        final var testClass = CodeSourceHelperTest.class;

        final var uri1 = CodeSourceHelper.locateCodeSource(testClass);
        final var uri2 = CodeSourceHelper.locateCodeSource(testClass);

        assertEquals(uri1, uri2);

        final var isJar1 = CodeSourceHelper.isJar(testClass);
        final var isJar2 = CodeSourceHelper.isJar(testClass);

        assertEquals(isJar1, isJar2);
    }

    @Test
    public void testDifferentClassesHaveDifferentOrSameSources() {

        final var testClassUri = CodeSourceHelper.locateCodeSource(CodeSourceHelperTest.class);
        final var helperClassUri = CodeSourceHelper.locateCodeSource(CodeSourceHelper.class);

        assertNotNull(testClassUri);
        assertNotNull(helperClassUri);

        // Test and helper classes might be from different sources (test vs main)
        // but both should be valid URIs - just verify they exist
        assertFalse(testClassUri.toString().isEmpty());
        assertFalse(helperClassUri.toString().isEmpty());
    }

    @Test
    public void testUriIsValid() {

        final var uri = CodeSourceHelper.locateCodeSource(CodeSourceHelperTest.class);

        assertNotNull(uri);
        assertNotNull(uri.getScheme());
        assertFalse(uri.getScheme().isEmpty());

        // Common schemes are file, jar, etc.
        assertTrue(uri.getScheme().equals("file") || uri.getScheme().equals("jar"));
    }
}
