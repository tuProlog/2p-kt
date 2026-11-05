package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.MutableClauseListImpl
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** A [ClauseDeque] that can be changed in place at both ends. */
interface MutableClauseDeque :
    ClauseDeque,
    MutableClauseQueue {
    /** Adds [clause] at the beginning. */
    @JsName("addFirst")
    fun addFirst(clause: Clause)

    /** Removes and returns the last clause, or `null` if empty. */
    @JsName("removeLast")
    fun removeLast(): Clause?

    companion object {
        /** A new, empty [MutableClauseDeque] matching clauses with [unificator]. */
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator = Unificator.default): MutableClauseDeque = of(unificator, emptyList())

        /** A new [MutableClauseDeque] of the given clauses, in order, matching clauses with [unificator]. */
        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clauses: Clause,
        ): MutableClauseDeque = of(unificator, clauses.asIterable())

        /** A new [MutableClauseDeque] of the given clauses, in order, matching clauses with [unificator]. */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): MutableClauseDeque = MutableClauseListImpl.of(unificator, clauses)

        /** A new [MutableClauseDeque] of the given clauses, in order, matching clauses with [unificator]. */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): MutableClauseDeque = of(unificator, clauses.asIterable())
    }
}
