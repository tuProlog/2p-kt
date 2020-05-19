package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.nodes.RootNode
import it.unibo.tuprolog.core.Clause

internal interface ReteTree {

    val isOrdered: Boolean

    val clauses: Sequence<Clause>

    val size: Int
        get() = clauses.count()

    fun get(clause: Clause): Sequence<Clause>

    operator fun contains(clause: Clause): Boolean {
        return get(clause).any()
    }

    fun assertA(clause: Clause)

    fun assertZ(clause: Clause)

    fun retractFirst(clause: Clause): Sequence<Clause>

    fun retractOnly(clause: Clause, limit: Int): Sequence<Clause>

    fun retractAll(clause: Clause): Sequence<Clause>

    fun deepCopy(): ReteTree

    companion object {
        fun emptyUnordered(): ReteTree =
            unordered(emptyList())

        fun unordered(clauses: Iterable<Clause>): ReteTree =
            RootNode(clauses, false)

        fun unordered(vararg clauses: Clause): ReteTree =
            unordered(listOf(*clauses))

        fun emptyOrdered(): ReteTree =
            ordered(emptyList())

        fun ordered(clauses: Iterable<Clause>): ReteTree =
            RootNode(clauses, true)

        fun ordered(vararg clauses: Clause): ReteTree =
            ordered(listOf(*clauses))
    }
}