package dev.iq.common.session;

/** Factory for creating graph sessions. */
@FunctionalInterface
public interface SessionFactory {

    /** Creates a new session. */
    Session create();
}
