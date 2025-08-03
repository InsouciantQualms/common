package dev.iq.common.version;

/**
 * Utility methods to work with Locators.
 */
public final class Locators {

    /** Type contains only static methods. */
    private Locators() {}

    /** Compares two identifiable items for equality based on their locators. */
    static boolean equals(final Locateable source, final Locateable target) {

        return source.locator().equals(target.locator());
    }

    /** Computes hash code for an identifiable item based on its locator. */
    static int hashCode(final Locateable target) {

        return target.locator().hashCode();
    }
}
