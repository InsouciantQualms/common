package dev.iq.common.adt;

import java.lang.annotation.*;

/**
 * Marks any element type in a source file as stable.  This is used for guidance to developers
 * and coding agents to prompt before modifying the annotated element type.
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
public @interface Stable {}
