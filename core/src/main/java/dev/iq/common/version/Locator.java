package dev.iq.common.version;

import dev.iq.common.annotation.Stable;

/**
 * Uniquely locates a versioned item.  The item itself is identified by its Uid.  Each item then
 * contains one or more versions, starting at one.  Each new version increments by one.
 *
 * Locators are equal when their Uid and version are the equal.
 */
@Stable
public record Locator(Uid id, int version) {

    /** Starting version number. */
    private static final int FIRST_VERSION = 1;

    /** Create a new, random locator with version one. */
    public static Locator generate() {

        return new Locator(NanoId.generate(), FIRST_VERSION);
    }

    /** Create the first version for a given Uid (starts with version one). */
    public static Locator first(final Uid id) {

        return new Locator(id, FIRST_VERSION);
    }

    /** Increment the current Uid's version by one. */
    public Locator increment() {

        return new Locator(id, version + 1);
    }
}
