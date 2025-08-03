package dev.iq.common.version;

import java.time.Instant;
import java.util.Optional;

record VersionedFixture(Locator locator, Instant created, Optional<Instant> expired) implements Versioned {}
