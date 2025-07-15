/*
 * Insouciant Qualms © 2025 by Sascha Goldsmith is licensed under CC BY 4.0.
 * To view a copy of this license, visit https://creativecommons.org/licenses/by/4.0.
 * To reach the creator, visit https://www.linkedin.com/in/saschagoldsmith.
 */

package dev.iq.common.io.pipe;

import dev.iq.common.fp.Io;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Pipe implementation that reads from InputStream, writes to OutputStream
 * and works with byte[] values.
 * <br/>
 * This implementation does not close the streams passed in.
 */
final class BytesPipe implements Pipe<byte[], InputStream, OutputStream> {

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] read(final InputStream in) {

        return Io.withReturn(() -> {
            try (final var out = new ByteArrayOutputStream()) {
                go(in, out);
                return out.toByteArray();
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(final byte[] target, final OutputStream out) {

        Io.withVoid(() -> {
            try (final var byteIn = new ByteArrayInputStream(target)) {
                go(byteIn, out);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("ReassignedVariable")
    public long go(final InputStream in, final OutputStream out, final int bufferSize) {

        return Io.withReturn(() -> {
            var total = 0L;
            final var buffer = new byte[bufferSize];
            var bytesRead = in.read(buffer);
            while (bytesRead >= 0) {
                if (bytesRead > 0) {
                    out.write(buffer, 0, bytesRead);
                    total += bytesRead;
                }
                bytesRead = in.read(buffer);
            }
            out.flush();
            return total;
        });
    }
}
