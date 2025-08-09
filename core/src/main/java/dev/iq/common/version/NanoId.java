/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.version;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import dev.iq.common.annotation.Stable;

/**
 * Encapsulates a Nano ID used to uniquely identify an item, like a UUID, but compliant for REST usage.
 * There is no ordering to a NanoID, and hence they can only be compared to each other for equality.
 * Nano IDs can be used as a key in a HashMap but should not be used as a database key (due to random ordering).
 * This record is immutable and thread-safe.
 */
@Stable
public record NanoId(String id) implements Uid {

    /** Generate a new, random Nano ID. */
    public static NanoId generate() {

        final var generated = NanoIdUtils.randomNanoId();
        return new NanoId(generated);
    }

    /** {@inheritDoc} */
    @Override
    public String code() {

        return id;
    }
}
