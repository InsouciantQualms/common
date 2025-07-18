/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.collection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

/** Tests for the Arrays utility class covering array class conversion functionality. */
public final class ArraysTest {

    @Test
    public void testToArrayWithString() {

        final var stringClass = String.class;
        final var arrayClass = Arrays.toArray(stringClass);

        assertNotNull(arrayClass);
        assertSame(String[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertSame(String.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithInteger() {

        final var integerClass = Integer.class;
        final var arrayClass = Arrays.toArray(integerClass);

        assertNotNull(arrayClass);
        assertSame(Integer[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertSame(Integer.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithPrimitiveInt() {

        final var intClass = int.class;
        final var arrayClass = Arrays.toArray(intClass);

        assertNotNull(arrayClass);
        assertSame(int[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertSame(int.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithPrimitiveBoolean() {

        final var booleanClass = boolean.class;
        final var arrayClass = Arrays.toArray(booleanClass);

        assertNotNull(arrayClass);
        assertSame(boolean[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertSame(boolean.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithPrimitiveDouble() {

        final var doubleClass = double.class;
        final var arrayClass = Arrays.toArray(doubleClass);

        assertNotNull(arrayClass);
        assertSame(double[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertSame(double.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithCustomClass() {

        final var customClass = TestClass.class;
        final var arrayClass = Arrays.toArray(customClass);

        assertNotNull(arrayClass);
        assertSame(TestClass[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertSame(TestClass.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithObjectClass() {

        final var objectClass = Object.class;
        final var arrayClass = Arrays.toArray(objectClass);

        assertNotNull(arrayClass);
        assertSame(Object[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertSame(Object.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithInterfaceClass() {

        final var runnableClass = Runnable.class;
        final var arrayClass = Arrays.toArray(runnableClass);

        assertNotNull(arrayClass);
        assertSame(Runnable[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertSame(Runnable.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayWithGenericClass() {

        final var listClass = List.class;
        final var arrayClass = Arrays.toArray(listClass);

        assertNotNull(arrayClass);
        assertSame(List[].class, arrayClass);
        assertTrue(arrayClass.isArray());
        assertSame(List.class, arrayClass.getComponentType());
    }

    @Test
    public void testToArrayConsistency() {

        final var stringClass = String.class;
        final var arrayClass1 = Arrays.toArray(stringClass);
        final var arrayClass2 = Arrays.toArray(stringClass);

        assertSame(arrayClass1, arrayClass2);
        assertSame(arrayClass1, arrayClass2);
    }

    @Test
    public void testToArrayWithAllPrimitives() {

        final var byteClass = Arrays.toArray(byte.class);
        final var shortClass = Arrays.toArray(short.class);
        final var charClass = Arrays.toArray(char.class);
        final var longClass = Arrays.toArray(long.class);
        final var floatClass = Arrays.toArray(float.class);

        assertSame(byte[].class, byteClass);
        assertSame(short[].class, shortClass);
        assertSame(char[].class, charClass);
        assertSame(long[].class, longClass);
        assertSame(float[].class, floatClass);
    }

    /** Simple test class for custom class testing. */
    private record TestClass(String value) {}
}
