/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.fp;

import dev.iq.common.error.UnexpectedException;
import dev.iq.common.log.Log;
import java.beans.IntrospectionException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests the various methods of the Try type. */
public final class TryTest {

    /** Helper method to always throw an exception. */
    private static void throwException() throws IntrospectionException {

        throw new IntrospectionException("Testing");
    }

    /**
     * Helper method to always throw an exception.
     *
     * @param message Error message
     * @throws IntrospectionException Test exception
     */
    private static void throwException(final String message) throws IntrospectionException {

        throw new IntrospectionException(message);
    }

    /**
     * Helper method to always throw an exception.
     *
     * @param message Error message
     * @throws IntrospectionException Test exception
     */
    private static Integer throwReturnException(final String message) throws IntrospectionException {

        throw new IntrospectionException(message);
    }

    /** Tests runnable methods. */
    @Test
    public void testVoid() {

        Assertions.assertThrows(UnexpectedException.class, () -> Try.withVoid(TryTest::throwException));
        Assertions.assertDoesNotThrow(
                () -> Try.withVoid(TryTest::throwException, e -> Log.error(TryTest.class, () -> "testing", e)));
    }

    /** Tests function methods. */
    @Test
    public void testReturn() {

        Assertions.assertThrows(UnexpectedException.class, () -> Try.withReturn(() -> throwReturnException("message")));
        Assertions.assertEquals(42, Try.withReturn(() -> throwReturnException("message"), e -> 42));
        Assertions.assertEquals(42, Try.withReturn(() -> 42));
    }
}
