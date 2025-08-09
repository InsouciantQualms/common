package dev.iq.common.version;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Interface to locate versioned data given an identifier (Uid).  All data will consist of one or
 * more versions (starting at one).  Only one version can be active at a time.  Implementations should
 * return versions in ascending version ID order (i.e., active if it exists will be last).
 */
public interface VersionedFinder<T extends Versioned> {

    /** Return all versions available (active and expired) for the specified ID. */
    List<T> findVersions(Uid id);

    /** Returns the active version (if available) for the specified ID. */
    Optional<T> findActive(Uid id);

    /** Find the exact version (active or expired) for the specified ID. */
    T find(Locator locator);

    /** Find a version (active or expired) for the specified ID at a given instant. */
    Optional<T> findAt(Uid id, Instant timestamp);
}
