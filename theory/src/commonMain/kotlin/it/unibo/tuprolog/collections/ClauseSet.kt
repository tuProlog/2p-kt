package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.FrozenClauseSet
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A [ClauseMultiSet] where no two clauses are variants of each other (see [ClauseCollection.containsVariant]):
 * clauses that are variants of one already present are not added.
 */
interface ClauseSet : ClauseMultiSet {
    override fun toMutable(): MutableClauseSet

    override fun toImmutable(): ClauseSet

    companion object {
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator = Unificator.default): ClauseSet = of(unificator, emptyList())

        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clauses: Clause,
        ): ClauseSet = of(unificator, clauses.asIterable())

        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): ClauseSet = FrozenClauseSet.of(unificator, clauses)

        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): ClauseSet = of(unificator, clauses.asIterable())
    }
}
