/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.serde;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests JSON serialization and deserialization.
 */
final class JsonSerdeTest {

    /** Test JSON to use. */
    private static final String TEST_JSON = """
        {
          "name" : "Sascha",
          "age" : 42
        }""";

    /**
     * Tests serialization.
     */
    @Test
    public void testSerialize() {

        final var test = new Tester("Sascha", 42);
        final var json = JsonSerde.toJson(test);
        Assertions.assertEquals(TEST_JSON, json);
    }

    /**
     * Tests deserialization.
     */
    @Test
    public void testDeserialize() {

        final var test = JsonSerde.fromJson(TEST_JSON, Tester.class);
        Assertions.assertEquals("Sascha", test.name);
        Assertions.assertEquals(42, test.age);
    }

    /**
     * Data class for testing.
     */
    private record Tester(String name, int age) {}
}
