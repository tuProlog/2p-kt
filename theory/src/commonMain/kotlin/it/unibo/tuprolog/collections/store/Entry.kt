package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause

/** A clause in a [MutableStore], with its position in the global order and its lifetime. */
internal class Entry(
    val clause: Clause,
    val order: Long,
    val born: Long,
) {
    /** The generation this entry was removed in; [Long.MAX_VALUE] while it is alive. [born] is the one it was added in. */
    var erased: Long = Long.MAX_VALUE

    /** Whether this entry has not been removed. */
    val isAlive: Boolean get() = erased == Long.MAX_VALUE

    /** Whether a lookup started at [generation] sees this entry: added at or before it, and removed after it. */
    fun isVisibleAt(generation: Long): Boolean = born <= generation && generation < erased
}
