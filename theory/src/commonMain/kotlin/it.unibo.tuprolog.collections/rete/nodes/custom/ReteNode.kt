package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.core.Clause

internal interface ReteNode {

    fun get(clause: Clause) : Sequence<Clause>

    fun assertA(clause: IndexedClause)

    fun assertZ(clause: IndexedClause)

    fun retractFirst(clause: Clause) : Sequence<Clause>

    fun retractAll(clause: Clause) : Sequence<Clause>

}