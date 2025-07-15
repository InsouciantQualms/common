/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.collection;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the Arrays utility class covering array class conversion functionality.
 */
public final class ArraysTest {

    @Test
    public void testToArrayWithString() {

        final var stringClass = String.class;
        final var arrayClass = Arrays.toArray(stringClass);
        
        assertNotNull(arrayClass);
        assertEquals(String[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertEquals(String.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithInteger() {

        final var integerClass = Integer.class;
        final var arrayClass = Arrays.toArray(integerClass);
        
        assertNotNull(arrayClass);
        assertEquals(Integer[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertEquals(Integer.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithPrimitiveInt() {

        final var intClass = int.class;
        final var arrayClass = Arrays.toArray(intClass);
        
        assertNotNull(arrayClass);
        assertEquals(int[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertEquals(int.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithPrimitiveBoolean() {

        final var booleanClass = boolean.class;
        final var arrayClass = Arrays.toArray(booleanClass);
        
        assertNotNull(arrayClass);
        assertEquals(boolean[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertEquals(boolean.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithPrimitiveDouble() {

        final var doubleClass = double.class;
        final var arrayClass = Arrays.toArray(doubleClass);
        
        assertNotNull(arrayClass);
        assertEquals(double[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertEquals(double.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithCustomClass() {

        final var customClass = TestClass.class;
        final var arrayClass = Arrays.toArray(customClass);
        
        assertNotNull(arrayClass);
        assertEquals(TestClass[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertEquals(TestClass.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithObjectClass() {

        final var objectClass = Object.class;
        final var arrayClass = Arrays.toArray(objectClass);
        
        assertNotNull(arrayClass);
        assertEquals(Object[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertEquals(Object.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithInterfaceClass() {

        final var runnableClass = Runnable.class;
        final var arrayClass = Arrays.toArray(runnableClass);
        
        assertNotNull(arrayClass);
        assertEquals(Runnable[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertEquals(Runnable.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithGenericClass() {

        final var listClass = java.util.List.class;
        final var arrayClass = Arrays.toArray(listClass);
        
        assertNotNull(arrayClass);
        assertEquals(java.util.List[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertEquals(java.util.List.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayConsistency() {

        final var stringClass = String.class;
        final var arrayClass1 = Arrays.toArray(stringClass);
        final var arrayClass2 = Arrays.toArray(stringClass);
        
        assertEquals(arrayClass1, arrayClass2);
        assertSame(arrayClass1, arrayClass2);
    }

    @Test
    public void testToArrayWithAllPrimitives() {

        final var byteClass = Arrays.toArray(byte.class);
        final var shortClass = Arrays.toArray(short.class);
        final var charClass = Arrays.toArray(char.class);
        final var longClass = Arrays.toArray(long.class);
        final var floatClass = Arrays.toArray(float.class);
        
        assertEquals(byte[].class, byteClass);
        assertEquals(short[].class, shortClass);
        assertEquals(char[].class, charClass);
        assertEquals(long[].class, longClass);
        assertEquals(float[].class, floatClass);
    }

    /**
     * Simple test class for custom class testing.
     */
    private static final class TestClass {
        private final String value;

        TestClass(final String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }
}