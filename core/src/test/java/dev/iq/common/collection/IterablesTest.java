/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Vector;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Iterables utility class covering immutable collection operations and conversions.
 */
final class IterablesTest {

    @Test
    void testImmutableWithList() {

        final var mutableList = new ArrayList<String>();
        mutableList.add("one");
        mutableList.add("two");
        mutableList.add("three");

        final var immutableIterable = Iterables.immutable(mutableList);

        assertNotNull(immutableIterable);
        assertIterableEquals(List.of("one", "two", "three"), immutableIterable);

        // Verify original list can be modified without affecting immutable copy
        mutableList.add("four");
        assertIterableEquals(List.of("one", "two", "three"), immutableIterable);
    }

    @Test
    void testImmutableWithSet() {

        final var mutableSet = new HashSet<Integer>();
        mutableSet.add(1);
        mutableSet.add(2);
        mutableSet.add(3);

        final var immutableIterable = Iterables.immutable(mutableSet);

        assertNotNull(immutableIterable);
        assertEquals(3, getSize(immutableIterable));
        assertTrue(contains(immutableIterable, 1));
        assertTrue(contains(immutableIterable, 2));
        assertTrue(contains(immutableIterable, 3));
    }

    @Test
    void testImmutableWithEmptyCollection() {

        final var emptyList = new ArrayList<String>();
        final var immutableIterable = Iterables.immutable(emptyList);

        assertNotNull(immutableIterable);
        assertFalse(immutableIterable.iterator().hasNext());
    }

    @Test
    void testOfWithMultipleElements() {

        final var iterable = Iterables.of("first", "second", "third");

        assertNotNull(iterable);
        assertIterableEquals(List.of("first", "second", "third"), iterable);
    }

    @Test
    void testOfWithSingleElement() {

        final var iterable = Iterables.of("single");

        assertNotNull(iterable);
        assertIterableEquals(List.of("single"), iterable);
    }

    @Test
    void testOfWithNoElements() {

        final var iterable = Iterables.of();

        assertNotNull(iterable);
        assertFalse(iterable.iterator().hasNext());
    }

    @Test
    void testOfDoesNotSupportNullElements() {

        // Iterables.of() uses List.of() which doesn't allow null values
        assertThrows(NullPointerException.class, () -> Iterables.of("first", null, "third"));
    }

    @Test
    void testOfWithDifferentTypes() {

        final var iterable = Iterables.of(1, 2, 3);

        assertNotNull(iterable);
        assertIterableEquals(List.of(1, 2, 3), iterable);
    }

    @Test
    void testSortWithComparator() {

        final var original = List.of("zebra", "apple", "banana");
        final var sorted = Iterables.sort(original, String::compareTo);

        assertNotNull(sorted);
        assertIterableEquals(List.of("apple", "banana", "zebra"), sorted);

        // Verify original is unchanged
        assertIterableEquals(List.of("zebra", "apple", "banana"), original);
    }

    @Test
    void testSortWithReverseComparator() {

        final var original = List.of(1, 3, 2, 5, 4);
        final var sorted = Iterables.sort(original, Comparator.reverseOrder());

        assertNotNull(sorted);
        assertIterableEquals(List.of(5, 4, 3, 2, 1), sorted);
    }

    @Test
    void testSortWithEmptyIterable() {

        final var empty = List.<String>of();
        final var sorted = Iterables.sort(empty, String::compareTo);

        assertNotNull(sorted);
        assertFalse(sorted.iterator().hasNext());
    }

    @Test
    void testSortWithSingleElement() {

        final var single = List.of("only");
        final var sorted = Iterables.sort(single, String::compareTo);

        assertNotNull(sorted);
        assertIterableEquals(List.of("only"), sorted);
    }

    @Test
    void testFromEnumeration() {

        final var vector = new Vector<String>();
        vector.add("first");
        vector.add("second");
        vector.add("third");

        final var enumeration = vector.elements();
        final var iterable = Iterables.from(enumeration);

        assertNotNull(iterable);
        assertIterableEquals(List.of("first", "second", "third"), iterable);
    }

    @Test
    void testFromEmptyEnumeration() {

        final var vector = new Vector<String>();
        final var enumeration = vector.elements();
        final var iterable = Iterables.from(enumeration);

        assertNotNull(iterable);
        assertFalse(iterable.iterator().hasNext());
    }

    @Test
    void testFromIterator() {

        final var list = List.of("alpha", "beta", "gamma");
        final var iterator = list.iterator();
        final var iterable = Iterables.from(iterator);

        assertNotNull(iterable);
        assertIterableEquals(List.of("alpha", "beta", "gamma"), iterable);
    }

    @Test
    void testFromEmptyIterator() {

        final var emptyList = List.<String>of();
        final var iterator = emptyList.iterator();
        final var iterable = Iterables.from(iterator);

        assertNotNull(iterable);
        assertFalse(iterable.iterator().hasNext());
    }

    @Test
    void testFromIteratorWithNulls() {

        final var list = new ArrayList<String>();
        list.add("first");
        list.add(null);
        list.add("third");
        final var iterator = list.iterator();
        final var iterable = Iterables.from(iterator);

        assertNotNull(iterable);
        final var expected = new ArrayList<String>();
        expected.add("first");
        expected.add(null);
        expected.add("third");
        assertIterableEquals(expected, iterable);
    }

    @Test
    void testImmutableIsActuallyImmutable() {

        final var mutableList = new ArrayList<String>();
        mutableList.add("test");

        final var immutableIterable = Iterables.immutable(mutableList);

        // Verify we can't cast it back to a mutable collection
        assertThrows(UnsupportedOperationException.class, () -> {
            if (immutableIterable instanceof final List<String> list) {
                list.add("should fail");
            }
        });
    }

    @Test
    void testSortIsImmutable() {

        final var original = new ArrayList<String>();
        original.add("c");
        original.add("a");
        original.add("b");

        final var sorted = Iterables.sort(original, String::compareTo);

        // Verify we can't modify the sorted result
        assertThrows(UnsupportedOperationException.class, () -> {
            if (sorted instanceof final List<String> list) {
                list.add("should fail");
            }
        });
    }

    /** Helper method to get size of an iterable. */
    private static <T> int getSize(final Iterable<T> iterable) {
        var count = 0;
        for (final var ignored : iterable) {
            count++;
        }
        return count;
    }

    /** Helper method to check if an iterable contains a specific element. */
    private static <T> boolean contains(final Iterable<T> iterable, final T element) {
        for (final var item : iterable) {
            if (Objects.equals(item, element)) {
                return true;
            }
        }
        return false;
    }
}
