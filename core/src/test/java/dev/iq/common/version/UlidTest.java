package dev.iq.common.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class UlidTest {

    @Test
    void testGenerate() {
        Ulid ulid1 = Ulid.generate();
        Ulid ulid2 = Ulid.generate();

        assertNotNull(ulid1);
        assertNotNull(ulid2);
        assertNotEquals(ulid1, ulid2);
        assertNotEquals(ulid1.code(), ulid2.code());
    }

    @Test
    void testFromString() {
        Ulid original = Ulid.generate();
        String stringRepresentation = original.code();

        Ulid parsed = Ulid.from(stringRepresentation);

        assertEquals(original, parsed);
        assertEquals(original.code(), parsed.code());
        assertEquals(original.hashCode(), parsed.hashCode());
    }

    @Test
    void testFromStringInvalid() {
        assertThrows(IllegalArgumentException.class, () -> Ulid.from("invalid-ulid"));
        assertThrows(NullPointerException.class, () -> Ulid.from(null));
    }

    @Test
    void testFromUuid() {
        UUID uuid = UUID.randomUUID();
        Ulid ulid = Ulid.fromUuid(uuid);

        assertNotNull(ulid);
        assertEquals(uuid, ulid.toUuid());
    }

    @Test
    void testFromUuidNull() {
        assertThrows(NullPointerException.class, () -> Ulid.fromUuid(null));
    }

    @Test
    void testToUuid() {
        Ulid ulid = Ulid.generate();
        UUID uuid = ulid.toUuid();

        assertNotNull(uuid);

        Ulid reconstructed = Ulid.fromUuid(uuid);
        assertEquals(ulid, reconstructed);
    }

    @Test
    void testCode() {
        Ulid ulid = Ulid.generate();
        String code = ulid.code();

        assertNotNull(code);
        assertEquals(26, code.length());
        assertTrue(code.matches("[0-9A-Z]{26}"));
    }

    @Test
    void testTimestamp() {
        Instant before = Instant.now().minusMillis(1);
        Ulid ulid = Ulid.generate();
        Instant after = Instant.now().plusMillis(1);

        Instant ulidTimestamp = ulid.timestamp();

        assertNotNull(ulidTimestamp);
        assertFalse(ulidTimestamp.isBefore(before));
        assertFalse(ulidTimestamp.isAfter(after));
    }

    @Test
    void testCompareTo() throws InterruptedException {
        Ulid ulid1 = Ulid.generate();
        Thread.sleep(2);
        Ulid ulid2 = Ulid.generate();
        Thread.sleep(2);
        Ulid ulid3 = Ulid.generate();

        assertTrue(ulid1.compareTo(ulid2) < 0);
        assertTrue(ulid2.compareTo(ulid3) < 0);
        assertTrue(ulid1.compareTo(ulid3) < 0);

        assertTrue(ulid2.compareTo(ulid1) > 0);
        assertTrue(ulid3.compareTo(ulid2) > 0);
        assertTrue(ulid3.compareTo(ulid1) > 0);

        assertEquals(0, ulid1.compareTo(ulid1));
    }

    @Test
    void testSortingOrder() throws InterruptedException {
        List<Ulid> ulids = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ulids.add(Ulid.generate());
            Thread.sleep(1);
        }

        List<Ulid> sorted = new ArrayList<>(ulids);
        Collections.shuffle(sorted);
        Collections.sort(sorted);

        assertEquals(ulids, sorted);
    }

    @Test
    void testEquals() {
        Ulid ulid1 = Ulid.generate();
        Ulid ulid2 = Ulid.from(ulid1.code());
        Ulid ulid3 = Ulid.generate();

        assertEquals(ulid1, ulid1);
        assertEquals(ulid1, ulid2);
        assertNotEquals(ulid1, ulid3);
        assertNotEquals(ulid1, null);
        assertNotEquals(ulid1, "not a ulid");
        assertNotEquals(ulid1, ulid1.code());
    }

    @Test
    void testHashCode() {
        Ulid ulid1 = Ulid.generate();
        Ulid ulid2 = Ulid.from(ulid1.code());
        Ulid ulid3 = Ulid.generate();

        assertEquals(ulid1.hashCode(), ulid2.hashCode());
        assertNotEquals(ulid1.hashCode(), ulid3.hashCode());
    }

    @Test
    void testHashMapCompatibility() {
        Set<Ulid> set = new HashSet<>();
        Ulid ulid1 = Ulid.generate();
        Ulid ulid2 = Ulid.from(ulid1.code());
        Ulid ulid3 = Ulid.generate();

        set.add(ulid1);
        assertTrue(set.contains(ulid1));
        assertTrue(set.contains(ulid2));
        assertFalse(set.contains(ulid3));

        set.add(ulid3);
        assertEquals(2, set.size());
    }

    @Test
    void testToString() {
        Ulid ulid = Ulid.generate();
        String string = ulid.toString();

        assertNotNull(string);
        assertTrue(string.startsWith("Ulid ["));
        assertTrue(string.endsWith("]"));
        assertTrue(string.contains(ulid.code()));
    }

    @Test
    void testThreadSafety() throws InterruptedException {
        int threadCount = 10;
        int ulidsPerThread = 100;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        Set<String> generatedCodes = Collections.synchronizedSet(new HashSet<>());

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < ulidsPerThread; j++) {
                        Ulid ulid = Ulid.generate();
                        generatedCodes.add(ulid.code());
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        assertTrue(latch.await(10, TimeUnit.SECONDS));
        executor.shutdown();

        assertEquals(threadCount * ulidsPerThread, generatedCodes.size());
    }

    @Test
    void testMonotonicity() throws InterruptedException {
        List<Ulid> ulids = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            ulids.add(Ulid.generate());
            Thread.sleep(2);
        }

        for (int i = 1; i < ulids.size(); i++) {
            Ulid prev = ulids.get(i - 1);
            Ulid current = ulids.get(i);
            assertTrue(prev.compareTo(current) < 0);
            assertTrue(prev.timestamp().compareTo(current.timestamp()) <= 0);
        }
    }

    @Test
    void testUidInterface() {
        Ulid ulid = Ulid.generate();

        assertTrue(ulid instanceof Uid);

        Uid uid = ulid;
        assertEquals(ulid.code(), uid.code());
    }

    @Test
    void testRoundTripConversions() {
        Ulid original = Ulid.generate();

        String asString = original.code();
        Ulid fromString = Ulid.from(asString);
        assertEquals(original, fromString);

        UUID asUuid = original.toUuid();
        Ulid fromUuid = Ulid.fromUuid(asUuid);
        assertEquals(original, fromUuid);

        assertEquals(fromString, fromUuid);
    }
}
