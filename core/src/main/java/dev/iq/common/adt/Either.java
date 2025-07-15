/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.adt;

import java.util.function.Function;

/**
 * Encapsulates an Either monad.  The standard semantics are to treat the left value
 * as an error condition and the right value as a successful result.  However, an
 * Either can also simply store two heterogenous types.
 * This class is immutable and thread-safe.
 */
public abstract class Either<A, B> {

    /**
     * Constructs a left-projection of an either.  Typically this will be the
     * an error during processing.
     */
    public static <A, B> Either<A, B> left(final A a) {

        return new Left<>(a);
    }

    /**
     * Constructs a right-projection of an either.  Typically this will be the
     * successful result of processing.
     */
    public static <A, B> Either<A, B> right(final B b) {

        return new Right<>(b);
    }

    /**
     * Returns whether the either is populated with a left-hand value.
     */
    public abstract boolean isLeft();

    /**
     * Returns whether the either is populated with a right-hand value.
     */
    public abstract boolean isRight();

    /**
     * Applies a function to either the left-projection or right-projection,
     * depending on which type of Either this represents.  Only one of the
     * functions will be executed (based on left or right).
     */
    public abstract <X> X either(final Function<A, X> left, final Function<B, X> right);

    /**
     * Represents the left projection of an Either, typically an error condition.
     * This class is immutable and thread-safe.
     */
    public static final class Left<A, B> extends Either<A, B> {

        /** Value. */
        private final A a;

        /**
         * Creates a left projection with the specified value.
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
     * This class is immutable and thread-safe.
     */
    public static final class Right<A, B> extends Either<A, B> {

        /** Value. */
        private final B b;

        /**
         * Creates a right projection with the specified value.
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
