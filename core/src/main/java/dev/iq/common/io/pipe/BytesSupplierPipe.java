/*
 * Insouciant Qualms © 2024 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.pipe;

import dev.iq.common.fp.Fn0;
import dev.iq.common.fp.Io;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Pipe implementation that reads from InputStream, writes to OutputStream
 * and works with byte[] values.  All suppliers are lazily evaluated.
 * <br/>
 * This implementation will close the streams passed in.
 */
final class BytesSupplierPipe implements Pipe<byte[], Fn0<? extends InputStream>, Fn0<? extends OutputStream>> {

    /** Delegate for byte-based operations. */
    private static final BytesPipe delegate = new BytesPipe();

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] read(final Fn0<? extends InputStream> in) {

        return Io.withReturn(() -> {
            try (final var stream = in.get()) {
                return delegate.read(stream);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte[] target, final Fn0<? extends OutputStream> out) {

        Io.withVoid(() -> {
            try (final var stream = out.get()) {
                delegate.write(target, stream);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long go(final Fn0<? extends InputStream> in, final Fn0<? extends OutputStream> out, final int bufferSize) {

        return Io.withReturn(() -> {
            try (final var streamIn = in.get(); final var streamOut = out.get()) {
                return delegate.go(streamIn, streamOut, bufferSize);
            }
        });
    }
}
