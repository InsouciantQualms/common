package dev.iq.common.version;

/**
 * Indicates a type is defined by its Locator (NanoID and version).
 */
@FunctionalInterface
public interface Locateable {

    /** Returns the unique locator for this Locateable item. */
    Locator locator();
}
