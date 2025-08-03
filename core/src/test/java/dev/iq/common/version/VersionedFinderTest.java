package dev.iq.common.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class VersionedFinderTest {

    private TestVersionedFinder finder;
    private NanoId id1;
    private NanoId id2;

    @BeforeEach
    void before() {
        finder = new TestVersionedFinder();
        id1 = NanoId.generate();
        id2 = NanoId.generate();
    }

    @Test
    void findVersions_returnsAllVersionsForId() {
        var v1 = new TestVersioned(
                new Locator(id1, 1),
                Instant.now().minusSeconds(60),
                Optional.of(Instant.now().minusSeconds(30)));
        var v2 = new TestVersioned(new Locator(id1, 2), Instant.now().minusSeconds(30), Optional.empty());
        finder.add(v1);
        finder.add(v2);

        List<TestVersioned> history = finder.findVersions(id1);

        assertEquals(2, history.size());
        assertEquals(1, history.get(0).locator().version());
        assertEquals(2, history.get(1).locator().version());
    }

    @Test
    void findVersions_returnsEmptyListForUnknownId() {
        List<TestVersioned> history = finder.findVersions(id1);
        assertTrue(history.isEmpty());
    }

    @Test
    void findActiveAt() {
        var v1 = new TestVersioned(
                new Locator(id1, 1),
                Instant.now().minusSeconds(60),
                Optional.of(Instant.now().minusSeconds(30)));
        var v2 = new TestVersioned(new Locator(id1, 2), Instant.now().minusSeconds(30), Optional.empty());
        finder.add(v1);
        finder.add(v2);

        TestVersioned current = finder.findActive(id1).orElseThrow();

        assertNotNull(current);
        assertEquals(2, current.locator().version());
    }

    @Test
    void findActive_returnsEmptyForUnknownId() {
        Optional<TestVersioned> result = finder.findActive(id1);
        assertTrue(result.isEmpty());
    }

    @Test
    void findActive_returnsEmptyForExpiredVersion() {
        var v1 = new TestVersioned(
                new Locator(id1, 1),
                Instant.now().minusSeconds(60),
                Optional.of(Instant.now().minusSeconds(30)));
        finder.add(v1);

        Optional<TestVersioned> result = finder.findActive(id1);
        assertTrue(result.isEmpty());
    }

    @Test
    void versionById_returnsSpecificVersion() {
        var locator = new Locator(id1, 2);
        var v1 = new TestVersioned(new Locator(id1, 1), Instant.now(), Optional.empty());
        var v2 = new TestVersioned(locator, Instant.now(), Optional.empty());
        finder.add(v1);
        finder.add(v2);

        TestVersioned result = finder.find(locator);

        assertNotNull(result);
        assertEquals(locator, result.locator());
    }

    @Test
    void find_throwsForUnknownLocator() {
        var locator = new Locator(id1, 1);
        assertThrows(NoSuchElementException.class, () -> finder.find(locator));
    }

    @Test
    void versionAt_returnsFindActiveAtTimestamp() {
        Instant base = Instant.now();
        var v1 = new TestVersioned(new Locator(id1, 1), base.minusSeconds(100), Optional.of(base.minusSeconds(50)));
        var v2 = new TestVersioned(new Locator(id1, 2), base.minusSeconds(50), Optional.empty());
        finder.add(v1);
        finder.add(v2);

        Optional<TestVersioned> result = finder.findAt(id1, base.minusSeconds(75));

        assertTrue(result.isPresent());
        assertEquals(1, result.get().locator().version());
    }

    @Test
    void versionAt_returnsEmptyWhenNoFindActiveAtTimestamp() {
        Instant base = Instant.now();
        var v1 = new TestVersioned(new Locator(id1, 1), base.minusSeconds(50), Optional.empty());
        finder.add(v1);

        Optional<TestVersioned> result = finder.findAt(id1, base.minusSeconds(100));

        assertFalse(result.isPresent());
    }

    @Test
    void multipleIds_maintainSeparateHistories() {
        var versionOneIdOne = new TestVersioned(new Locator(id1, 1), Instant.now(), Optional.empty());
        var versionOneIdTwo = new TestVersioned(new Locator(id2, 1), Instant.now(), Optional.empty());
        var versionTwoIdTwo = new TestVersioned(new Locator(id2, 2), Instant.now(), Optional.empty());
        finder.add(versionOneIdOne);
        finder.add(versionOneIdTwo);
        finder.add(versionTwoIdTwo);

        assertEquals(1, finder.findVersions(id1).size());
        assertEquals(2, finder.findVersions(id2).size());
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

    private static class TestVersionedFinder implements VersionedFinder<TestVersioned> {
        private final List<TestVersioned> items = new ArrayList<>();

        void add(TestVersioned item) {
            items.add(item);
        }

        @Override
        public List<TestVersioned> findVersions(NanoId id) {
            return Versions.findAllVersions(id, items);
        }

        @Override
        public Optional<TestVersioned> findActive(NanoId id) {
            return Versions.findActive(id, items);
        }

        @Override
        public TestVersioned find(Locator locator) {
            return items.stream()
                    .filter(v -> v.locator().equals(locator))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Version not found: " + locator));
        }

        @Override
        public Optional<TestVersioned> findAt(NanoId id, Instant timestamp) {
            return Versions.findAt(id, timestamp, items);
        }
    }
}
