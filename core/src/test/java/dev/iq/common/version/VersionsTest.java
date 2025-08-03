package dev.iq.common.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

final class VersionsTest {

    private NanoId id1;
    private NanoId id2;
    private List<VersionedFixture> items;

    @BeforeEach
    void before() {
        id1 = NanoId.generate();
        id2 = NanoId.generate();
        items = new ArrayList<>();
    }

    @Test
    void findActive_returnsLatestActiveVersion() {
        final var v1 = new VersionedFixture(new Locator(id1, 1), Instant.now().minusSeconds(60), Optional.empty());
        final var v2 = new VersionedFixture(
                new Locator(id1, 2),
                Instant.now().minusSeconds(30),
                Optional.of(Instant.now().minusSeconds(10)));
        final var v3 = new VersionedFixture(new Locator(id1, 3), Instant.now().minusSeconds(5), Optional.empty());
        items.addAll(List.of(v1, v2, v3));

        final var result = Versions.findActive(id1, items);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().locator().version());
    }

    @Test
    void findActive_returnsEmptyWhenNoActiveVersions() {
        final var v1 = new VersionedFixture(
                new Locator(id1, 1),
                Instant.now().minusSeconds(60),
                Optional.of(Instant.now().minusSeconds(30)));
        final var v2 = new VersionedFixture(
                new Locator(id1, 2),
                Instant.now().minusSeconds(30),
                Optional.of(Instant.now().minusSeconds(10)));
        items.addAll(List.of(v1, v2));

        final var result = Versions.findActive(id1, items);

        assertFalse(result.isPresent());
    }

    @Test
    void findActive_returnsEmptyForNonExistentId() {
        final var v1 = new VersionedFixture(new Locator(id1, 1), Instant.now(), Optional.empty());
        items.add(v1);

        final var result = Versions.findActive(id2, items);

        assertFalse(result.isPresent());
    }

    @Test
    void findAt_returnsVersionActiveAtTimestamp() {
        final var base = Instant.now();
        final var v1 =
                new VersionedFixture(new Locator(id1, 1), base.minusSeconds(100), Optional.of(base.minusSeconds(80)));
        final var v2 =
                new VersionedFixture(new Locator(id1, 2), base.minusSeconds(80), Optional.of(base.minusSeconds(40)));
        final var v3 = new VersionedFixture(new Locator(id1, 3), base.minusSeconds(40), Optional.empty());
        items.addAll(List.of(v1, v2, v3));

        final var result = Versions.findAt(id1, base.minusSeconds(60), items);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().locator().version());
    }

    @Test
    void findAt_returnsEmptyWhenNoVersionActiveAtTimestamp() {
        final var base = Instant.now();
        final var v1 = new VersionedFixture(new Locator(id1, 1), base.minusSeconds(50), Optional.empty());
        items.add(v1);

        final var result = Versions.findAt(id1, base.minusSeconds(100), items);

        assertFalse(result.isPresent());
    }

    @Test
    void findAllVersions_returnsAllVersionsSorted() {
        final var v3 = new VersionedFixture(new Locator(id1, 3), Instant.now(), Optional.empty());
        final var v1 = new VersionedFixture(new Locator(id1, 1), Instant.now(), Optional.empty());
        final var v2 = new VersionedFixture(new Locator(id1, 2), Instant.now(), Optional.empty());
        final var other = new VersionedFixture(new Locator(id2, 1), Instant.now(), Optional.empty());
        items.addAll(List.of(v3, v1, v2, other));

        final var result = Versions.findAllVersions(id1, items);

        assertEquals(3, result.size());
        assertEquals(1, result.get(0).locator().version());
        assertEquals(2, result.get(1).locator().version());
        assertEquals(3, result.get(2).locator().version());
    }

    @Test
    void findAllVersions_returnsEmptyListForNonExistentId() {
        final var v1 = new VersionedFixture(new Locator(id1, 1), Instant.now(), Optional.empty());
        items.add(v1);

        final var result = Versions.findAllVersions(id2, items);

        assertTrue(result.isEmpty());
    }

    @Test
    void allActive_returnsOnlyActiveVersions() {
        final var active1 = new VersionedFixture(new Locator(id1, 1), Instant.now(), Optional.empty());
        final var expired = new VersionedFixture(new Locator(id1, 2), Instant.now(), Optional.of(Instant.now()));
        final var active2 = new VersionedFixture(new Locator(id2, 1), Instant.now(), Optional.empty());
        items.addAll(List.of(active1, expired, active2));

        final var result = Versions.allActive(items);

        assertEquals(2, result.size());
        assertTrue(result.contains(active1));
        assertTrue(result.contains(active2));
        assertFalse(result.contains(expired));
    }

    @Test
    void validateForExpiry_returnsElementWhenPresent() {
        final var element = new VersionedFixture(new Locator(id1, 1), Instant.now(), Optional.empty());

        final var result = Versions.validateForExpiry(Optional.of(element), id1, "TestElement");

        assertSame(element, result);
    }

    @Test
    void validateForExpiry_throwsWhenElementNotPresent() {
        final var exception = assertThrows(
                IllegalArgumentException.class, () -> Versions.validateForExpiry(Optional.empty(), id1, "TestElement"));

        assertEquals("TestElement not found: " + id1, exception.getMessage());
    }

    @Test
    void isActiveAt_returnsTrueWhenActiveAtTimestamp() {
        final var base = Instant.now();
        final var v1 = new VersionedFixture(new Locator(id1, 1), base.minusSeconds(100), Optional.empty());
        final var v2 =
                new VersionedFixture(new Locator(id1, 2), base.minusSeconds(100), Optional.of(base.minusSeconds(50)));

        assertTrue(Versions.isActiveAt(base.minusSeconds(60), v1));
        assertTrue(Versions.isActiveAt(base.minusSeconds(80), v2));
    }

    @Test
    void isActiveAt_returnsFalseWhenNotActiveAtTimestamp() {
        final var base = Instant.now();
        final var notYetCreated = new VersionedFixture(new Locator(id1, 1), base.plusSeconds(10), Optional.empty());
        final var expired =
                new VersionedFixture(new Locator(id1, 2), base.minusSeconds(100), Optional.of(base.minusSeconds(50)));

        assertFalse(Versions.isActiveAt(base, notYetCreated));
        assertFalse(Versions.isActiveAt(base.minusSeconds(40), expired));
    }

    @Test
    void isActiveAt_handlesBoundaryConditions() {
        final var created = Instant.parse("2024-01-01T12:00:00Z");
        final var expired = Instant.parse("2024-01-01T14:00:00Z");
        final var versioned = new VersionedFixture(new Locator(id1, 1), created, Optional.of(expired));

        assertTrue(Versions.isActiveAt(created, versioned));
        assertFalse(Versions.isActiveAt(expired, versioned));
        assertTrue(Versions.isActiveAt(created.plusSeconds(1), versioned));
        assertTrue(Versions.isActiveAt(expired.minusSeconds(1), versioned));
    }

    @Test
    void findActive_handlesEmptyCollection() {
        final Optional<VersionedFixture> result = Versions.findActive(id1, Collections.emptyList());
        assertFalse(result.isPresent());
    }

    @Test
    void findAt_handlesEmptyCollection() {
        final Optional<VersionedFixture> result = Versions.findAt(id1, Instant.now(), Collections.emptyList());
        assertFalse(result.isPresent());
    }

    @Test
    void allActive_handlesEmptyCollection() {
        final List<VersionedFixture> result = Versions.allActive(Collections.emptyList());
        assertTrue(result.isEmpty());
    }
}
