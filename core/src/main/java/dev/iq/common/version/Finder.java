package dev.iq.common.version;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Interface to locate elements and components contained in a graph. */
public interface Finder<T extends Versioned> {

    List<T> versionHistory(NanoId id);

    T currentVersion(NanoId id);

    T versionById(Locator locator);

    Optional<T> versionAt(NanoId id, Instant timestamp);
}
