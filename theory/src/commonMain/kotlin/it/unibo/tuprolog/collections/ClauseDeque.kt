package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.FrozenClauseList
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** A [ClauseQueue] accessible at both ends. */
interface ClauseDeque : ClauseQueue {
    /** The last clause, or `null` if empty. */
    @JsName("lastOrNull")
    fun lastOrNull(): Clause? = clauses.lastOrNull()

    /** The last clause; throws [NoSuchElementException] if empty. */
    @JsName("last")
    fun last(): Clause = lastOrNull() ?: throw NoSuchElementException("Empty clause collection")

    override fun toMutable(): MutableClauseDeque

    override fun toImmutable(): ClauseDeque

    companion object {
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator = Unificator.default): ClauseDeque = of(unificator, emptyList())

        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clauses: Clause,
        ): ClauseDeque = of(unificator, clauses.asIterable())

        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): ClauseDeque = FrozenClauseList.of(unificator, clauses)

        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): ClauseDeque = of(unificator, clauses.asIterable())
    }
}
