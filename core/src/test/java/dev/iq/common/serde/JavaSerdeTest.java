/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.serde;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import dev.iq.common.error.IoException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/** Tests for the JavaSerde class covering Java serialization functionality. */
final class JavaSerdeTest {

    @TempDir
    private Path tempDir;

    @Test
    void testSerializeAndDeserializeString() {

        final var testString = "Hello, World!";
        final var testPath = tempDir.resolve("test.ser");

        assertDoesNotThrow(() -> JavaSerde.serialize(testString, testPath));

        final var result = JavaSerde.deserialize(testPath, String.class);

        assertEquals(testString, result);
    }

    @Test
    void testSerializeAndDeserializeInteger() {

        final var testInteger = 42;
        final var testPath = tempDir.resolve("test.ser");

        assertDoesNotThrow(() -> JavaSerde.serialize(testInteger, testPath));

        final var result = JavaSerde.deserialize(testPath, Integer.class);

        assertEquals(testInteger, result);
    }

    @Test
    void testSerializeAndDeserializeList() {

        final var testList = new ArrayList<String>();
        testList.add("item1");
        testList.add("item2");
        testList.add("item3");

        final var testPath = tempDir.resolve("test.ser");

        assertDoesNotThrow(() -> JavaSerde.serialize(testList, testPath));

        final var result = JavaSerde.deserialize(testPath, ArrayList.class);

        assertEquals(testList, result);
    }

    @Test
    void testSerializeAndDeserializeMap() {

        final var testMap = new HashMap<String, String>();
        testMap.put("key1", "value1");
        testMap.put("key2", "value2");

        final var testPath = tempDir.resolve("test.ser");

        assertDoesNotThrow(() -> JavaSerde.serialize(testMap, testPath));

        final var result = JavaSerde.deserialize(testPath, HashMap.class);

        assertEquals(testMap, result);
    }

    @Test
    void testSerializeAndDeserializeCustomObject() {

        final var testObject = new TestSerializable("name", 123);
        final var testPath = tempDir.resolve("test.ser");

        assertDoesNotThrow(() -> JavaSerde.serialize(testObject, testPath));

        final var result = JavaSerde.deserialize(testPath, TestSerializable.class);

        assertEquals(testObject.name(), result.name());
        assertEquals(testObject.value(), result.value());
    }

    @Test
    void testSerializeToOutputStream() {

        final var testString = "Stream test";
        final var outputStream = new ByteArrayOutputStream();

        assertDoesNotThrow(() -> JavaSerde.serialize(testString, outputStream));

        final var bytes = outputStream.toByteArray();
        assertTrue(bytes.length > 0);
    }

    @Test
    void testDeserializeFromInputStream() {

        final var testString = "Stream test";
        final var outputStream = new ByteArrayOutputStream();

        JavaSerde.serialize(testString, outputStream);

        final var inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        final var result = JavaSerde.deserialize(inputStream, String.class);

        assertEquals(testString, result);
    }

    @Test
    void testSerializeNullValue() {

        final var testPath = tempDir.resolve("test.ser");

        assertDoesNotThrow(() -> JavaSerde.serialize(null, testPath));

        final var result = JavaSerde.deserialize(testPath, String.class);

        assertNull(result);
    }

    @Test
    void testSerializeEmptyString() {

        final var testString = "";
        final var testPath = tempDir.resolve("test.ser");

        assertDoesNotThrow(() -> JavaSerde.serialize(testString, testPath));

        final var result = JavaSerde.deserialize(testPath, String.class);

        assertEquals("", result);
    }

    @Test
    void testSerializeEmptyList() {

        final var testList = new ArrayList<String>();
        final var testPath = tempDir.resolve("test.ser");

        assertDoesNotThrow(() -> JavaSerde.serialize(testList, testPath));

        final var result = JavaSerde.deserialize(testPath, ArrayList.class);

        assertEquals(testList, result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeserializeNonExistentFile() {

        final var nonExistentPath = tempDir.resolve("nonexistent.ser");

        assertThrows(IoException.class, () -> JavaSerde.deserialize(nonExistentPath, String.class));
    }

    @Test
    void testDeserializeWithWrongType() {

        final var testString = "Hello";
        final var testPath = tempDir.resolve("test.ser");

        JavaSerde.serialize(testString, testPath);

        assertThrows(ClassCastException.class, () -> JavaSerde.deserialize(testPath, Integer.class));
    }

    @Test
    void testSerializeToReadOnlyDirectory() {

        final var readOnlyPath = tempDir.resolve("readonly");
        readOnlyPath.toFile().mkdirs();
        readOnlyPath.toFile().setReadOnly();

        final var testPath = readOnlyPath.resolve("test.ser");

        assertThrows(IoException.class, () -> JavaSerde.serialize("test", testPath));
    }

    @Test
    void testSerializeWithClosedOutputStream() {

        final var outputStream = new ByteArrayOutputStream();
        try {
            outputStream.close();
        } catch (final IOException e) {
            fail("Failed to close stream in test setup");
        }

        // ByteArrayOutputStream doesn't throw on write after close, so just verify it doesn't fail
        assertDoesNotThrow(() -> JavaSerde.serialize("test", outputStream));
    }

    @Test
    void testRoundTripSerialization() {

        final var testObject = new TestSerializable("test", 456);
        final var testPath = tempDir.resolve("roundtrip.ser");

        JavaSerde.serialize(testObject, testPath);
        final var result = JavaSerde.deserialize(testPath, TestSerializable.class);

        assertEquals(testObject.name(), result.name());
        assertEquals(testObject.value(), result.value());
    }

    @Test
    void testSerializeComplexObject() {

        final var innerList = List.of("a", "b", "c");
        final var innerMap = Map.of("key1", "value1", "key2", "value2");
        final var complexObject = new ComplexSerializable(innerList, innerMap);

        final var testPath = tempDir.resolve("complex.ser");

        JavaSerde.serialize(complexObject, testPath);
        final var result = JavaSerde.deserialize(testPath, ComplexSerializable.class);

        assertEquals(complexObject.list(), result.list());
        assertEquals(complexObject.map(), result.map());
    }

    private record TestSerializable(String name, int value) implements Serializable {}

    private record ComplexSerializable(List<String> list, Map<String, String> map) implements Serializable {}
}
