package dev.iq.common.adt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
