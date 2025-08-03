package dev.iq.common.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
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
    final void before() {
        finder = new TestVersionedFinder();
        id1 = NanoId.generate();
        id2 = NanoId.generate();
    }

    @Test
    final void findVersions_returnsAllVersionsForId() {
        final var v1 = new TestVersioned(
                new Locator(id1, 1),
                Instant.now().minusSeconds(60),
                Optional.of(Instant.now().minusSeconds(30)));
        final var v2 = new TestVersioned(new Locator(id1, 2), Instant.now().minusSeconds(30), Optional.empty());
        finder.add(v1);
        finder.add(v2);

        final var history = finder.findVersions(id1);

        assertEquals(2, history.size());
        assertEquals(1, history.get(0).locator().version());
        assertEquals(2, history.get(1).locator().version());
    }

    @Test
    final void findVersions_returnsEmptyListForUnknownId() {
        final var history = finder.findVersions(id1);
        assertTrue(history.isEmpty());
    }

    @Test
    final void findActiveAt() {
        final var v1 = new TestVersioned(
                new Locator(id1, 1),
                Instant.now().minusSeconds(60),
                Optional.of(Instant.now().minusSeconds(30)));
        final var v2 = new TestVersioned(new Locator(id1, 2), Instant.now().minusSeconds(30), Optional.empty());
        finder.add(v1);
        finder.add(v2);

        final var current = finder.findActive(id1).orElseThrow();

        assertNotNull(current);
        assertEquals(2, current.locator().version());
    }

    @Test
    final void findActive_returnsEmptyForUnknownId() {
        final var result = finder.findActive(id1);
        assertTrue(result.isEmpty());
    }

    @Test
    final void findActive_returnsEmptyForExpiredVersion() {
        final var v1 = new TestVersioned(
                new Locator(id1, 1),
                Instant.now().minusSeconds(60),
                Optional.of(Instant.now().minusSeconds(30)));
        finder.add(v1);

        final var result = finder.findActive(id1);
        assertTrue(result.isEmpty());
    }

    @Test
    final void versionById_returnsSpecificVersion() {
        final var locator = new Locator(id1, 2);
        final var v1 = new TestVersioned(new Locator(id1, 1), Instant.now(), Optional.empty());
        final var v2 = new TestVersioned(locator, Instant.now(), Optional.empty());
        finder.add(v1);
        finder.add(v2);

        final var result = finder.find(locator);

        assertNotNull(result);
        assertEquals(locator, result.locator());
    }

    @Test
    final void find_throwsForUnknownLocator() {
        final var locator = new Locator(id1, 1);
        assertThrows(NoSuchElementException.class, () -> finder.find(locator));
    }

    @Test
    final void versionAt_returnsFindActiveAtTimestamp() {
        final var base = Instant.now();
        final var v1 =
                new TestVersioned(new Locator(id1, 1), base.minusSeconds(100), Optional.of(base.minusSeconds(50)));
        final var v2 = new TestVersioned(new Locator(id1, 2), base.minusSeconds(50), Optional.empty());
        finder.add(v1);
        finder.add(v2);

        final var result = finder.findAt(id1, base.minusSeconds(75));

        assertTrue(result.isPresent());
        assertEquals(1, result.get().locator().version());
    }

    @Test
    final void versionAt_returnsEmptyWhenNoFindActiveAtTimestamp() {
        final var base = Instant.now();
        final var v1 = new TestVersioned(new Locator(id1, 1), base.minusSeconds(50), Optional.empty());
        finder.add(v1);

        final var result = finder.findAt(id1, base.minusSeconds(100));

        assertFalse(result.isPresent());
    }

    @Test
    final void multipleIds_maintainSeparateHistories() {
        final var versionOneIdOne = new TestVersioned(new Locator(id1, 1), Instant.now(), Optional.empty());
        final var versionOneIdTwo = new TestVersioned(new Locator(id2, 1), Instant.now(), Optional.empty());
        final var versionTwoIdTwo = new TestVersioned(new Locator(id2, 2), Instant.now(), Optional.empty());
        finder.add(versionOneIdOne);
        finder.add(versionOneIdTwo);
        finder.add(versionTwoIdTwo);

        assertEquals(1, finder.findVersions(id1).size());
        assertEquals(2, finder.findVersions(id2).size());
    }

    private record TestVersioned(Locator locator, Instant created, Optional<Instant> expired) implements Versioned {}

    private static class TestVersionedFinder implements VersionedFinder<TestVersioned> {
        private final Collection<TestVersioned> items = new ArrayList<>();

        private void add(final TestVersioned item) {
            items.add(item);
        }

        @Override
        public final List<TestVersioned> findVersions(final NanoId id) {
            return Versions.findAllVersions(id, items);
        }

        @Override
        public final Optional<TestVersioned> findActive(final NanoId id) {
            return Versions.findActive(id, items);
        }

        @Override
        public final TestVersioned find(final Locator locator) {
            return items.stream()
                    .filter(v -> v.locator().equals(locator))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchElementException("Version not found: " + locator));
        }

        @Override
        public final Optional<TestVersioned> findAt(final NanoId id, final Instant timestamp) {
            return Versions.findAt(id, timestamp, items);
        }
    }
}
