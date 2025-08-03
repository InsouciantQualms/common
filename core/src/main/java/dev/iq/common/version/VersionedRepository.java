package dev.iq.common.version;

import java.time.Instant;
import java.util.List;

/**
 * Base operations for any repository that tracks a versioned element (Node, Edge and Component).
 */
public interface VersionedRepository<T extends Versioned> extends VersionedFinder<T> {

    /** Saves an element to the persistence store. */
    T save(T node);

    /** Finds an element by its ID returning all versions (active and inactive). */
    List<T> findAll(NanoId nodeId);

    /** Deletes an element from the repository returning true if it was found. */
    boolean delete(NanoId nodeId);

    /** Expires an element at the given timestamp returning true if it was found. */
    boolean expire(NanoId id, Instant timestamp);
}
