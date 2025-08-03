/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.adt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;
import org.junit.jupiter.api.Test;

/** Tests for the Either monad implementation covering left and right projections. */
final class EitherTest {

    @Test
    void testLeftProjection() {

        final var either = Either.<String, Integer>left("error");

        assertTrue(either.isLeft());
        assertFalse(either.isRight());
    }

    @Test
    void testRightProjection() {

        final var either = Either.<String, Integer>right(42);

        assertFalse(either.isLeft());
        assertTrue(either.isRight());
    }

    @Test
    void testEitherWithLeftValue() {

        final var either = Either.<String, Integer>left("error");
        final Function<String, String> leftFunction = s -> "Left: " + s;
        final Function<Integer, String> rightFunction = i -> "Right: " + i;

        final var result = either.either(leftFunction, rightFunction);

        assertEquals("Left: error", result);
    }

    @Test
    void testEitherWithRightValue() {

        final var either = Either.<String, Integer>right(42);
        final Function<String, String> leftFunction = s -> "Left: " + s;
        final Function<Integer, String> rightFunction = i -> "Right: " + i;

        final var result = either.either(leftFunction, rightFunction);

        assertEquals("Right: 42", result);
    }

    @Test
    void testEitherWithNullValues() {

        final var leftEither = Either.<String, Integer>left(null);
        final var rightEither = Either.<String, Integer>right(null);

        assertTrue(leftEither.isLeft());
        assertTrue(rightEither.isRight());

        final var leftResult = leftEither.either(s -> "null left", i -> "null right");
        final var rightResult = rightEither.either(s -> "null left", i -> "null right");

        assertEquals("null left", leftResult);
        assertEquals("null right", rightResult);
    }

    @Test
    void testEitherWithDifferentTypes() {

        final var stringNumberEither = Either.<String, Double>right(3.14);
        final var booleanListEither = Either.<Boolean, String>left(true);

        assertTrue(stringNumberEither.isRight());
        assertTrue(booleanListEither.isLeft());

        final var stringResult = stringNumberEither.either(String::length, Double::intValue);
        final var booleanResult = booleanListEither.either(b -> b ? "true" : "false", Function.identity());

        assertEquals(3, stringResult);
        assertEquals("true", booleanResult);
    }

    @Test
    void testEitherFunctionReturnsCorrectType() {

        final var leftEither = Either.<String, Integer>left("test");
        final var rightEither = Either.<String, Integer>right(100);

        final var leftResult = leftEither.either(String::length, Integer::doubleValue);
        final var rightResult = rightEither.either(String::length, Integer::doubleValue);

        assertEquals(4, leftResult);
        assertEquals(100.0, rightResult);
    }

    @Test
    void testEitherWithComplexTransformations() {

        final var either = Either.<Exception, String>right("hello world");

        final var result = either.either(
                ex -> "Error: " + ex.getMessage(), str -> str.toUpperCase().replace(" ", "_"));

        assertEquals("HELLO_WORLD", result);
    }

    @Test
    void testEitherLeftProjectionWithException() {

        final var exception = new IllegalArgumentException("Invalid input");
        final var either = Either.<Exception, String>left(exception);

        final var result = either.either(ex -> "Exception: " + ex.getClass().getSimpleName(), str -> "Success: " + str);

        assertEquals("Exception: IllegalArgumentException", result);
    }
}
