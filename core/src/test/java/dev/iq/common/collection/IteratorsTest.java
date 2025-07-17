/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.collection;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Iterators utility class covering enumeration conversion and safe element removal.
 */
public final class IteratorsTest {

    @Test
    public void testFromEnumeration() {

        final var vector = new Vector<String>();
        vector.add("first");
        vector.add("second");
        vector.add("third");

        final var enumeration = vector.elements();
        final var iterator = Iterators.from(enumeration);

        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertEquals("first", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("second", iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("third", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testFromEmptyEnumeration() {

        final var vector = new Vector<String>();
        final var enumeration = vector.elements();
        final var iterator = Iterators.from(enumeration);

        assertNotNull(iterator);
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testFromEnumerationWithNulls() {

        final var vector = new Vector<String>();
        vector.add("first");
        vector.add(null);
        vector.add("third");

        final var enumeration = vector.elements();
        final var iterator = Iterators.from(enumeration);

        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertEquals("first", iterator.next());
        assertTrue(iterator.hasNext());
        assertNull(iterator.next());
        assertTrue(iterator.hasNext());
        assertEquals("third", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testFromEnumerationNoSuchElement() {

        final var vector = new Vector<String>();
        final var enumeration = vector.elements();
        final var iterator = Iterators.from(enumeration);

        assertFalse(iterator.hasNext());
        assertThrows(NoSuchElementException.class, iterator::next);
    }

    @Test
    public void testRemoveWithMatchingElements() {

        final var list = new ArrayList<String>();
        list.add("apple");
        list.add("banana");
        list.add("cherry");
        list.add("date");

        final var iterator = list.iterator();
        final var removedCount = Iterators.remove(iterator, s -> s.startsWith("b") || s.startsWith("d"));

        assertEquals(2, removedCount);
        assertEquals(List.of("apple", "cherry"), list);
    }

    @Test
    public void testRemoveWithNoMatchingElements() {

        final var list = new ArrayList<String>();
        list.add("apple");
        list.add("banana");
        list.add("cherry");

        final var iterator = list.iterator();
        final var removedCount = Iterators.remove(iterator, s -> s.startsWith("z"));

        assertEquals(0, removedCount);
        assertEquals(List.of("apple", "banana", "cherry"), list);
    }

    @Test
    public void testRemoveAllElements() {

        final var list = new ArrayList<String>();
        list.add("apple");
        list.add("banana");
        list.add("cherry");

        final var iterator = list.iterator();
        final var removedCount = Iterators.remove(iterator, s -> true);

        assertEquals(3, removedCount);
        assertTrue(list.isEmpty());
    }

    @Test
    public void testRemoveWithEmptyIterator() {

        final var list = new ArrayList<String>();
        final var iterator = list.iterator();
        final var removedCount = Iterators.remove(iterator, s -> true);

        assertEquals(0, removedCount);
        assertTrue(list.isEmpty());
    }

    @Test
    public void testRemoveWithNullElements() {

        final var list = new ArrayList<String>();
        list.add("apple");
        list.add(null);
        list.add("banana");
        list.add(null);

        final var iterator = list.iterator();
        final var removedCount = Iterators.remove(iterator, Objects::isNull);

        assertEquals(2, removedCount);
        assertEquals(List.of("apple", "banana"), list);
    }

    @Test
    public void testRemoveWithComplexPredicate() {

        final var list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);

        final var iterator = list.iterator();
        final var removedCount = Iterators.remove(iterator, n -> (n % 2) == 0);

        assertEquals(3, removedCount);
        assertEquals(List.of(1, 3, 5), list);
    }

    @Test
    public void testRemoveIteratorNotSupported() {

        final var list = List.of("apple", "banana", "cherry");
        final var iterator = list.iterator();

        // List.of() returns an immutable list, so iterator.remove() should fail
        assertThrows(UnsupportedOperationException.class, () -> Iterators.remove(iterator, s -> s.equals("banana")));
    }

    @Test
    public void testRemoveWithCustomObjects() {

        final var list = new ArrayList<Person>();
        list.add(new Person("Alice", 25));
        list.add(new Person("Bob", 30));
        list.add(new Person("Charlie", 35));
        list.add(new Person("David", 40));

        final var iterator = list.iterator();
        final var removedCount = Iterators.remove(iterator, person -> person.age() > 30);

        assertEquals(2, removedCount);
        assertEquals(2, list.size());
        assertEquals("Alice", list.get(0).name());
        assertEquals("Bob", list.get(1).name());
    }

    @Test
    public void testRemovePredicateOrder() {

        final var list = new ArrayList<String>();
        list.add("a");
        list.add("bb");
        list.add("ccc");
        list.add("dddd");

        final var iterator = list.iterator();
        final var removedCount = Iterators.remove(iterator, s -> s.length() > 2);

        assertEquals(2, removedCount);
        assertEquals(List.of("a", "bb"), list);
    }

    @Test
    public void testFromEnumerationConsistency() {

        final var vector = new Vector<String>();
        vector.add("test");

        final var enumeration = vector.elements();
        final var iterator = Iterators.from(enumeration);

        // Test that the iterator reflects the enumeration state
        assertTrue(iterator.hasNext());
        assertEquals("test", iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testRemoveCountAccuracy() {

        final var list = new ArrayList<String>();
        for (var i = 0; i < 100; i++) {
            list.add("item" + i);
        }

        final var iterator = list.iterator();
        final var removedCount = Iterators.remove(iterator, s -> s.contains("1"));

        // Should remove items containing "1": 1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 21, 31, 41, 51, 61, 71, 81, 91
        assertEquals(19, removedCount);
        assertEquals(81, list.size());
    }

    /**
     * Simple test record for custom object testing.
     */
    private record Person(String name, int age) {}
}