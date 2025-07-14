package dev.iq.common.version;

import java.time.Instant;
import java.util.Optional;

/**
 * Represents a versioned item that can be located by a unique NanoId and version number.
 */
public interface Versioned {

    /**
     * Returns the unique locator for this versioned item.
     */
    Locator locator();

    /**
     * Returns the timestamp when this version was created.
     */
    Instant created();

    /**
     * Returns the timestamp when this version expired, if applicable.
     */
    Optional<Instant> expired();

    /**
     * Checks if an identifiable item is active at a given timestamp.
     */
    static boolean isActiveAt(final Instant timestamp, final Versioned e) {

        final var created = e.created();
        final var expired = e.expired();
        return !created.isAfter(timestamp) && (expired.isEmpty() || expired.get().isAfter(timestamp));
    }

    /**
     * Compares two identifiable items for equality based on their locators.
     */
    static boolean equals(final Versioned source, final Versioned target) {

        return source.locator().equals(target.locator());
    }

    /**
     * Computes hash code for an identifiable item based on its locator.
     */
    static int hashCode(final Versioned target) {

        return target.locator().hashCode();
    }
}
