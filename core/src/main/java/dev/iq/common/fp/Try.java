/*
 * Insouciant Qualms © 2024 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.fp;

import dev.iq.common.adt.Either;
import dev.iq.common.error.UnexpectedException;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Semi-functional (as in functional programming, the class is fully working) implementation that allows
 * callers to safely handle code that may throw an exception without using try-catch blocks themselves.
 */
public final class Try {

    /**
     * Private constructor.  Type contains only static members.
     */
    private Try() {}

    /**
     * Execute the specified function and return either any errors that occurred (left) or
     * the result of successfuly processing (right).
     *
     * @param fx  Target to execute (try-block)
     * @param <T> Parameterized type of success value
     * @return Either       Result of processing (exception is left, success is right)
     */
    public static <T> Either<Exception, T> withEither(final Fn0<? extends T> fx) {

        try {
            return Either.right(fx.get());
        } catch (final Exception e) {
            return Either.left(e);
        }
    }

    /**
     * Execute the specified runnable, rethrowing any excetions encountered.
     *
     * @param fx Target to execute (try-block)
     */
    public static void withVoid(final Proc0 fx) {

        withVoid(fx, e -> {
                throw new UnexpectedException(e);
            }
        );
    }

    /**
     * Execute the specified runnable, using the exception handler specified.
     *
     * @param fx Target to execute (try-block)
     * @param ex Exception handler (catch-block)
     */
    public static void withVoid(final Proc0 fx, final Consumer<Exception> ex) {

        withVoid(fx, ex, () -> {});
    }

    /**
     * Execute the specified runnable, using the exception handler specified and a finalzier to run in all cases.
     *
     * @param fx        Target to execute (try-block)
     * @param ex        Exception handler (catch-block)
     * @param finalizer Executes regardless of success or error (finally-block)
     */
    public static void withVoid(final Proc0 fx, final Consumer<Exception> ex, final Runnable finalizer) {

        try {
            fx.run();
        } catch (final Exception e) {
            ex.accept(e);
        } finally {
            finalizer.run();
        }
    }

    /**
     * Execute the specified supplier, rethrowing any errors that have occurred.
     * <br/>
     * If an error occurs, the handler must not itself throw an exception and must return a value other than null.
     * If the call was instead successful, a normal result will be returned.
     *
     * @param fx  Target to execute (try-block)
     * @param <R> Type to return
     * @return T            Result of successful execution or from the error handler
     */
    public static <R> R withReturn(final Fn0<R> fx) {

        return withReturn(fx, e -> {
                throw new UnexpectedException(e);
            }
        );
    }

    /**
     * Execute the specified supplier, using the exception handler specified.
     * <br/>
     * If an error occurs, the handler must not itself throw an exception and must return a value other than null.
     * If the call was instead successful, a normal result will be returned.
     *
     * @param fx  Target to execute (try-block)
     * @param ex  Exception handler (catch-block)
     * @param <R> Type to return
     * @return T            Result of successful execution or from the error handler
     */
    public static <R> R withReturn(final Fn0<? extends R> fx, final Function<Exception, R> ex) {

        return withReturn(fx, ex, () -> {});
    }

    /**
     * Execute the specified supplier, using the exception handler specified and a finalzier to run in all cases.
     * <br/>
     * If an error occurs, the handler must not itself throw an exception and must return a value other than null.
     * If the call was instead successful, a normal result will be returned.
     *
     * @param fx        Target to execute (try-block)
     * @param ex        Exception handler (catch-block)
     * @param finalizer Executes regardless of success or error (finally-block)
     * @param <R>       Type to return
     * @return T            Result of successful execution or from the error handler
     */
    public static <R> R withReturn(final Fn0<? extends R> fx, final Function<Exception, R> ex, final Runnable finalizer) {

        try {
            return fx.get();
        } catch (final Exception e) {
            return ex.apply(e);
        } finally {
            finalizer.run();
        }
    }
}
