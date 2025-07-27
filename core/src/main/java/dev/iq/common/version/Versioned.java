package dev.iq.common.version;

import dev.iq.common.annotation.Stable;
import java.time.Instant;
import java.util.Optional;

/** Represents a versioned item that can be located by a unique NanoId and version number. */
@Stable
public interface Versioned {

    /** Returns the unique locator for this versioned item. */
    Locator locator();

    /** Returns the timestamp when this version was created. */
    Instant created();

    /** Returns the timestamp when this version expired, if applicable. */
    Optional<Instant> expired();
}
