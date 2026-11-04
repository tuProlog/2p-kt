package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.FrozenClauseMultiSet
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A read-only [ClauseCollection] promising no particular order, where the same clause may occur more than once.
 * Two multisets are equal when they contain equal clauses with the same multiplicities.
 */
interface ClauseMultiSet : ClauseCollection {
    override fun toMutable(): MutableClauseMultiSet

    override fun toImmutable(): ClauseMultiSet

    companion object {
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator = Unificator.default): ClauseMultiSet = of(unificator, emptyList())

        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clauses: Clause,
        ): ClauseMultiSet = of(unificator, clauses.asIterable())

        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): ClauseMultiSet = FrozenClauseMultiSet.of(unificator, clauses)

        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): ClauseMultiSet = of(unificator, clauses.asIterable())
    }
}
