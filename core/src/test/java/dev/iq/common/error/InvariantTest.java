/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.error;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests for the Invariant utility class assertion methods. */
@SuppressWarnings({"ConstantValue", "TestMethodWithoutAssertion"})
final class InvariantTest {

    @Test
    public void testRequireTrueWithCondition() {

        Invariant.require(true, "This should not be thrown");
    }

    @Test
    public void testRequireWithFalseCondition() {

        final var message = "Test failure message";

        final var exception =
                Assertions.assertThrows(IllegalStateException.class, () -> Invariant.require(false, message));

        Assertions.assertEquals(message, exception.getMessage());
    }

    @Test
    public void testRequireWithComplexConditions() {

        final var message = "Complex condition failed";

        // Test with null check
        final String nullString = null;
        Assertions.assertThrows(IllegalStateException.class, () -> Invariant.require(nullString != null, message));

        // Test with array bounds
        final var array = new int[5];
        Assertions.assertThrows(IllegalStateException.class, () -> Invariant.require(array.length > 10, message));
    }

    @Test
    public void testFail() {

        final var message = "This should always fail";

        final var error = Assertions.assertThrows(AssertionError.class, () -> Invariant.fail(message));

        Assertions.assertEquals(message, error.getMessage());
    }

    @Test
    public void testFailWithDifferentMessages() {

        final var message1 = "First failure";
        final var message2 = "Second failure";

        final var error1 = Assertions.assertThrows(AssertionError.class, () -> Invariant.fail(message1));

        final var error2 = Assertions.assertThrows(AssertionError.class, () -> Invariant.fail(message2));

        Assertions.assertEquals(message1, error1.getMessage());
        Assertions.assertEquals(message2, error2.getMessage());
    }

    @Test
    public void testFailWithNullMessage() {

        final var error = Assertions.assertThrows(AssertionError.class, () -> Invariant.fail(null));

        // AssertionError converts null to "null" string
        Assertions.assertEquals("null", error.getMessage());
    }

    @Test
    public void testFailWithEmptyMessage() {

        final var message = "";

        final var error = Assertions.assertThrows(AssertionError.class, () -> Invariant.fail(message));

        Assertions.assertEquals(message, error.getMessage());
    }

    @Test
    public void testRequireTrueVsAssertExceptionTypes() {

        final var message = "Test message";

        // Verify requireTrue throws IllegalStateException
        Assertions.assertThrows(IllegalStateException.class, () -> Invariant.require(false, message));
    }
}
