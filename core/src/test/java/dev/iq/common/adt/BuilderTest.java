/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.adt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Objects;
import org.junit.jupiter.api.Test;

/** Tests for the Builder interface covering basic functionality. */
public final class BuilderTest {

    @Test
    void testBuildWithString() {

        final var builder = new TestStringBuilder("test");
        final var result = builder.build();

        assertNotNull(result);
        assertEquals("test", result);
    }

    @Test
    void testBuildWithInteger() {

        final var builder = new TestIntegerBuilder(42);
        final var result = builder.build();

        assertNotNull(result);
        assertEquals(42, result);
    }

    @Test
    void testBuildWithNull() {

        final var builder = new TestStringBuilder(null);
        final var result = builder.build();

        assertNull(result);
    }

    @Test
    void testBuildWithComplexObject() {

        final var data = new TestData("name", 123);
        final var builder = new TestObjectBuilder(data);
        final var result = builder.build();

        assertNotNull(result);
        assertEquals(data, result);
        assertEquals("name", result.name());
        assertEquals(123, result.value());
    }

    @Test
    void testBuildConsistency() {

        final var builder = new TestStringBuilder("consistent");
        final var result1 = builder.build();
        final var result2 = builder.build();

        assertEquals(result1, result2);
    }

    @Test
    void testBuildWithEmptyString() {

        final var builder = new TestStringBuilder("");
        final var result = builder.build();

        assertNotNull(result);
        assertEquals("", result);
    }

    private record TestStringBuilder(String value) implements Builder<String> {

        @Override
        public String build() {
            return value;
        }
    }

    private record TestIntegerBuilder(Integer value) implements Builder<Integer> {

        @Override
        public Integer build() {
            return value;
        }
    }

    private record TestObjectBuilder(TestData data) implements Builder<TestData> {

        @Override
        public TestData build() {
            return data;
        }
    }

    private record TestData(String name, int value) {

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if ((obj == null) || (getClass() != obj.getClass())) {
                return false;
            }
            final var testData = (TestData) obj;
            return (value == testData.value) && (Objects.equals(name, testData.name));
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }
    }
}
