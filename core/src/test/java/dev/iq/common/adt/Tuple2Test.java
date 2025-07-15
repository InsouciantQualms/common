/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.adt;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Tuple2 record covering basic functionality, equality, and object methods.
 */
public final class Tuple2Test {

    @Test
    public void testBasicCreation() {

        final var tuple = new Tuple2<>("hello", 42);
        
        assertEquals("hello", tuple._1());
        assertEquals(42, tuple._2());
    }

    @Test
    public void testWithNullValues() {

        final var tuple = new Tuple2<String, Integer>(null, null);
        
        assertNull(tuple._1());
        assertNull(tuple._2());
    }

    @Test
    public void testEquality() {

        final var tuple1 = new Tuple2<>("hello", 42);
        final var tuple2 = new Tuple2<>("hello", 42);
        final var tuple3 = new Tuple2<>("world", 42);
        final var tuple4 = new Tuple2<>("hello", 24);
        
        assertEquals(tuple1, tuple2);
        assertNotEquals(tuple1, tuple3);
        assertNotEquals(tuple1, tuple4);
        assertNotEquals(tuple3, tuple4);
    }

    @Test
    public void testEqualityWithNulls() {

        final var tuple1 = new Tuple2<String, Integer>(null, 42);
        final var tuple2 = new Tuple2<String, Integer>(null, 42);
        final var tuple3 = new Tuple2<String, Integer>("hello", null);
        final var tuple4 = new Tuple2<String, Integer>("hello", null);
        final var tuple5 = new Tuple2<String, Integer>(null, null);
        final var tuple6 = new Tuple2<String, Integer>(null, null);
        
        assertEquals(tuple1, tuple2);
        assertEquals(tuple3, tuple4);
        assertEquals(tuple5, tuple6);
        assertNotEquals(tuple1, tuple3);
        assertNotEquals(tuple1, tuple5);
        assertNotEquals(tuple3, tuple5);
    }

    @Test
    public void testHashCode() {

        final var tuple1 = new Tuple2<>("hello", 42);
        final var tuple2 = new Tuple2<>("hello", 42);
        final var tuple3 = new Tuple2<>("world", 42);
        
        assertEquals(tuple1.hashCode(), tuple2.hashCode());
        assertNotEquals(tuple1.hashCode(), tuple3.hashCode());
    }

    @Test
    public void testHashCodeWithNulls() {

        final var tuple1 = new Tuple2<String, Integer>(null, 42);
        final var tuple2 = new Tuple2<String, Integer>(null, 42);
        final var tuple3 = new Tuple2<String, Integer>("hello", null);
        final var tuple4 = new Tuple2<String, Integer>(null, null);
        
        assertEquals(tuple1.hashCode(), tuple2.hashCode());
        assertNotEquals(tuple1.hashCode(), tuple3.hashCode());
        assertNotEquals(tuple1.hashCode(), tuple4.hashCode());
    }

    @Test
    public void testToString() {

        final var tuple = new Tuple2<>("hello", 42);
        final var result = tuple.toString();
        
        assertNotNull(result);
        assertTrue(result.contains("hello"));
        assertTrue(result.contains("42"));
    }

    @Test
    public void testToStringWithNulls() {

        final var tuple1 = new Tuple2<String, Integer>(null, 42);
        final var tuple2 = new Tuple2<String, Integer>("hello", null);
        final var tuple3 = new Tuple2<String, Integer>(null, null);
        
        final var result1 = tuple1.toString();
        final var result2 = tuple2.toString();
        final var result3 = tuple3.toString();
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);
        assertTrue(result1.contains("null"));
        assertTrue(result2.contains("null"));
        assertTrue(result3.contains("null"));
    }

    @Test
    public void testDifferentTypes() {

        final var stringIntTuple = new Tuple2<>("test", 123);
        final var booleanDoubleTuple = new Tuple2<>(true, 3.14);
        final var listArrayTuple = new Tuple2<>(new int[]{1, 2, 3}, "array");
        
        assertEquals("test", stringIntTuple._1());
        assertEquals(123, stringIntTuple._2());
        
        assertEquals(true, booleanDoubleTuple._1());
        assertEquals(3.14, booleanDoubleTuple._2());
        
        assertArrayEquals(new int[]{1, 2, 3}, listArrayTuple._1());
        assertEquals("array", listArrayTuple._2());
    }

    @Test
    public void testSameType() {

        final var tuple = new Tuple2<>("first", "second");
        
        assertEquals("first", tuple._1());
        assertEquals("second", tuple._2());
    }

    @Test
    public void testNestedTuples() {

        final var innerTuple = new Tuple2<>("inner", 42);
        final var outerTuple = new Tuple2<>(innerTuple, "outer");
        
        assertEquals(innerTuple, outerTuple._1());
        assertEquals("outer", outerTuple._2());
        assertEquals("inner", outerTuple._1()._1());
        assertEquals(42, outerTuple._1()._2());
    }

    @Test
    public void testComplexObjects() {

        final var person = new Person("John", 30);
        final var address = new Address("123 Main St", "Anytown");
        final var tuple = new Tuple2<>(person, address);
        
        assertEquals(person, tuple._1());
        assertEquals(address, tuple._2());
        assertEquals("John", tuple._1().name());
        assertEquals(30, tuple._1().age());
        assertEquals("123 Main St", tuple._2().street());
        assertEquals("Anytown", tuple._2().city());
    }

    @Test
    public void testRecordMethods() {

        final var tuple = new Tuple2<>("test", 42);
        
        // Test that standard record methods work
        assertNotNull(tuple.toString());
        assertTrue(tuple.equals(new Tuple2<>("test", 42)));
        assertEquals(tuple.hashCode(), new Tuple2<>("test", 42).hashCode());
    }

    /**
     * Simple test record for complex object testing.
     */
    private record Person(String name, int age) {}

    /**
     * Simple test record for complex object testing.
     */
    private record Address(String street, String city) {}
}