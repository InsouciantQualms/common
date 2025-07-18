/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.adt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

/** Tests for the Typed class covering type reference functionality. */
public final class TypedTest {

    @Test
    public void testTyped1Creation() {

        final var typed = Typed.of("test", String.class);

        assertNotNull(typed);
        assertEquals("test", typed.getFirst());
        assertNotNull(typed.getTypeA());
    }

    @Test
    public void testTyped1WithNull() {

        final var typed = Typed.of(null, String.class);

        assertNotNull(typed);
        assertNull(typed.getFirst());
        assertNotNull(typed.getTypeA());
    }

    @Test
    public void testTyped1WithInteger() {

        final var typed = Typed.of(42, Integer.class);

        assertNotNull(typed);
        assertEquals(42, typed.getFirst());
        assertNotNull(typed.getTypeA());
    }

    @Test
    public void testTyped1WithNullClassThrows() {

        assertThrows(NullPointerException.class, () -> Typed.of("test", null));
    }

    @Test
    public void testTyped2Creation() {

        final var typed = Typed.of("hello", 123, String.class, Integer.class);

        assertNotNull(typed);
        assertEquals("hello", typed.getFirst());
        assertEquals(123, typed.getSecond());
        assertNotNull(typed.getTypeA());
        assertNotNull(typed.getTypeB());
    }

    @Test
    public void testTyped2WithNulls() {

        final var typed = Typed.of(null, null, String.class, Integer.class);

        assertNotNull(typed);
        assertNull(typed.getFirst());
        assertNull(typed.getSecond());
        assertNotNull(typed.getTypeA());
        assertNotNull(typed.getTypeB());
    }

    @Test
    public void testTyped2WithNullClassThrows() {

        assertThrows(NullPointerException.class, () -> Typed.of("hello", 123, null, Integer.class));

        assertThrows(NullPointerException.class, () -> Typed.of("hello", 123, String.class, null));
    }

    @Test
    public void testTyped3Creation() {

        final var typed = Typed.of("hello", 123, true, String.class, Integer.class, Boolean.class);

        assertNotNull(typed);
        assertEquals("hello", typed.getFirst());
        assertEquals(123, typed.getSecond());
        assertTrue(typed.getThird());
        assertNotNull(typed.getTypeA());
        assertNotNull(typed.getTypeB());
        assertNotNull(typed.getTypeC());
    }

    @Test
    public void testTyped3WithNulls() {

        final var typed = Typed.of(null, null, null, String.class, Integer.class, Boolean.class);

        assertNotNull(typed);
        assertNull(typed.getFirst());
        assertNull(typed.getSecond());
        assertNull(typed.getThird());
        assertNotNull(typed.getTypeA());
        assertNotNull(typed.getTypeB());
        assertNotNull(typed.getTypeC());
    }

    @Test
    public void testTyped3WithNullClassThrows() {

        assertThrows(
                NullPointerException.class, () -> Typed.of("hello", 123, true, null, Integer.class, Boolean.class));

        assertThrows(NullPointerException.class, () -> Typed.of("hello", 123, true, String.class, null, Boolean.class));

        assertThrows(NullPointerException.class, () -> Typed.of("hello", 123, true, String.class, Integer.class, null));
    }

    @Test
    public void testTyped1GetMethods() {

        final var typed = Typed.of("test", String.class);

        assertEquals("test", typed.getFirst());
        assertNotNull(typed.getTypeA());
    }

    @Test
    public void testTyped2GetMethods() {

        final var typed = Typed.of("hello", 123, String.class, Integer.class);

        assertEquals("hello", typed.getFirst());
        assertEquals(123, typed.getSecond());
        assertNotNull(typed.getTypeA());
        assertNotNull(typed.getTypeB());
    }

    @Test
    public void testTyped3GetMethods() {

        final var typed = Typed.of("hello", 123, true, String.class, Integer.class, Boolean.class);

        assertEquals("hello", typed.getFirst());
        assertEquals(123, typed.getSecond());
        assertTrue(typed.getThird());
        assertNotNull(typed.getTypeA());
        assertNotNull(typed.getTypeB());
        assertNotNull(typed.getTypeC());
    }

    @Test
    public void testTypedWithDifferentTypes() {

        final var typed1 = Typed.of(42.0, Double.class);
        final var typed2 = Typed.of("test", true, String.class, Boolean.class);
        final var typed3 = Typed.of(1, 2.0, "three", Integer.class, Double.class, String.class);

        assertEquals(42.0, typed1.getFirst());
        assertEquals("test", typed2.getFirst());
        assertTrue(typed2.getSecond());
        assertEquals(1, typed3.getFirst());
        assertEquals(2.0, typed3.getSecond());
        assertEquals("three", typed3.getThird());
    }

    @Test
    public void testTypedWithComplexTypes() {

        final var list = List.of("a", "b");
        final var set = Set.of(1, 2);
        final var typed = Typed.of(list, set, List.class, Set.class);

        assertNotNull(typed);
        assertEquals(list, typed.getFirst());
        assertEquals(set, typed.getSecond());
        assertEquals(2, typed.getFirst().size());
        assertEquals(2, typed.getSecond().size());
    }

    @Test
    public void testTypedFactoryMethods() {

        final var typed1 = Typed.of("single", String.class);
        final var typed2 = Typed.of("first", "second", String.class, String.class);
        final var typed3 = Typed.of("first", "second", "third", String.class, String.class, String.class);

        assertNotNull(typed1);
        assertNotNull(typed2);
        assertNotNull(typed3);

        assertEquals("single", typed1.getFirst());
        assertEquals("first", typed2.getFirst());
        assertEquals("second", typed2.getSecond());
        assertEquals("first", typed3.getFirst());
        assertEquals("second", typed3.getSecond());
        assertEquals("third", typed3.getThird());
    }

    @Test
    public void testTypedTypeInformationExists() {

        final var typed1 = Typed.of("test", String.class);
        final var typed2 = Typed.of("hello", 123, String.class, Integer.class);
        final var typed3 = Typed.of("hello", 123, true, String.class, Integer.class, Boolean.class);

        assertNotNull(typed1.getTypeA());
        assertNotNull(typed2.getTypeA());
        assertNotNull(typed2.getTypeB());
        assertNotNull(typed3.getTypeA());
        assertNotNull(typed3.getTypeB());
        assertNotNull(typed3.getTypeC());
    }
}
