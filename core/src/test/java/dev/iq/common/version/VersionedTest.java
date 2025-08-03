package dev.iq.common.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

final class VersionedTest {

    @Test
    void minimalImplementation_satisfiesContract() {
        final var created = Instant.now();
        final var locator = new Locator(NanoId.generate(), 1);

        final var minimal = new Versioned() {
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
                return Optional.empty();
            }
        };

        assertEquals(locator, minimal.locator());
        assertEquals(created, minimal.created());
        assertTrue(minimal.expired().isEmpty());
    }

    @Test
    void expiredVersion_maintainsContract() {
        final var created = Instant.now().minusSeconds(100);
        final var expired = Instant.now().minusSeconds(50);
        final var locator = new Locator(NanoId.generate(), 1);

        final var expiredVersion = new Versioned() {
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
                return Optional.of(expired);
            }
        };

        assertEquals(locator, expiredVersion.locator());
        assertEquals(created, expiredVersion.created());
        assertTrue(expiredVersion.expired().isPresent());
        assertEquals(expired, expiredVersion.expired().get());
    }

    @Test
    void recordImplementation_worksCorrectly() {
        final var created = Instant.now();
        final var locator = new Locator(NanoId.generate(), 42);
        final var expired = Optional.of(created.plusSeconds(3600));

        final var versioned = new RecordVersioned(locator, created, expired);

        assertEquals(locator, versioned.locator());
        assertEquals(created, versioned.created());
        assertEquals(expired, versioned.expired());
    }

    @Test
    void versionedEquality_basedOnLocator() {
        final var id = NanoId.generate();
        final var locator1 = new Locator(id, 1);
        final var locator2 = new Locator(id, 1);
        final var now = Instant.now();

        final Locateable v1 = new RecordVersioned(locator1, now, Optional.empty());
        final Locateable v2 = new RecordVersioned(locator2, now.plusSeconds(10), Optional.of(now.plusSeconds(20)));

        assertTrue(Locators.equals(v1, v2));
    }

    @Test
    void versionedHashCode_consistentWithEquals() {
        final var id = NanoId.generate();
        final var locator = new Locator(id, 1);
        final var now = Instant.now();

        final Locateable v1 = new RecordVersioned(locator, now, Optional.empty());
        final Locateable v2 = new RecordVersioned(locator, now.plusSeconds(10), Optional.of(now.plusSeconds(20)));

        assertEquals(Locators.hashCode(v1), Locators.hashCode(v2));
    }

    @Test
    void differentVersions_haveDifferentLocators() {
        final var id = NanoId.generate();
        final var now = Instant.now();

        final Locateable v1 = new RecordVersioned(new Locator(id, 1), now, Optional.empty());
        final Locateable v2 = new RecordVersioned(new Locator(id, 2), now, Optional.empty());

        assertNotEquals(v1.locator(), v2.locator());
        assertFalse(Locators.equals(v1, v2));
    }

    @Test
    void nullExpired_handledProperly() {
        final var v1 = new RecordVersioned(new Locator(NanoId.generate(), 1), Instant.now(), null);

        assertNull(v1.expired());
    }

    private record RecordVersioned(Locator locator, Instant created, Optional<Instant> expired) implements Versioned {}
}
