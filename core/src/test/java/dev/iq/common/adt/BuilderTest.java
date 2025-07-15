/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.adt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Builder interface covering basic functionality.
 */
public final class BuilderTest {

    @Test
    public void testBuildWithString() {

        final var builder = new TestStringBuilder("test");
        final var result = builder.build();
        
        assertNotNull(result);
        assertEquals("test", result);
    }

    @Test
    public void testBuildWithInteger() {

        final var builder = new TestIntegerBuilder(42);
        final var result = builder.build();
        
        assertNotNull(result);
        assertEquals(42, result);
    }

    @Test
    public void testBuildWithNull() {

        final var builder = new TestStringBuilder(null);
        final var result = builder.build();
        
        assertNull(result);
    }

    @Test
    public void testBuildWithComplexObject() {

        final var data = new TestData("name", 123);
        final var builder = new TestObjectBuilder(data);
        final var result = builder.build();
        
        assertNotNull(result);
        assertEquals(data, result);
        assertEquals("name", result.getName());
        assertEquals(123, result.getValue());
    }

    @Test
    public void testBuildConsistency() {

        final var builder = new TestStringBuilder("consistent");
        final var result1 = builder.build();
        final var result2 = builder.build();
        
        assertEquals(result1, result2);
    }

    @Test
    public void testBuildWithEmptyString() {

        final var builder = new TestStringBuilder("");
        final var result = builder.build();
        
        assertNotNull(result);
        assertEquals("", result);
    }

    private static final class TestStringBuilder implements Builder<String> {
        private final String value;

        TestStringBuilder(final String value) {
            this.value = value;
        }

        @Override
        public String build() {
            return value;
        }
    }

    private static final class TestIntegerBuilder implements Builder<Integer> {
        private final Integer value;

        TestIntegerBuilder(final Integer value) {
            this.value = value;
        }

        @Override
        public Integer build() {
            return value;
        }
    }

    private static final class TestObjectBuilder implements Builder<TestData> {
        private final TestData data;

        TestObjectBuilder(final TestData data) {
            this.data = data;
        }

        @Override
        public TestData build() {
            return data;
        }
    }

    private static final class TestData {
        private final String name;
        private final int value;

        TestData(final String name, final int value) {
            this.name = name;
            this.value = value;
        }

        String getName() {
            return name;
        }

        int getValue() {
            return value;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            final var testData = (TestData) obj;
            return value == testData.value && 
                   (name != null ? name.equals(testData.name) : testData.name == null);
        }

        @Override
        public int hashCode() {
            return (name != null ? name.hashCode() : 0) * 31 + value;
        }
    }
}