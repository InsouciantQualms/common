package dev.iq.common.version;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;

/**
 * Encapsulates a Nano ID used to uniquely identify and item, like a UUID,
 * but compliant for REST or microservice usage.
 */
public record NanoId(String id) {

    /**
     * Generate a new, random Nano ID.
     */
    public static NanoId generate() {

        final var generated = NanoIdUtils.randomNanoId();
        return new NanoId(generated);
    }
}
