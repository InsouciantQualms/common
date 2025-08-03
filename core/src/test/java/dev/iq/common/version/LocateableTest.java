package dev.iq.common.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.Test;

final class LocateableTest {

    private final NanoId id1 = NanoId.generate();
    private final NanoId id2 = NanoId.generate();

    @Test
    void equals_returnsTrueForSameLocator() {

        final var locator = new Locator(id1, 1);
        final var v1 = new VersionedFixture(locator, Instant.now(), Optional.empty());
        final var v2 = new VersionedFixture(locator, Instant.now().plusSeconds(10), Optional.of(Instant.now()));

        assertTrue(Locators.equals(v1, v2));
        assertTrue(Locators.equals(v1, v1));
    }

    @Test
    void equals_returnsFalseForDifferentLocators() {

        final var v1 = new VersionedFixture(new Locator(id1, 1), Instant.now(), Optional.empty());
        final var v2 = new VersionedFixture(new Locator(id1, 2), Instant.now(), Optional.empty());
        final var v3 = new VersionedFixture(new Locator(id2, 1), Instant.now(), Optional.empty());

        assertFalse(Locators.equals(v1, v2));
        assertFalse(Locators.equals(v1, v3));
    }

    @Test
    void hashCode_returnsSameValueForSameLocator() {

        final var locator = new Locator(id1, 1);
        final var v1 = new VersionedFixture(locator, Instant.now(), Optional.empty());
        final var v2 = new VersionedFixture(locator, Instant.now().plusSeconds(10), Optional.empty());

        assertEquals(Locators.hashCode(v1), Locators.hashCode(v2));
    }

    @Test
    void hashCode_returnsDifferentValuesForDifferentLocators() {

        final var v1 = new VersionedFixture(new Locator(id1, 1), Instant.now(), Optional.empty());
        final var v2 = new VersionedFixture(new Locator(id2, 1), Instant.now(), Optional.empty());

        assertNotEquals(Locators.hashCode(v1), Locators.hashCode(v2));
    }
}
