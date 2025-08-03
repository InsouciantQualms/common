package dev.iq.common.version;

import dev.iq.common.annotation.Stable;

/**
 * Uniquely locates a versioned item.  The item itself is identified by its NanoId.  Each item then
 * contains one or more versions, starting at one.  Each new version increments by one.
 *
 * Locators are equal when their NanoId and version are the equal.
 */
@Stable
public record Locator(NanoId id, int version) {

    /** Starting version number. */
    private static final int FIRST_VERSION = 1;

    /** Create a new, random locator with version one. */
    public static Locator generate() {

        return new Locator(NanoId.generate(), FIRST_VERSION);
    }

    /** Create the version version for a given NanoID (starts with version one. */
    public static Locator first(final NanoId id) {

        return new Locator(id, FIRST_VERSION);
    }

    /** Increment the current NanoID's version by one. */
    public Locator increment() {

        return new Locator(id, version + 1);
    }
}
