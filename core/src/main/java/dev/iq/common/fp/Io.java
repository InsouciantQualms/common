/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.fp;

import dev.iq.common.adt.Either;
import dev.iq.common.error.IoException;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Encapsulates operations that have side-effects (ala functional programming).  This type behaves
 * nearly identically to Try, but this type will throw IoException instead of UnexpectedException.
 */
public final class Io {

    /** Type contains only static members. */
    private Io() {}

    /**
     * Execute the specified function and return either any errors that occurred (left) or
     * the result of successfuly processing (right).
     *
     * @param  fx           Target to execute (try-block)
     * @return Either       Result of processing (exception is left, success is right)
     * @param  <T>          Parameterized type of success value
     */
    public static <T> Either<Exception, T> withEither(final Fn0<T> fx) {

        return Try.withEither(fx);
    }

    /**
     * Execute the specified runnable, rethrowing any excetions encountered.
     *
     * @param  fx           Target to execute (try-block)
     */
    public static void withVoid(final Proc0 fx) {

        withVoid(fx, e -> {
            throw new IoException(e);
        });
    }

    /**
     * Execute the specified runnable, using the exception handler specified.
     *
     * @param  fx           Target to execute (try-block)
     * @param  ex           Exception handler (catch-block)
     */
    public static void withVoid(final Proc0 fx, final Consumer<Exception> ex) {

        withVoid(fx, ex, () -> {});
    }

    /**
     * Execute the specified runnable, using the exception handler specified and a finalzier to run in all cases.
     *
     * @param  fx           Target to execute (try-block)
     * @param  ex           Exception handler (catch-block)
     * @param  finalizer    Executes regardless of success or error (finally-block)
     */
    public static void withVoid(final Proc0 fx, final Consumer<Exception> ex, final Runnable finalizer) {

        Try.withVoid(fx, ex, finalizer);
    }

    /**
     * Execute the specified supplier, rethrowing any errors that have occurred.
     * <br/>
     * If an error occurs, the handler must not itself throw an exception and must return a value other than null.
     * If the call was instead successful, a normal result will be returned.
     *
     * @param  fx           Target to execute (try-block)
     * @param  <R>          Type to return
     * @return T            Result of successful execution or from the error handler
     */
    public static <R> R withReturn(final Fn0<R> fx) {

        return withReturn(fx, e -> {
            throw new IoException(e);
        });
    }

    /**
     * Execute the specified supplier, using the exception handler specified.
     * <br/>
     * If an error occurs, the handler must not itself throw an exception and must return a value other than null.
     * If the call was instead successful, a normal result will be returned.
     *
     * @param  fx           Target to execute (try-block)
     * @param  ex           Exception handler (catch-block)
     * @param  <R>          Type to return
     * @return T            Result of successful execution or from the error handler
     */
    public static <R> R withReturn(final Fn0<? extends R> fx, final Function<Exception, R> ex) {

        return Try.withReturn(fx, ex, () -> {});
    }

    /**
     * Execute the specified supplier, using the exception handler specified and a finalzier to run in all cases.
     * <br/>
     * If an error occurs, the handler must not itself throw an exception and must return a value other than null.
     * If the call was instead successful, a normal result will be returned.
     *
     * @param  fx           Target to execute (try-block)
     * @param  ex           Exception handler (catch-block)
     * @param  finalizer    Executes regardless of success or error (finally-block)
     * @param  <R>          Type to return
     * @return T            Result of successful execution or from the error handler
     */
    public static <R> R withReturn(final Fn0<? extends R> fx, final Function<Exception, R> ex, final Runnable finalizer) {

        return Try.withReturn(fx, ex, finalizer);
    }
}
