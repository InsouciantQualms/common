package dev.iq.common.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class VersionedTest {

    @Test
    void minimalImplementation_satisfiesContract() {
        Instant created = Instant.now();
        Locator locator = new Locator(NanoId.generate(), 1);

        Versioned minimal = new Versioned() {
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
        Instant created = Instant.now().minusSeconds(100);
        Instant expired = Instant.now().minusSeconds(50);
        Locator locator = new Locator(NanoId.generate(), 1);

        Versioned expiredVersion = new Versioned() {
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
        Instant created = Instant.now();
        Locator locator = new Locator(NanoId.generate(), 42);
        Optional<Instant> expired = Optional.of(created.plusSeconds(3600));

        RecordVersioned versioned = new RecordVersioned(locator, created, expired);

        assertEquals(locator, versioned.locator());
        assertEquals(created, versioned.created());
        assertEquals(expired, versioned.expired());
    }

    @Test
    void versionedEquality_basedOnLocator() {
        NanoId id = NanoId.generate();
        Locator locator1 = new Locator(id, 1);
        Locator locator2 = new Locator(id, 1);
        Instant now = Instant.now();

        Versioned v1 = new RecordVersioned(locator1, now, Optional.empty());
        Versioned v2 = new RecordVersioned(locator2, now.plusSeconds(10), Optional.of(now.plusSeconds(20)));

        assertTrue(Locateable.equals(v1, v2));
    }

    @Test
    void versionedHashCode_consistentWithEquals() {
        NanoId id = NanoId.generate();
        Locator locator = new Locator(id, 1);
        Instant now = Instant.now();

        Versioned v1 = new RecordVersioned(locator, now, Optional.empty());
        Versioned v2 = new RecordVersioned(locator, now.plusSeconds(10), Optional.of(now.plusSeconds(20)));

        assertEquals(Locateable.hashCode(v1), Locateable.hashCode(v2));
    }

    @Test
    void differentVersions_haveDifferentLocators() {
        NanoId id = NanoId.generate();
        Instant now = Instant.now();

        Versioned v1 = new RecordVersioned(new Locator(id, 1), now, Optional.empty());
        Versioned v2 = new RecordVersioned(new Locator(id, 2), now, Optional.empty());

        assertNotEquals(v1.locator(), v2.locator());
        assertFalse(Locateable.equals(v1, v2));
    }

    @Test
    void nullExpired_handledProperly() {
        RecordVersioned v1 = new RecordVersioned(new Locator(NanoId.generate(), 1), Instant.now(), null);

        assertNull(v1.expired());
    }

    private record RecordVersioned(Locator locator, Instant created, Optional<Instant> expired) implements Versioned {}
}
