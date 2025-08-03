/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.serde;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests for the XmlPropertiesSerde class covering Properties serialization functionality. */
final class XmlPropertiesSerdeTest {

    @TempDir
    private Path tempDir;

    @Test
    void testSerializeAndDeserializeSimpleMap() {

        final var testMap = Map.of(
                "key1", "value1",
                "key2", "value2",
                "key3", "value3");

        final var testPath = tempDir.resolve("test.properties");

        assertDoesNotThrow(() -> XmlPropertiesSerde.serialize(testMap, testPath));

        final var result = XmlPropertiesSerde.deserialize(testPath);

        assertEquals(testMap, result);
    }

    @Test
    void testSerializeAndDeserializeEmptyMap() {

        final var testMap = Map.<String, String>of();
        final var testPath = tempDir.resolve("empty.properties");

        assertDoesNotThrow(() -> XmlPropertiesSerde.serialize(testMap, testPath));

        final var result = XmlPropertiesSerde.deserialize(testPath);

        assertTrue(result.isEmpty());
    }

    @Test
    void testSerializeAndDeserializeSingleEntry() {

        final var testMap = Map.of("singleKey", "singleValue");
        final var testPath = tempDir.resolve("single.properties");

        assertDoesNotThrow(() -> XmlPropertiesSerde.serialize(testMap, testPath));

        final var result = XmlPropertiesSerde.deserialize(testPath);

        assertEquals(testMap, result);
        assertEquals("singleValue", result.get("singleKey"));
    }

    @Test
    void testSerializeToOutputStream() {

        final var testMap = Map.of("key", "value");
        final var outputStream = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> XmlPropertiesSerde.serialize(testMap, outputStream));

        final var bytes = outputStream.toByteArray();
        assertTrue(bytes.length > 0);

        final var xmlContent = new String(bytes, StandardCharsets.UTF_8);
        assertTrue(xmlContent.contains("<?xml"));
        assertTrue(xmlContent.contains("<properties>"));
        assertTrue(xmlContent.contains("key"));
        assertTrue(xmlContent.contains("value"));
    }

    @Test
    void testDeserializeFromInputStream() {

        final var testMap = Map.of("streamKey", "streamValue");
        final var outputStream = new ByteArrayOutputStream();

        XmlPropertiesSerde.serialize(testMap, outputStream);

        final var inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        final var result = XmlPropertiesSerde.deserialize(inputStream);

        assertEquals(testMap, result);
    }

    @Test
    void testSerializeAndDeserializeWithNullValues() {

        // Properties doesn't support null values, so we'll test with a map that filters nulls
        final var testMap = new HashMap<String, String>();
        testMap.put("nullKey", ""); // Use empty string instead of null
        testMap.put("validKey", "validValue");

        final var testPath = tempDir.resolve("nulls.properties");

        assertDoesNotThrow(() -> XmlPropertiesSerde.serialize(testMap, testPath));

        final var result = XmlPropertiesSerde.deserialize(testPath);

        assertEquals("", result.get("nullKey"));
        assertEquals("validValue", result.get("validKey"));
    }

    @Test
    void testSerializeAndDeserializeWithEmptyValues() {

        final var testMap = Map.of(
                "emptyValue", "",
                "normalValue", "normal");

        final var testPath = tempDir.resolve("empty_values.properties");

        assertDoesNotThrow(() -> XmlPropertiesSerde.serialize(testMap, testPath));

        final var result = XmlPropertiesSerde.deserialize(testPath);

        assertEquals(testMap, result);
        assertEquals("", result.get("emptyValue"));
    }

    @Test
    void testSerializeAndDeserializeWithDotNotation() {

        final var testMap = Map.of(
                "app.name", "MyApplication",
                "app.version", "1.0.0",
                "app.database.host", "localhost",
                "app.database.port", "5432");

        final var testPath = tempDir.resolve("dot_notation.properties");

        assertDoesNotThrow(() -> XmlPropertiesSerde.serialize(testMap, testPath));

        final var result = XmlPropertiesSerde.deserialize(testPath);

        assertEquals(testMap, result);
    }

    @Test
    void testXmlFormatOutput() {

        final var testMap = Map.of("test", "value");
        final var outputStream = new ByteArrayOutputStream();

        XmlPropertiesSerde.serialize(testMap, outputStream);

        final var xmlContent = outputStream.toString(StandardCharsets.UTF_8);

        assertTrue(xmlContent.contains("<?xml version=\"1.0\" encoding=\"UTF-8\""));
        assertTrue(xmlContent.contains("<properties>"));
        assertTrue(xmlContent.contains("</properties>"));
        assertTrue(xmlContent.contains("<entry key=\"test\">value</entry>"));
    }

    @Test
    void testRoundTripWithStream() {

        final var originalMap = Map.of(
                "database.url", "jdbc:postgresql://localhost:5432/mydb",
                "database.username", "user",
                "server.port", "8080");

        final var outputStream = new ByteArrayOutputStream();
        XmlPropertiesSerde.serialize(originalMap, outputStream);

        final var inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        final var result = XmlPropertiesSerde.deserialize(inputStream);

        assertEquals(originalMap, result);
    }

    @Test
    void testSerializeAndDeserializeWithSpecialCharacters() {

        final var testMap = Map.of(
                "key_with_underscore", "value_with_underscore",
                "key-with-dash", "value-with-dash");

        final var testPath = tempDir.resolve("special.properties");

        assertDoesNotThrow(() -> XmlPropertiesSerde.serialize(testMap, testPath));

        final var result = XmlPropertiesSerde.deserialize(testPath);

        assertEquals(testMap, result);
    }
}
