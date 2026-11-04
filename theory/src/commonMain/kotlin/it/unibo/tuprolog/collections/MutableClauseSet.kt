package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.MutableClauseSetImpl
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** A [ClauseSet] that can be changed in place: [add] returns `false` for clauses that are variants of present ones. */
interface MutableClauseSet :
    ClauseSet,
    MutableClauseMultiSet {
    companion object {
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator = Unificator.default): MutableClauseSet = of(unificator, emptyList())

        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clauses: Clause,
        ): MutableClauseSet = of(unificator, clauses.asIterable())

        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): MutableClauseSet = MutableClauseSetImpl.of(unificator, clauses)

        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): MutableClauseSet = of(unificator, clauses.asIterable())
    }
}
