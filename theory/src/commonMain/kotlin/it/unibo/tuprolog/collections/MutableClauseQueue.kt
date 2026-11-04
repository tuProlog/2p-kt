package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.MutableClauseListImpl
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** A [ClauseQueue] that can be changed in place: clauses are added at the end, and removed from the beginning. */
interface MutableClauseQueue :
    ClauseQueue,
    MutableClauseCollection {
    /** Adds [clause] at the end (same as [add]). */
    @JsName("addLast")
    fun addLast(clause: Clause) {
        add(clause)
    }

    /** Removes and returns the first clause, or `null` if empty. */
    @JsName("removeFirst")
    fun removeFirst(): Clause?

    companion object {
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator = Unificator.default): MutableClauseQueue = of(unificator, emptyList())

        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clauses: Clause,
        ): MutableClauseQueue = of(unificator, clauses.asIterable())

        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): MutableClauseQueue = MutableClauseListImpl.of(unificator, clauses)

        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): MutableClauseQueue = of(unificator, clauses.asIterable())
    }
}
