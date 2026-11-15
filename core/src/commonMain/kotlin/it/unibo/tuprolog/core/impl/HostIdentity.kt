package it.unibo.tuprolog.core.impl

/** Whether [a] and [b] are the very same host value (reference identity, with value semantics for JS primitives). */
internal expect fun sameHost(
    a: Any,
    b: Any,
): Boolean

/** A hash code consistent with [sameHost], never relying on the host value's own `hashCode` or `toString`. */
internal expect fun hostIdentityHashCode(value: Any): Int

/**
 * A deterministic, within-process total order over host values, consistent with [sameHost]: it returns 0 if and only
 * if [a] and [b] are the same host value.
 */
internal expect fun compareHostIdentities(
    a: Any,
    b: Any,
): Int
