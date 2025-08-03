/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

/** Tests for the NanoId record covering unique ID functionality. */
final class NanoIdTest {

    private static final Pattern PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

    @Test
    void testGenerate() {

        final var nanoId = NanoId.generate();

        assertNotNull(nanoId);
        assertNotNull(nanoId.id());
        assertFalse(nanoId.id().isEmpty());
    }

    @Test
    void testConstructor() {

        final var testId = "test-id-123";
        final var nanoId = new NanoId(testId);

        assertEquals(testId, nanoId.id());
    }

    @Test
    void testGenerateUniqueness() {

        final var id1 = NanoId.generate();
        final var id2 = NanoId.generate();

        assertNotEquals(id1.id(), id2.id());
    }

    @Test
    void testGenerateMultiple() {

        final var ids = new HashSet<String>();
        final var count = 1000;

        for (var i = 0; i < count; i++) {
            final var nanoId = NanoId.generate();
            ids.add(nanoId.id());
        }

        assertEquals(count, ids.size());
    }

    @Test
    void testRecordEquality() {

        final var id1 = new NanoId("same-id");
        final var id2 = new NanoId("same-id");

        assertEquals(id1, id2);
        final var id3 = new NanoId("different-id");
        assertNotEquals(id1, id3);
    }

    @Test
    void testRecordHashCode() {

        final var id1 = new NanoId("same-id");
        final var id2 = new NanoId("same-id");

        assertEquals(id1.hashCode(), id2.hashCode());
        final var id3 = new NanoId("different-id");
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }

    @Test
    void testRecordToString() {

        final var testId = "test-id-456";
        final var nanoId = new NanoId(testId);

        final var string = nanoId.toString();
        assertNotNull(string);
        assertTrue(string.contains(testId));
    }

    @Test
    void testIdLength() {

        final var nanoId = NanoId.generate();

        assertNotNull(nanoId.id());
        assertFalse(nanoId.id().isEmpty());
    }

    @Test
    void testIdCharacters() {

        final var nanoId = NanoId.generate();
        final var id = nanoId.id();

        assertTrue(PATTERN.matcher(id).matches());
    }

    @Test
    void testNullId() {

        final var nanoId = new NanoId(null);

        assertNull(nanoId.id());
    }

    @Test
    void testEmptyId() {

        final var nanoId = new NanoId("");

        assertEquals("", nanoId.id());
    }

    @Test
    void testWithSpecialCharacters() {

        final var specialId = "test_id-123";
        final var nanoId = new NanoId(specialId);

        assertEquals(specialId, nanoId.id());
    }

    @Test
    void testImmutability() {

        final var originalId = "original-id";
        final var nanoId = new NanoId(originalId);

        assertEquals(originalId, nanoId.id());

        final var retrievedId = nanoId.id();
        assertEquals(originalId, retrievedId);
    }

    @Test
    void testSetUsage() {

        final var nanoId1 = new NanoId("id1");

        final var set = new HashSet<NanoId>();
        set.add(nanoId1);
        final var nanoId2 = new NanoId("id2");
        set.add(nanoId2);
        final var nanoId3 = new NanoId("id1");
        set.add(nanoId3);

        assertEquals(2, set.size());
        assertTrue(set.contains(nanoId1));
        assertTrue(set.contains(nanoId2));
        assertTrue(set.contains(nanoId3));
    }
}
