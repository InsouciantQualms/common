package dev.iq.common.error;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Verifies that a checked exception can be type-erased to an unchecked one. */
final class UncheckedTest {

    /** Ensures that a checked exception can be erased to an unchecked one. */
    @Test
    public void testUnchecked() {

        Assertions.assertThrows(IOException.class, () -> {
            try {
                problematic();
                Assertions.fail();
            } catch (final IOException e) {
                throw Unchecked.rethrow(e);
            }
        });
    }

    /** Helper method that always throws a checked exception. */
    private static void problematic() throws IOException {

        throw new IOException("Whoops");
    }
}
