package dev.iq.common.adt;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class TypeSafeBuilderTest {

    static FirstNameBuilder person() {

        return firstName -> lastName -> age -> new Person(firstName, lastName, age);
    }

    @Test
    public void testBuilder() {

        final var person = person().firstName("Sascha").lastName("Goldsmith").age(30);
        Assertions.assertNotNull(person);
    }

    @FunctionalInterface
    private interface FirstNameBuilder {
        LastNameBuilder firstName(String firstName);
    }

    @FunctionalInterface
    private interface LastNameBuilder {
        AgeBuilder lastName(String lastName);
    }

    @FunctionalInterface
    private interface AgeBuilder {

        Person age(int age);
    }

    private record Person(String firstName, String lastName, int age) {}
}
