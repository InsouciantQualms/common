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
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        assertNotNull(nanoId.code());
        assertFalse(nanoId.code().isEmpty());
    }

    @Test
    void testFromMethod() {

        final var testId = "test-id-123";
        final var nanoId = NanoId.from(testId);

        assertEquals(testId, nanoId.code());
    }

    @Test
    void testGenerateUniqueness() {

        final var id1 = NanoId.generate();
        final var id2 = NanoId.generate();

        assertNotEquals(id1.code(), id2.code());
    }

    @Test
    void testGenerateMultiple() {

        final var ids = new HashSet<String>();
        final var count = 1000;

        for (var i = 0; i < count; i++) {
            final var nanoId = NanoId.generate();
            ids.add(nanoId.code());
        }

        assertEquals(count, ids.size());
    }

    @Test
    void testRecordEquality() {

        final var id1 = NanoId.from("same-id");
        final var id2 = NanoId.from("same-id");

        assertEquals(id1, id2);
        final var id3 = NanoId.from("different-id");
        assertNotEquals(id1, id3);
    }

    @Test
    void testRecordHashCode() {

        final var id1 = NanoId.from("same-id");
        final var id2 = NanoId.from("same-id");

        assertEquals(id1.hashCode(), id2.hashCode());
        final var id3 = NanoId.from("different-id");
        assertNotEquals(id1.hashCode(), id3.hashCode());
    }

    @Test
    void testRecordToString() {

        final var testId = "test-id-456";
        final var nanoId = NanoId.from(testId);

        final var string = nanoId.toString();
        assertNotNull(string);
        assertTrue(string.contains(testId));
        assertTrue(string.startsWith("NanoId ["));
        assertTrue(string.endsWith("]"));
    }

    @Test
    void testIdLength() {

        final var nanoId = NanoId.generate();

        assertNotNull(nanoId.code());
        assertFalse(nanoId.code().isEmpty());
    }

    @Test
    void testIdCharacters() {

        final var nanoId = NanoId.generate();
        final var id = nanoId.code();

        assertTrue(PATTERN.matcher(id).matches());
    }

    @Test
    void testNullId() {

        assertThrows(NullPointerException.class, () -> NanoId.from(null));
    }

    @Test
    void testEmptyId() {

        final var nanoId = NanoId.from("");

        assertEquals("", nanoId.code());
    }

    @Test
    void testWithSpecialCharacters() {

        final var specialId = "test_id-123";
        final var nanoId = NanoId.from(specialId);

        assertEquals(specialId, nanoId.code());
    }

    @Test
    void testImmutability() {

        final var originalId = "original-id";
        final var nanoId = NanoId.from(originalId);

        assertEquals(originalId, nanoId.code());

        final var retrievedId = nanoId.code();
        assertEquals(originalId, retrievedId);
    }

    @Test
    void testSetUsage() {

        final var nanoId1 = NanoId.from("id1");

        final var set = new HashSet<NanoId>();
        set.add(nanoId1);
        final var nanoId2 = NanoId.from("id2");
        set.add(nanoId2);
        final var nanoId3 = NanoId.from("id1");
        set.add(nanoId3);

        assertEquals(2, set.size());
        assertTrue(set.contains(nanoId1));
        assertTrue(set.contains(nanoId2));
        assertTrue(set.contains(nanoId3));
    }

    @Test
    void testUidInterface() {

        final var nanoId = NanoId.generate();

        assertTrue(nanoId instanceof Uid);

        final Uid uid = nanoId;
        assertEquals(nanoId.code(), uid.code());
    }
}
