package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.FrozenClauseList
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName
import kotlin.jvm.JvmStatic

/**
 * A [ClauseDeque] with positional access. Immutable lists access positions in O(1); mutable ones in O(n), as their
 * storage is optimised for updates at both ends and for lookups by unification.
 */
interface ClauseList : ClauseDeque {
    /**
     * The clause at [index].
     * @throws IndexOutOfBoundsException if there is no clause at [index]
     */
    @JsName("getAt")
    operator fun get(index: Int): Clause = asList()[index]

    /** The position of the first clause unifying with [query], or -1 if none does. */
    @JsName("indexOfFirstMatching")
    fun indexOfFirstMatching(query: Clause): Int {
        val match = firstMatching(query) ?: return -1
        return clauses.indexOfFirst { it === match }
    }

    /** A read-only list view of these clauses (a snapshot, for mutable lists). */
    @JsName("asList")
    fun asList(): List<Clause>

    override fun toMutable(): MutableClauseList

    override fun toImmutable(): ClauseList

    companion object {
        /** An empty [ClauseList] matching clauses with [unificator]. */
        @JvmStatic
        @JsName("empty")
        fun empty(unificator: Unificator = Unificator.default): ClauseList = of(unificator, emptyList())

        /** A [ClauseList] of the given clauses, in order, matching clauses with [unificator]. */
        @JvmStatic
        @JsName("of")
        fun of(
            unificator: Unificator,
            vararg clauses: Clause,
        ): ClauseList = of(unificator, clauses.asIterable())

        /** A [ClauseList] of the given clauses, in order, matching clauses with [unificator]. */
        @JvmStatic
        @JsName("ofIterable")
        fun of(
            unificator: Unificator,
            clauses: Iterable<Clause>,
        ): ClauseList = FrozenClauseList.of(unificator, clauses)

        /** A [ClauseList] of the given clauses, in order, matching clauses with [unificator]. */
        @JvmStatic
        @JsName("ofSequence")
        fun of(
            unificator: Unificator,
            clauses: Sequence<Clause>,
        ): ClauseList = of(unificator, clauses.asIterable())
    }
}
