package dev.iq.common.persist;

/**
 * Factory for creating graph sessions.
 */
@FunctionalInterface
public interface SessionFactory {

    /**
     * Creates a new session.
     */
    Session create();
}
