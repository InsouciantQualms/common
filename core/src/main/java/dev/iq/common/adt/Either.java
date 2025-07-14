/*
 * Insouciant Qualms © 2024 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.adt;

import java.util.function.Function;

/**
 * Encapsulates an Either monad.  The standard semantics are to treat the left value
 * as an error condition and the right value as a successful result.  However, an
 * Either can also simply store two heterogenous types.
 *
 * @param  <A>          Parameterized type of left (normally error)
 * @param  <B>          Parameterized type of right (normally success)
 */
public abstract class Either<A, B> {

    /**
     * Constructs a left-projection of an either.  Typically this will be the
     * an error during processing.
     *
     * @param  a        Value of left
     * @return Either   Left projection of either
     * @param  <A>      Parameteried type of left (error)
     * @param  <B>      Parameterized type of right (success)
     */
    public static <A, B> Either<A, B> left(final A a) {

        return new Left<>(a);
    }

    /**
     * Constructs a right-projection of an either.  Typically this will be the
     * successful result of processing.
     *
     * @param  b        Value of right
     * @return Either   Right projection of either
     * @param  <A>      Parameteried type of left (error)
     * @param  <B>      Parameterized type of right (success)
     */
    public static <A, B> Either<A, B> right(final B b) {

        return new Right<>(b);
    }

    /**
     * Returns whether the either is popualted with a left-hand value.
     *
     * @return boolean  True if left-projection
     */
    public abstract boolean isLeft();

    /**
     * Returns whether the either is popualted with a right-hand value.
     *
     * @return boolean  True if right-projection
     */
    public abstract boolean isRight();

    /**
     * Applies a function to either the left-projection or right-projection,
     * depending on which type of Either this represents.  Only one of the
     * funcitons will be executed (based on left or right).
     *
     * @param  left     Function to apply to a left-projection
     * @param  right    Function to apply to a right-projection
     * @return X        Result of processing
     * @param  <X>      Parameterized type to return from either left or right
     */
    public abstract <X> X either(final Function<A, X> left, final Function<B, X> right);

    /**
     * Represents the left projection of an Either, typically an error condition.
     *
     * @param <A>       Paramaterized type of left
     * @param <B>       Parameterized type of right
     */
    public static final class Left<A, B> extends Either<A, B> {

        /** Value. */
        private final A a;

        /**
         * Creates a left projection with the specified value.
         *
         * @param a     Value
         */
        private Left(final A a) {

            this.a = a;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isLeft() {

            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRight() {

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <X> X either(final Function<A, X> left, final Function<B, X> right) {

            return left.apply(a);
        }
    }

    /**
     * Represents the right projection of an Either, typically a successful execution result.
     *
     * @param <A>       Paramaterized type of left
     * @param <B>       Parameterized type of right
     */
    public static final class Right<A, B> extends Either<A, B> {

        /** Value. */
        private final B b;

        /**
         * Creates a right projection with the specified value.
         *
         * @param b     Value
         */
        private Right(final B b) {

            this.b = b;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isLeft() {

            return false;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isRight() {

            return true;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <X> X either(final Function<A, X> left, final Function<B, X> right) {

            return right.apply(b);
        }
    }
}
