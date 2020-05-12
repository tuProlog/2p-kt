package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.core.Clause

internal interface RootNode {

    val operationalOrder: Boolean

    fun theory() : Sequence<Clause>

    fun get(clause: Clause): Sequence<Clause>

    fun assertA(clause: Clause)

    fun assertZ(clause: Clause)

    fun retractFirst(clause: Clause): Sequence<Clause>

    fun retractOnly(clause: Clause, limit: Int): Sequence<Clause>

    fun retractAll(clause: Clause): Sequence<Clause>

    fun deepCopy(): RootNode

    companion object {
        fun emptyMultiset() : RootNode =
            multiset(emptyList())

        fun multiset(clauses: Iterable<Clause>) : RootNode =
            FamilyRootNode(clauses, false)

        fun multiset(vararg clauses: Clause) : RootNode =
            multiset(*clauses)

        fun emptyOrdered() : RootNode =
            ordered(emptyList())

        fun ordered(clauses: Iterable<Clause>) : RootNode =
            FamilyRootNode(clauses, true)

        fun ordered(vararg clauses: Clause) : RootNode =
            ordered(*clauses)
    }
}