/*
 * Insouciant Qualms © 2024 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.adt;

/**
 * Internal interface for returning a builder.  Calling build() will return
 * either a StreamEncrypter or a StreamDecyrpter, depending on the factory
 * method that created the Builder.
 *
 * @param <T>                   Type that is built
 */
@FunctionalInterface
public interface Builder<T> {

    /**
     * Causes the builder to return a fully built instance of T.
     *
     * @return T               Built type
     */
    T build();
}
