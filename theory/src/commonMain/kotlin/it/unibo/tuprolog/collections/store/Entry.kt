package it.unibo.tuprolog.collections.store

import it.unibo.tuprolog.core.Clause

/** A clause in a [MutableStore], with its position in the global order and its lifetime. */
internal class Entry(
    val clause: Clause,
    val order: Long,
    val born: Long,
) {
    var erased: Long = Long.MAX_VALUE

    val isAlive: Boolean get() = erased == Long.MAX_VALUE

    fun isVisibleAt(generation: Long): Boolean = born <= generation && generation < erased
}
