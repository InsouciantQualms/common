package dev.iq.common.version;

import java.time.Instant;
import java.util.Optional;

final class VersionedFixture implements Versioned {

    private final Locator locator;
    private final Instant created;
    private final Optional<Instant> expired;

    VersionedFixture(Locator locator, Instant created, Optional<Instant> expired) {
        this.locator = locator;
        this.created = created;
        this.expired = expired;
    }

    @Override
    public Locator locator() {
        return locator;
    }

    @Override
    public Instant created() {
        return created;
    }

    @Override
    public Optional<Instant> expired() {
        return expired;
    }
}
