/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.fp;

import dev.iq.common.error.IoException;
import dev.iq.common.log.Log;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests the various methods of the Try type. */
final class IoTest {

    /**
     * Helper method to always throw an exception.
     *
     * @throws IOException Test exception
     */
    private static void throwException() throws IOException {

        throw new IOException("Testing");
    }

    /**
     * Helper method to always throw an exception.
     *
     * @param message Error message
     * @throws IOException Test exception
     */
    private static void throwException(final String message) throws IOException {

        throw new IOException(message);
    }

    /**
     * Helper method to always throw an exception.
     *
     * @param message Error message
     * @throws IOException Test exception
     */
    private static Integer throwReturnException(final String message) throws IOException {

        throw new IOException(message);
    }

    /** Tests runnable methods. */
    @Test
    void testVoid() {

        Assertions.assertThrows(IoException.class, () -> Io.withVoid(IoTest::throwException));
        Assertions.assertDoesNotThrow(
                () -> Io.withVoid(IoTest::throwException, e -> Log.error(IoTest.class, () -> "testing", e)));
    }

    /** Tests function methods. */
    @Test
    void testReturn() {

        Assertions.assertThrows(IoException.class, () -> Io.withReturn(() -> throwReturnException("message")));
        Assertions.assertEquals(42, Io.withReturn(() -> throwReturnException("message"), e -> 42));
        Assertions.assertEquals(42, Io.withReturn(() -> 42));
    }
}
