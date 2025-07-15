/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.error;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Invariant utility class assertion methods.
 */
final class InvariantTest {

    @Test
    public void testRequireTrueWithTrueCondition() {

        // Should not throw any exception
        Invariant.requireTrue(true, "This should not be thrown");
        
        // Test with various true conditions
        Invariant.requireTrue(1 == 1, "One equals one");
        Invariant.requireTrue("test".equals("test"), "String equality");
        Invariant.requireTrue(5 > 3, "Five greater than three");
    }

    @Test
    public void testRequireTrueWithFalseCondition() {

        final var message = "Test failure message";
        
        final var exception = Assertions.assertThrows(IllegalStateException.class, () -> 
            Invariant.requireTrue(false, message)
        );
        
        Assertions.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testRequireTrueWithComplexConditions() {

        final var message = "Complex condition failed";
        
        // Test with null check
        final String nullString = null;
        Assertions.assertThrows(IllegalStateException.class, () -> 
            Invariant.requireTrue(nullString != null, message)
        );
        
        // Test with array bounds
        final var array = new int[5];
        Assertions.assertThrows(IllegalStateException.class, () -> 
            Invariant.requireTrue(array.length > 10, message)
        );
    }

    @Test
    public void testAssertTrueWithTrueCondition() {

        // Should not throw any exception
        Invariant.assertTrue(true, "This should not be thrown");
        
        // Test with various true conditions
        Invariant.assertTrue(1 == 1, "One equals one");
        Invariant.assertTrue("test".equals("test"), "String equality");
        Invariant.assertTrue(5 > 3, "Five greater than three");
    }

    @Test
    public void testAssertTrueWithFalseCondition() {

        final var message = "Test assertion failure message";
        
        final var error = Assertions.assertThrows(AssertionError.class, () -> 
            Invariant.assertTrue(false, message)
        );
        
        Assertions.assertEquals(message, error.getMessage());
    }

    @Test
    public void testAssertTrueWithComplexConditions() {

        final var message = "Complex assertion failed";
        
        // Test with null check
        final String nullString = null;
        Assertions.assertThrows(AssertionError.class, () -> 
            Invariant.assertTrue(nullString != null, message)
        );
        
        // Test with mathematical condition
        Assertions.assertThrows(AssertionError.class, () -> 
            Invariant.assertTrue(Math.PI > 4.0, message)
        );
    }

    @Test
    public void testFail() {

        final var message = "This should always fail";
        
        final var error = Assertions.assertThrows(AssertionError.class, () -> 
            Invariant.fail(message)
        );
        
        Assertions.assertEquals(message, error.getMessage());
    }

    @Test
    public void testFailWithDifferentMessages() {

        final var message1 = "First failure";
        final var message2 = "Second failure";
        
        final var error1 = Assertions.assertThrows(AssertionError.class, () -> 
            Invariant.fail(message1)
        );
        
        final var error2 = Assertions.assertThrows(AssertionError.class, () -> 
            Invariant.fail(message2)
        );
        
        Assertions.assertEquals(message1, error1.getMessage());
        Assertions.assertEquals(message2, error2.getMessage());
    }

    @Test
    public void testFailWithNullMessage() {

        final var error = Assertions.assertThrows(AssertionError.class, () -> 
            Invariant.fail(null)
        );
        
        // AssertionError converts null to "null" string
        Assertions.assertEquals("null", error.getMessage());
    }

    @Test
    public void testFailWithEmptyMessage() {

        final var message = "";
        
        final var error = Assertions.assertThrows(AssertionError.class, () -> 
            Invariant.fail(message)
        );
        
        Assertions.assertEquals(message, error.getMessage());
    }

    @Test
    public void testRequireTrueVsAssertTrueExceptionTypes() {

        final var message = "Test message";
        
        // Verify requireTrue throws IllegalStateException
        Assertions.assertThrows(IllegalStateException.class, () -> 
            Invariant.requireTrue(false, message)
        );
        
        // Verify assertTrue throws AssertionError
        Assertions.assertThrows(AssertionError.class, () -> 
            Invariant.assertTrue(false, message)
        );
    }
}