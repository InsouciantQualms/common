/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.adt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

/** Tests for the Coded interface covering basic functionality. */
public final class CodedTest {

    @Test
    void testGetCodeWithString() {

        final var coded = new TestStringCoded("TEST_CODE");
        final var result = coded.code();

        assertNotNull(result);
        assertEquals("TEST_CODE", result);
    }

    @Test
    void testGetCodeWithInteger() {

        final var coded = new TestIntegerCoded(42);
        final var result = coded.code();

        assertNotNull(result);
        assertEquals(42, result);
    }

    @Test
    void testGetCodeWithNull() {

        final var coded = new TestStringCoded(null);
        final var result = coded.code();

        assertNull(result);
    }

    @Test
    void testGetCodeWithEnum() {

        final var coded = new TestEnumCoded(TestEnum.VALUE1);
        final var result = coded.code();

        assertNotNull(result);
        assertEquals(TestEnum.VALUE1, result);
    }

    @Test
    void testGetCodeConsistency() {

        final var coded = new TestStringCoded("CONSISTENT");
        final var result1 = coded.code();
        final var result2 = coded.code();

        assertEquals(result1, result2);
        assertSame(result1, result2);
    }

    @Test
    void testGetCodeWithEmptyString() {

        final var coded = new TestStringCoded("");
        final var result = coded.code();

        assertNotNull(result);
        assertEquals("", result);
    }

    @Test
    void testGetCodeWithDifferentTypes() {

        final var stringCoded = new TestStringCoded("abc");
        final var enumCoded = new TestEnumCoded(TestEnum.VALUE2);

        assertEquals("abc", stringCoded.code());
        final var intCoded = new TestIntegerCoded(123);
        assertEquals(123, intCoded.code());
        assertEquals(TestEnum.VALUE2, enumCoded.code());
    }

    private record TestStringCoded(String code) implements Coded<String> {}

    private record TestIntegerCoded(Integer code) implements Coded<Integer> {}

    private record TestEnumCoded(TestEnum code) implements Coded<TestEnum> {}

    private enum TestEnum {
        VALUE1,
        VALUE2
    }
}
