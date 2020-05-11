package it.unibo.tuprolog.collections.rete.nodes.engineered

import it.unibo.tuprolog.core.Clause

interface IndexingLeaf {

    fun get(clause: Clause): Sequence<Clause>

    fun assertA(clause: IndexedClause)

    fun assertZ(clause: IndexedClause)

    fun getFirst(clause: Clause): IndexedClause?

    fun getAny(clause: Clause): IndexedClause?

    fun getIndexed(clause: Clause): Sequence<IndexedClause>

    fun retractAll(clause: Clause): Sequence<Clause>

    fun retractAllIndexed(clause: Clause): Sequence<IndexedClause>

    fun retractIndexed(indexed: IndexedClause): Sequence<Clause>

}