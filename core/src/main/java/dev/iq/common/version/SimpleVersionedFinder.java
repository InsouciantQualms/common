package dev.iq.common.version;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * In memory simple implementation of the VersionedFinder interface.
 */
public final class SimpleVersionedFinder<T extends Versioned> implements VersionedFinder<T> {

    /** Map of loaded components by uid. */
    private final Map<Uid, List<T>> all;

    /** Creates an empty simple versioned finder. */
    public SimpleVersionedFinder() {

        all = new HashMap<>();
    }

    /** Creates a simple versioned finder with the specified versioned elements. */
    public SimpleVersionedFinder(final Map<Uid, List<T>> all) {

        this.all = Map.copyOf(all);
    }

    /** Adds the specified versioned element to the finder, returning a new persistent instance. */
    public SimpleVersionedFinder<T> add(final T target) {

        final var copy = new HashMap<Uid, List<T>>(all.size());
        for (final var entry : all.entrySet()) {
            copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        copy.computeIfAbsent(target.locator().id(), k -> new ArrayList<>()).add(target);
        return new SimpleVersionedFinder<>(copy);
    }

    /** {@inheritDoc} */
    @Override
    public Optional<T> findActive(final Uid id) {

        final var versions = all.get(id);
        if (versions == null) {
            return Optional.empty();
        }

        return versions.stream().filter(c -> c.expired().isEmpty()).max(Comparator.comparingInt(c -> c.locator()
                .version()));
    }

    /** {@inheritDoc} */
    @Override
    public Optional<T> findAt(final Uid id, final Instant timestamp) {

        // Find the component version active at timestamp
        final var versions = all.get(id);
        if (versions == null) {
            return Optional.empty();
        }
        return versions.stream()
                .filter(c -> !c.created().isAfter(timestamp))
                .filter(c -> c.expired().isEmpty() || c.expired().get().isAfter(timestamp))
                .max(Comparator.comparingInt(c -> c.locator().version()));
    }

    /** {@inheritDoc} */
    @Override
    public T find(final Locator locator) {

        final var versions = all.get(locator.id());
        if (versions == null) {
            throw new IllegalArgumentException("Not found for uid: " + locator);
        }

        return versions.stream()
                .filter(c -> c.locator().equals(locator))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Not found for locator: " + locator));
    }

    /** {@inheritDoc} */
    @Override
    public List<T> findVersions(final Uid id) {
        final var versions = all.get(id);
        return (versions != null) ? new ArrayList<>(versions) : List.of();
    }
}
