package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.MutableClauseListImpl
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/** A [ClauseList] that can be changed in place, also by position (in O(n)). */
interface MutableClauseList :
    ClauseList,
    MutableClauseDeque {
    /**
     * Inserts [clause] at [index] (from 0 to [size], included), in O(n).
     * @throws IndexOutOfBoundsException if [index] is outside that range
     */
    @JsName("addAt")
    fun add(
        index: Int,
        clause: Clause,
    )

    /**
     * Replaces the clause at [index] with [clause], returning the replaced one, in O(n).
     * @throws IndexOutOfBoundsException if there is no clause at [index]
     */
    @JsName("set")
    operator fun set(
        index: Int,
        clause: Clause,
    ): Clause

    /**
     * Removes and returns the clause at [index], in O(n).
     * @throws IndexOutOfBoundsException if there is no clause at [index]
     */
    @JsName("removeAt")
    fun removeAt(index: Int): Clause

    companion object {
        /** A new, empty [MutableClauseList] matching clauses with [unificator]. */
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator = Unificator.default): MutableClauseList = of(unificator, emptyList())

        /** A new [MutableClauseList] of the given clauses, in order, matching clauses with [unificator]. */
        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clauses: Clause,
        ): MutableClauseList = of(unificator, clauses.asIterable())

        /** A new [MutableClauseList] of the given clauses, in order, matching clauses with [unificator]. */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): MutableClauseList = MutableClauseListImpl.of(unificator, clauses)

        /** A new [MutableClauseList] of the given clauses, in order, matching clauses with [unificator]. */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): MutableClauseList = of(unificator, clauses.asIterable())
    }
}
