package dev.iq.common.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VersionsTest {

    private NanoId id1;
    private NanoId id2;
    private List<TestVersioned> items;

    @BeforeEach
    void before() {
        id1 = NanoId.generate();
        id2 = NanoId.generate();
        items = new ArrayList<>();
    }

    @Test
    void findActive_returnsLatestActiveVersion() {
        var v1 = new TestVersioned(new Locator(id1, 1), Instant.now().minusSeconds(60), Optional.empty());
        var v2 = new TestVersioned(
                new Locator(id1, 2),
                Instant.now().minusSeconds(30),
                Optional.of(Instant.now().minusSeconds(10)));
        var v3 = new TestVersioned(new Locator(id1, 3), Instant.now().minusSeconds(5), Optional.empty());
        items.addAll(List.of(v1, v2, v3));

        Optional<TestVersioned> result = Versions.findActive(id1, items);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().locator().version());
    }

    @Test
    void findActive_returnsEmptyWhenNoActiveVersions() {
        var v1 = new TestVersioned(
                new Locator(id1, 1),
                Instant.now().minusSeconds(60),
                Optional.of(Instant.now().minusSeconds(30)));
        var v2 = new TestVersioned(
                new Locator(id1, 2),
                Instant.now().minusSeconds(30),
                Optional.of(Instant.now().minusSeconds(10)));
        items.addAll(List.of(v1, v2));

        Optional<TestVersioned> result = Versions.findActive(id1, items);

        assertFalse(result.isPresent());
    }

    @Test
    void findActive_returnsEmptyForNonExistentId() {
        var v1 = new TestVersioned(new Locator(id1, 1), Instant.now(), Optional.empty());
        items.add(v1);

        Optional<TestVersioned> result = Versions.findActive(id2, items);

        assertFalse(result.isPresent());
    }

    @Test
    void findAt_returnsVersionActiveAtTimestamp() {
        Instant base = Instant.now();
        var v1 = new TestVersioned(new Locator(id1, 1), base.minusSeconds(100), Optional.of(base.minusSeconds(80)));
        var v2 = new TestVersioned(new Locator(id1, 2), base.minusSeconds(80), Optional.of(base.minusSeconds(40)));
        var v3 = new TestVersioned(new Locator(id1, 3), base.minusSeconds(40), Optional.empty());
        items.addAll(List.of(v1, v2, v3));

        Optional<TestVersioned> result = Versions.findAt(id1, base.minusSeconds(60), items);

        assertTrue(result.isPresent());
        assertEquals(2, result.get().locator().version());
    }

    @Test
    void findAt_returnsEmptyWhenNoVersionActiveAtTimestamp() {
        Instant base = Instant.now();
        var v1 = new TestVersioned(new Locator(id1, 1), base.minusSeconds(50), Optional.empty());
        items.add(v1);

        Optional<TestVersioned> result = Versions.findAt(id1, base.minusSeconds(100), items);

        assertFalse(result.isPresent());
    }

    @Test
    void findAllVersions_returnsAllVersionsSorted() {
        var v3 = new TestVersioned(new Locator(id1, 3), Instant.now(), Optional.empty());
        var v1 = new TestVersioned(new Locator(id1, 1), Instant.now(), Optional.empty());
        var v2 = new TestVersioned(new Locator(id1, 2), Instant.now(), Optional.empty());
        var other = new TestVersioned(new Locator(id2, 1), Instant.now(), Optional.empty());
        items.addAll(List.of(v3, v1, v2, other));

        List<TestVersioned> result = Versions.findAllVersions(id1, items);

        assertEquals(3, result.size());
        assertEquals(1, result.get(0).locator().version());
        assertEquals(2, result.get(1).locator().version());
        assertEquals(3, result.get(2).locator().version());
    }

    @Test
    void findAllVersions_returnsEmptyListForNonExistentId() {
        var v1 = new TestVersioned(new Locator(id1, 1), Instant.now(), Optional.empty());
        items.add(v1);

        List<TestVersioned> result = Versions.findAllVersions(id2, items);

        assertTrue(result.isEmpty());
    }

    @Test
    void allActive_returnsOnlyActiveVersions() {
        var active1 = new TestVersioned(new Locator(id1, 1), Instant.now(), Optional.empty());
        var expired = new TestVersioned(new Locator(id1, 2), Instant.now(), Optional.of(Instant.now()));
        var active2 = new TestVersioned(new Locator(id2, 1), Instant.now(), Optional.empty());
        items.addAll(List.of(active1, expired, active2));

        List<TestVersioned> result = Versions.allActive(items);

        assertEquals(2, result.size());
        assertTrue(result.contains(active1));
        assertTrue(result.contains(active2));
        assertFalse(result.contains(expired));
    }

    @Test
    void validateForExpiry_returnsElementWhenPresent() {
        var element = new TestVersioned(new Locator(id1, 1), Instant.now(), Optional.empty());

        TestVersioned result = Versions.validateForExpiry(Optional.of(element), id1, "TestElement");

        assertEquals(element, result);
    }

    @Test
    void validateForExpiry_throwsWhenElementNotPresent() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> Versions.validateForExpiry(Optional.empty(), id1, "TestElement"));

        assertEquals("TestElement not found: " + id1, exception.getMessage());
    }

    @Test
    void isActiveAt_returnsTrueWhenActiveAtTimestamp() {
        Instant base = Instant.now();
        var v1 = new TestVersioned(new Locator(id1, 1), base.minusSeconds(100), Optional.empty());
        var v2 = new TestVersioned(new Locator(id1, 2), base.minusSeconds(100), Optional.of(base.minusSeconds(50)));

        assertTrue(Versions.isActiveAt(base.minusSeconds(60), v1));
        assertTrue(Versions.isActiveAt(base.minusSeconds(80), v2));
    }

    @Test
    void isActiveAt_returnsFalseWhenNotActiveAtTimestamp() {
        Instant base = Instant.now();
        var notYetCreated = new TestVersioned(new Locator(id1, 1), base.plusSeconds(10), Optional.empty());
        var expired =
                new TestVersioned(new Locator(id1, 2), base.minusSeconds(100), Optional.of(base.minusSeconds(50)));

        assertFalse(Versions.isActiveAt(base, notYetCreated));
        assertFalse(Versions.isActiveAt(base.minusSeconds(40), expired));
    }

    @Test
    void isActiveAt_handlesBoundaryConditions() {
        Instant created = Instant.parse("2024-01-01T12:00:00Z");
        Instant expired = Instant.parse("2024-01-01T14:00:00Z");
        var versioned = new TestVersioned(new Locator(id1, 1), created, Optional.of(expired));

        assertTrue(Versions.isActiveAt(created, versioned));
        assertFalse(Versions.isActiveAt(expired, versioned));
        assertTrue(Versions.isActiveAt(created.plusSeconds(1), versioned));
        assertTrue(Versions.isActiveAt(expired.minusSeconds(1), versioned));
    }

    @Test
    void equals_returnsTrueForSameLocator() {
        var locator = new Locator(id1, 1);
        var v1 = new TestVersioned(locator, Instant.now(), Optional.empty());
        var v2 = new TestVersioned(locator, Instant.now().plusSeconds(10), Optional.of(Instant.now()));

        assertTrue(Versions.equals(v1, v2));
        assertTrue(Versions.equals(v1, v1));
    }

    @Test
    void equals_returnsFalseForDifferentLocators() {
        var v1 = new TestVersioned(new Locator(id1, 1), Instant.now(), Optional.empty());
        var v2 = new TestVersioned(new Locator(id1, 2), Instant.now(), Optional.empty());
        var v3 = new TestVersioned(new Locator(id2, 1), Instant.now(), Optional.empty());

        assertFalse(Versions.equals(v1, v2));
        assertFalse(Versions.equals(v1, v3));
    }

    @Test
    void hashCode_returnsSameValueForSameLocator() {
        var locator = new Locator(id1, 1);
        var v1 = new TestVersioned(locator, Instant.now(), Optional.empty());
        var v2 = new TestVersioned(locator, Instant.now().plusSeconds(10), Optional.empty());

        assertEquals(Versions.hashCode(v1), Versions.hashCode(v2));
    }

    @Test
    void hashCode_returnsDifferentValuesForDifferentLocators() {
        var v1 = new TestVersioned(new Locator(id1, 1), Instant.now(), Optional.empty());
        var v2 = new TestVersioned(new Locator(id2, 1), Instant.now(), Optional.empty());

        assertNotEquals(Versions.hashCode(v1), Versions.hashCode(v2));
    }

    @Test
    void findActive_handlesEmptyCollection() {
        Optional<TestVersioned> result = Versions.findActive(id1, Collections.emptyList());
        assertFalse(result.isPresent());
    }

    @Test
    void findAt_handlesEmptyCollection() {
        Optional<TestVersioned> result = Versions.findAt(id1, Instant.now(), Collections.emptyList());
        assertFalse(result.isPresent());
    }

    @Test
    void allActive_handlesEmptyCollection() {
        List<TestVersioned> result = Versions.allActive(Collections.emptyList());
        assertTrue(result.isEmpty());
    }

    private static class TestVersioned implements Versioned {
        private final Locator locator;
        private final Instant created;
        private final Optional<Instant> expired;

        TestVersioned(Locator locator, Instant created, Optional<Instant> expired) {
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
}
