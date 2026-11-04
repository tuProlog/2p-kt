package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.core.Clause

/** A clause stored as a key, compared by identity: variants are told apart by the store, not by the value map. */
internal class KeyRef(
    val clause: Clause,
) {
    override fun equals(other: Any?): Boolean = other is KeyRef && other.clause === clause

    override fun hashCode(): Int = clause.hashCode()
}
