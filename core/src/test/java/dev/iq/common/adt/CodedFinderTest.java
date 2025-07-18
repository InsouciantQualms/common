/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.adt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** Tests for the CodedFinder utility class. */
final class CodedFinderTest {

    @Test
    public void testFindRequired() {

        final var found = CodedFinder.require("NJ", CodedEnum.class);
        Assertions.assertEquals(CodedEnum.NEW_JERSEY, found);
    }

    private enum CodedEnum implements Coded<String> {
        MICHIGAN("MI"),
        NEW_JERSEY("NJ");

        private final String code;

        CodedEnum(final String code) {

            this.code = code;
        }

        @Override
        public String getCode() {

            return code;
        }
    }
}
