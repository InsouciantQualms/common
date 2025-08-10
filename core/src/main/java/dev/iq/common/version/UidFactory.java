package dev.iq.common.version;

import java.util.Objects;

/**
 * Factory for creating Uid instances.
 */
public final class UidFactory {

    /** Type contains only static members. */
    private UidFactory() {}

    /** Generates a new Uid. The implementation currently defaults to Ulid. */
    public static Uid generate() {

        return Ulid.generate();
    }

    /** Attempts to create a Uid from the specified string. */
    public static Uid from(final String target) {

        Objects.requireNonNull(target);
        return switch (target.length()) {
            case Ulid.DEFAULT_LENGTH -> Ulid.from(target);
            case NanoId.DEFAULT_LENGTH -> NanoId.from(target);
            default -> throw new IllegalArgumentException(String.format("Unknown format %s", target));
        };
    }
}
