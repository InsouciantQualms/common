/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Tests for the Locator record covering versioned location functionality. */
final class LocatorTest {

    @Test
    void testConstructor() {

        final var nanoId = NanoId.from("test-id");
        final var version = 5;
        final var locator = new Locator(nanoId, version);

        assertEquals(nanoId, locator.id());
        assertEquals(version, locator.version());
    }

    @Test
    void testGenerate() {

        final var locator = Locator.generate();

        assertNotNull(locator);
        assertNotNull(locator.id());
        assertEquals(1, locator.version());
    }

    @Test
    void testFirst() {

        final var nanoId = NanoId.from("test-id");
        final var locator = Locator.first(nanoId);

        assertEquals(nanoId, locator.id());
        assertEquals(1, locator.version());
    }

    @Test
    void testIncrement() {

        final var nanoId = NanoId.from("test-id");
        final var locator = new Locator(nanoId, 1);
        final var incremented = locator.increment();

        assertEquals(nanoId, incremented.id());
        assertEquals(2, incremented.version());
        assertEquals(1, locator.version());
    }

    @Test
    void testIncrementMultiple() {

        final var nanoId = NanoId.from("test-id");
        final var locator = new Locator(nanoId, 1);

        final var v2 = locator.increment();
        final var v3 = v2.increment();
        final var v4 = v3.increment();

        assertEquals(1, locator.version());
        assertEquals(2, v2.version());
        assertEquals(3, v3.version());
        assertEquals(4, v4.version());

        assertEquals(nanoId, v4.id());
    }

    @Test
    void testRecordEquality() {

        final var nanoId = NanoId.from("test-id");
        final var locator1 = new Locator(nanoId, 1);
        final var locator2 = new Locator(nanoId, 1);

        assertEquals(locator1, locator2);
        final var locator3 = new Locator(nanoId, 2);
        assertNotEquals(locator1, locator3);
    }

    @Test
    void testRecordHashCode() {

        final var nanoId = NanoId.from("test-id");
        final var locator1 = new Locator(nanoId, 1);
        final var locator2 = new Locator(nanoId, 1);

        assertEquals(locator1.hashCode(), locator2.hashCode());
        final var locator3 = new Locator(nanoId, 2);
        assertNotEquals(locator1.hashCode(), locator3.hashCode());
    }

    @Test
    void testRecordToString() {

        final var nanoId = NanoId.from("test-id");
        final var locator = new Locator(nanoId, 42);

        final var string = locator.toString();
        assertNotNull(string);
        assertTrue(string.contains("test-id"));
        assertTrue(string.contains("42"));
    }

    @Test
    void testVersionIncrement() {

        final var nanoId = NanoId.from("test-id");
        final var locator = new Locator(nanoId, 10);
        final var incremented = locator.increment();

        assertEquals(11, incremented.version());
        assertEquals(nanoId, incremented.id());
    }

    @Test
    void testFirstVersion() {

        final var nanoId = NanoId.from("test-id");
        final var locator = Locator.first(nanoId);

        assertEquals(1, locator.version());
        assertEquals(nanoId, locator.id());
    }

    @Test
    void testGenerateUniqueness() {

        final var locator1 = Locator.generate();
        final var locator2 = Locator.generate();

        assertNotEquals(locator1.id(), locator2.id());
        assertEquals(1, locator1.version());
        assertEquals(1, locator2.version());
    }

    @Test
    void testImmutability() {

        final var nanoId = NanoId.from("test-id");
        final var locator = new Locator(nanoId, 1);

        final var originalId = locator.id();
        final var originalVersion = locator.version();

        final var incremented = locator.increment();

        assertEquals(originalId, locator.id());
        assertEquals(originalVersion, locator.version());
        assertEquals(originalVersion + 1, incremented.version());
    }

    @Test
    void testIncrementFromZero() {

        final var nanoId = NanoId.from("test-id");
        final var locator = new Locator(nanoId, 0);
        final var incremented = locator.increment();

        assertEquals(0, locator.version());
        assertEquals(1, incremented.version());
    }

    @Test
    void testIncrementFromNegative() {

        final var nanoId = NanoId.from("test-id");
        final var locator = new Locator(nanoId, -1);
        final var incremented = locator.increment();

        assertEquals(-1, locator.version());
        assertEquals(0, incremented.version());
    }

    @Test
    void testEqualsWithDifferentIds() {

        final var nanoId1 = NanoId.from("test-id-1");
        final var nanoId2 = NanoId.from("test-id-2");
        final var locator1 = new Locator(nanoId1, 1);
        final var locator2 = new Locator(nanoId2, 1);

        assertNotEquals(locator1, locator2);
    }

    @Test
    void testNullId() {

        final var locator = new Locator(null, 1);

        assertNull(locator.id());
        assertEquals(1, locator.version());
    }

    @Test
    void testWithUlid() {

        final var ulid = Ulid.generate();
        final var locator = Locator.first(ulid);

        assertEquals(ulid, locator.id());
        assertEquals(1, locator.version());

        final var incremented = locator.increment();
        assertEquals(ulid, incremented.id());
        assertEquals(2, incremented.version());
    }

    @Test
    void testWithUidInterface() {

        final Uid uid = NanoId.generate();
        final var locator = Locator.first(uid);

        assertEquals(uid, locator.id());
        assertEquals(1, locator.version());
    }
}
