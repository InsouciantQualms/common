/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Streams utility class covering conversions from various collection types to
 * streams.
 */
final class StreamsTest {

    @Test
    void testOfWithBytes() {

        final var bytes = new byte[] {1, 2, 3, 4, 5};
        final var stream = Streams.of(bytes);

        assertNotNull(stream);
        final var result = stream.toList();
        assertEquals(5, result.size());
        assertEquals(Byte.valueOf((byte) 1), result.get(0));
        assertEquals(Byte.valueOf((byte) 2), result.get(1));
        assertEquals(Byte.valueOf((byte) 3), result.get(2));
        assertEquals(Byte.valueOf((byte) 4), result.get(3));
        assertEquals(Byte.valueOf((byte) 5), result.get(4));
    }

    @Test
    void testOfWithEmptyByteArray() {

        final var bytes = new byte[0];
        final var stream = Streams.of(bytes);

        assertNotNull(stream);
        final var result = stream.toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void testOfWithSingleByte() {

        final var bytes = new byte[] {42};
        final var stream = Streams.of(bytes);

        assertNotNull(stream);
        final var result = stream.toList();
        assertEquals(1, result.size());
        assertEquals(Byte.valueOf((byte) 42), result.getFirst());
    }

    @Test
    void testOfWithNegativeBytes() {

        final var bytes = new byte[] {-1, -2, -3};
        final var stream = Streams.of(bytes);

        assertNotNull(stream);
        final var result = stream.toList();
        assertEquals(3, result.size());
        assertEquals(Byte.valueOf((byte) -1), result.get(0));
        assertEquals(Byte.valueOf((byte) -2), result.get(1));
        assertEquals(Byte.valueOf((byte) -3), result.get(2));
    }

    @Test
    void testFromEnumeration() {

        final var vector = new Vector<String>();
        vector.add("first");
        vector.add("second");
        vector.add("third");

        final var enumeration = vector.elements();
        final var stream = Streams.from(enumeration);

        assertNotNull(stream);
        final var result = stream.collect(Collectors.toList());
        assertEquals(List.of("first", "second", "third"), result);
    }

    @Test
    void testFromEmptyEnumeration() {

        final var vector = new Vector<String>();
        final var enumeration = vector.elements();
        final var stream = Streams.from(enumeration);

        assertNotNull(stream);
        final var result = stream.toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromEnumerationWithNulls() {

        final var vector = new Vector<String>();
        vector.add("first");
        vector.add(null);
        vector.add("third");

        final var enumeration = vector.elements();
        final var stream = Streams.from(enumeration);

        assertNotNull(stream);
        final var result = stream.collect(Collectors.toList());
        final var expected = new ArrayList<String>();
        expected.add("first");
        expected.add(null);
        expected.add("third");
        assertEquals(expected, result);
    }

    @Test
    void testFromIterator() {

        final var list = List.of("alpha", "beta", "gamma");
        final var iterator = list.iterator();
        final var stream = Streams.from(iterator);

        assertNotNull(stream);
        final var result = stream.collect(Collectors.toList());
        assertEquals(List.of("alpha", "beta", "gamma"), result);
    }

    @Test
    void testFromEmptyIterator() {

        final var emptyList = List.<String>of();
        final var iterator = emptyList.iterator();
        final var stream = Streams.from(iterator);

        assertNotNull(stream);
        final var result = stream.toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromIteratorWithNulls() {

        final var list = new ArrayList<String>();
        list.add("first");
        list.add(null);
        list.add("third");
        final var iterator = list.iterator();
        final var stream = Streams.from(iterator);

        assertNotNull(stream);
        final var result = stream.collect(Collectors.toList());
        final var expected = new ArrayList<String>();
        expected.add("first");
        expected.add(null);
        expected.add("third");
        assertEquals(expected, result);
    }

    @Test
    void testFromIterable() {

        final var iterable = List.of("one", "two", "three");
        final var stream = Streams.from(iterable);

        assertNotNull(stream);
        final var result = stream.collect(Collectors.toList());
        assertEquals(List.of("one", "two", "three"), result);
    }

    @Test
    void testFromEmptyIterable() {

        final var iterable = List.<String>of();
        final var stream = Streams.from(iterable);

        assertNotNull(stream);
        final var result = stream.toList();
        assertTrue(result.isEmpty());
    }

    @Test
    void testFromIterableWithNulls() {

        final var iterable = new ArrayList<String>();
        iterable.add("first");
        iterable.add(null);
        iterable.add("third");
        final var stream = Streams.from(iterable);

        assertNotNull(stream);
        final var result = stream.collect(Collectors.toList());
        final var expected = new ArrayList<String>();
        expected.add("first");
        expected.add(null);
        expected.add("third");
        assertEquals(expected, result);
    }

    @Test
    void testFromSet() {

        final var set = Set.of("apple", "banana", "cherry");
        final var stream = Streams.from(set);

        assertNotNull(stream);
        final var result = stream.collect(Collectors.toSet());
        assertEquals(set, result);
    }

    @Test
    void testStreamOperationsOnByteStream() {

        final var bytes = new byte[] {1, 2, 3, 4, 5};
        final var stream = Streams.of(bytes);

        final var result =
                stream.filter(b -> (b % 2) == 1).map(b -> (byte) (b * 2)).toList();

        assertEquals(3, result.size());
        assertEquals(Byte.valueOf((byte) 2), result.get(0));
        assertEquals(Byte.valueOf((byte) 6), result.get(1));
        assertEquals(Byte.valueOf((byte) 10), result.get(2));
    }

    @Test
    void testStreamOperationsOnIterableStream() {

        final var iterable = List.of("apple", "banana", "cherry", "date");
        final var stream = Streams.from(iterable);

        final var result = stream.filter(s -> s.length() > 4)
                .map(String::toUpperCase)
                .sorted()
                .collect(Collectors.toList());

        assertEquals(List.of("APPLE", "BANANA", "CHERRY"), result);
    }

    @Test
    void testStreamOperationsOnEnumerationStream() {

        final var vector = new Vector<Integer>();
        vector.add(1);
        vector.add(2);
        vector.add(3);
        vector.add(4);
        vector.add(5);

        final var enumeration = vector.elements();
        final var stream = Streams.from(enumeration);

        final var result = stream.filter(n -> (n % 2) == 0).map(n -> n * n).collect(Collectors.toList());

        assertEquals(List.of(4, 16), result);
    }

    @Test
    void testStreamIsNotParallel() {

        final var iterable = List.of("test1", "test2", "test3");
        final var stream = Streams.from(iterable);

        assertNotNull(stream);
        assertFalse(stream.isParallel());
    }

    @Test
    void testByteStreamWithMaxAndMinValues() {

        final var bytes = new byte[] {Byte.MIN_VALUE, 0, Byte.MAX_VALUE};
        final var stream = Streams.of(bytes);

        assertNotNull(stream);
        final var result = stream.toList();
        assertEquals(3, result.size());
        assertEquals(Byte.valueOf(Byte.MIN_VALUE), result.get(0));
        assertEquals(Byte.valueOf((byte) 0), result.get(1));
        assertEquals(Byte.valueOf(Byte.MAX_VALUE), result.get(2));
    }

    @Test
    void testChainedConversions() {

        final var vector = new Vector<String>();
        vector.add("first");
        vector.add("second");
        vector.add("third");

        // Test enumeration -> iterator -> iterable -> stream
        final var enumeration = vector.elements();
        final var iterator = Iterators.from(enumeration);
        final var iterable = Iterables.from(iterator);
        final var stream = Streams.from(iterable);

        assertNotNull(stream);
        final var result = stream.collect(Collectors.toList());
        assertEquals(List.of("first", "second", "third"), result);
    }

    @Test
    void testLargeByteArray() {

        final var bytes = new byte[1000];
        for (var i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (i % 256);
        }

        final var stream = Streams.of(bytes);

        assertNotNull(stream);
        final var count = stream.count();
        assertEquals(1000, count);
    }

    @Test
    void testCustomObjectsInStream() {

        final var people = List.of(new Person("Alice", 25), new Person("Bob", 30), new Person("Charlie", 35));

        final var stream = Streams.from(people);

        assertNotNull(stream);
        final var names = stream.map(Person::name).collect(Collectors.toList());

        assertEquals(List.of("Alice", "Bob", "Charlie"), names);
    }

    /** Simple test record for custom object testing. */
    private record Person(String name, int age) {}
}
