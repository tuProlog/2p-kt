package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.FrozenClauseList
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A read-only [ClauseCollection] keeping clauses in order, as Prolog does: lookups yield matching clauses in that
 * order. Two queues (deques, lists) are equal when they contain equal clauses in the same order; a `Theory`, however,
 * only equals another `Theory`.
 */
interface ClauseQueue : ClauseCollection {
    /** The first clause, or `null` if empty. */
    @JsName("firstOrNull")
    fun firstOrNull(): Clause? = iterator().let { if (it.hasNext()) it.next() else null }

    /**
     * The first clause.
     * @throws NoSuchElementException if this queue is empty
     */
    @JsName("first")
    fun first(): Clause = firstOrNull() ?: throw NoSuchElementException("Empty clause collection")

    override fun toMutable(): MutableClauseQueue

    override fun toImmutable(): ClauseQueue

    companion object {
        /** An empty [ClauseQueue] matching clauses with [unificator]. */
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator = Unificator.default): ClauseQueue = of(unificator, emptyList())

        /** A [ClauseQueue] of the given clauses, in order, matching clauses with [unificator]. */
        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clauses: Clause,
        ): ClauseQueue = of(unificator, clauses.asIterable())

        /** A [ClauseQueue] of the given clauses, in order, matching clauses with [unificator]. */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): ClauseQueue = FrozenClauseList.of(unificator, clauses)

        /** A [ClauseQueue] of the given clauses, in order, matching clauses with [unificator]. */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): ClauseQueue = of(unificator, clauses.asIterable())
    }
}
