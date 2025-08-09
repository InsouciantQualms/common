package dev.iq.common.version;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class UidFactoryTest {

    @Test
    void testFromNanoId() {
        NanoId original = NanoId.generate();
        String idString = original.code();

        Uid uid = UidFactory.from(idString);

        assertNotNull(uid);
        assertTrue(uid instanceof NanoId);
        assertEquals(idString, uid.code());
    }

    @Test
    void testFromUlid() {
        Ulid original = Ulid.generate();
        String idString = original.code();

        Uid uid = UidFactory.from(idString);

        assertNotNull(uid);
        assertTrue(uid instanceof Ulid);
        assertEquals(idString, uid.code());
    }

    @Test
    void testFromNullThrows() {
        assertThrows(NullPointerException.class, () -> UidFactory.from(null));
    }

    @Test
    void testFromInvalidLengthThrows() {
        String invalidLength = "too-short";

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> UidFactory.from(invalidLength));

        assertTrue(exception.getMessage().contains("Unknown format"));
        assertTrue(exception.getMessage().contains(invalidLength));
    }

    @Test
    void testFromEmptyStringThrows() {
        assertThrows(IllegalArgumentException.class, () -> UidFactory.from(""));
    }

    @Test
    void testFromLongStringThrows() {
        String tooLong = "this-is-way-too-long-to-be-a-valid-id-format";

        assertThrows(IllegalArgumentException.class, () -> UidFactory.from(tooLong));
    }

    @Test
    void testRoundTripNanoId() {
        NanoId original = NanoId.generate();
        Uid fromFactory = UidFactory.from(original.code());

        assertEquals(original, fromFactory);
    }

    @Test
    void testRoundTripUlid() {
        Ulid original = Ulid.generate();
        Uid fromFactory = UidFactory.from(original.code());

        assertEquals(original, fromFactory);
    }
}
