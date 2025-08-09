/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.version;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import dev.iq.common.annotation.Stable;
import java.util.Objects;

/**
 * Encapsulates a Nano ID used to uniquely identify an item, like a UUID, but compliant for REST usage.
 * There is no ordering to a NanoID, and hence they can only be compared to each other for equality.
 * Nano IDs can be used as a key in a HashMap but should not be used as a database key (due to random ordering).
 * This type is immutable and thread-safe.
 */
@Stable
@SuppressWarnings("ClassCanBeRecord")
public final class NanoId implements Uid {

    /** Default length of a Nano ID. */
    static final int DEFAULT_LENGTH = 21;

    /** The string representation of the Nano ID. */
    private final String id;

    /** Creates a NanoId with the specified string as its value. */
    private NanoId(final String id) {

        Objects.requireNonNull(id);
        this.id = id;
    }

    /** Generate a new, random Nano ID. */
    public static NanoId generate() {

        final var generated = NanoIdUtils.randomNanoId();
        return new NanoId(generated);
    }

    /** Parses the string representation of a NanoId. */
    public static NanoId from(final String id) {

        Objects.requireNonNull(id);
        return new NanoId(id);
    }

    /** {@inheritDoc} */
    @Override
    public String code() {

        return id;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(final Object target) {

        return (target instanceof NanoId) && (id.equals(((NanoId) target).id));
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {

        return id.hashCode();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {

        return String.format("NanoId [%s]", id);
    }
}
