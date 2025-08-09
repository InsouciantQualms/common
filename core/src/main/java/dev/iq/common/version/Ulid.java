package dev.iq.common.version;

import com.github.f4b6a3.ulid.UlidCreator;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a ULID, which is a lexographically sortable unique identifier.
 * ULIDs can safely be used as both keys in a HashMap and in a database.
 * ULIDs have a time component, storing the Instant they are created and are Comparable.
 * ULIDs are also monotonically increasing (over time, the identifiers will compare as later).
 * This type is immutable and thread-safe.
 */
@SuppressWarnings("ClassCanBeRecord")
public final class Ulid implements Uid, Comparable<Ulid> {

    /** Delegate implementation of Ulid. */
    private final com.github.f4b6a3.ulid.Ulid delegate;

    /** Creates a Ulid with the specified delegate as its implementation. */
    private Ulid(final com.github.f4b6a3.ulid.Ulid delegate) {

        Objects.requireNonNull(delegate);
        this.delegate = delegate;
    }

    /** Generate a new, random ULID. */
    public static Ulid generate() {

        final var generated = UlidCreator.getUlid();
        return new Ulid(generated);
    }

    /** Parses the string representation of a ULID. */
    public static Ulid from(final String id) {

        Objects.requireNonNull(id);
        final var parsed = com.github.f4b6a3.ulid.Ulid.from(id);
        return new Ulid(parsed);
    }

    /** Converts a UUID to a ULID. */
    public static Ulid fromUuid(final UUID uuid) {

        Objects.requireNonNull(uuid);
        final var parsed = com.github.f4b6a3.ulid.Ulid.from(uuid);
        return new Ulid(parsed);
    }

    /** {@inheritDoc} */
    @Override
    public String code() {

        return delegate.toString();
    }

    /** Returns the instant (timestamp) associated with the ULID. */
    public Instant timestamp() {

        return delegate.getInstant();
    }

    /** Converts the ULID to a UUID. */
    public UUID toUuid() {

        return delegate.toUuid();
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object target) {

        return (target instanceof Ulid) && (delegate.equals(((Ulid) target).delegate));
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {

        return delegate.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(final Ulid other) {

        return delegate.compareTo(other.delegate);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {

        return String.format("Ulid [%s]", delegate);
    }
}
