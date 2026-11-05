package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.MutableClauseMultiSetImpl
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** A [ClauseMultiSet] that can be changed in place. */
interface MutableClauseMultiSet :
    ClauseMultiSet,
    MutableClauseCollection {
    companion object {
        /** A new, empty [MutableClauseMultiSet] matching clauses with [unificator]. */
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator = Unificator.default): MutableClauseMultiSet = of(unificator, emptyList())

        /**
         * A new [MutableClauseMultiSet] of the given clauses, matching clauses with [unificator], skipping those that
         * are variants of a clause already added.
         */
        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clauses: Clause,
        ): MutableClauseMultiSet = of(unificator, clauses.asIterable())

        /**
         * A new [MutableClauseMultiSet] of the given clauses, matching clauses with [unificator], skipping those that
         * are variants of a clause already added.
         */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): MutableClauseMultiSet = MutableClauseMultiSetImpl.of(unificator, clauses)

        /**
         * A new [MutableClauseMultiSet] of the given clauses, matching clauses with [unificator], skipping those that
         * are variants of a clause already added.
         */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): MutableClauseMultiSet = of(unificator, clauses.asIterable())
    }
}
