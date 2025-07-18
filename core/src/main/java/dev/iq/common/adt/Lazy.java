/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.adt;

import dev.iq.common.error.Invariant;
import dev.iq.common.fp.Fn0;
import dev.iq.common.fp.Io;
import dev.iq.common.lock.SimpleLock;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Indicates the type is lazy loadable. The specified supplier will only be called once for the
 * first get() and then memoize the response for subsequent calls.
 */
public final class Lazy<T> {

    /**
     * Detects a recursive initialization within the same thread on the same instance (not static
     * since check is per instance).
     */
    @SuppressWarnings("ThreadLocalNotStaticFinal")
    private final ThreadLocal<Boolean> initializing = ThreadLocal.withInitial(() -> false);

    /** Flag indicating whether loaded (memoized) or not. */
    private final AtomicBoolean loaded = new AtomicBoolean(false);

    /** Re-entrant lock to ensure that concurrent operations see a consistent value. */
    private final SimpleLock lock = SimpleLock.reentrant();

    /** Supplier of the value (evaluated lazily on a call to get()). */
    private final Fn0<T> supplier;

    /** Cached (memoized) value. */
    private T value = null;

    /** Private constructor. Use factory methods to instantiate. */
    private Lazy(final Fn0<T> supplier) {

        this.supplier = supplier;
    }

    /**
     * Creates a lazily evaluated value that will invoke the supplied function only once, on the
     * first call of get().
     */
    public static <T> Lazy<T> of(final Fn0<T> supplier) {

        return new Lazy<>(supplier);
    }

    /**
     * Returns whether the type has been loaded.
     *
     * @return boolean True if previously loaded
     */
    public boolean loaded() {

        return loaded.get();
    }

    /** Return the underlying type using the supplied originally specified. */
    public T get() {

        if (!loaded()) {
            load();
        }
        return value;
    }

    /**
     * Load the value from the supplier and ensure that we do not cause deadlock or allow recursive
     * calls to stack overflow.
     */
    private void load() {

        Invariant.requireTrue(!initializing.get(), "Recursive call to Lazy.get() on same instance in the same thread");
        lock.withVoid(() -> {
            if (!loaded.get()) {
                initializing.set(true);
                try {
                    value = Io.withReturn(supplier);
                    loaded.set(true);
                } finally {
                    initializing.set(false);
                }
            }
        });
    }
}
