package dev.iq.common.version;

import dev.iq.common.adt.Coded;
import dev.iq.common.annotation.Stable;

/**
 * Represents a unique identifier.  Implementations should be immutable and thread safe.
 */
@FunctionalInterface
@Stable
public interface Uid extends Coded<String> {}
