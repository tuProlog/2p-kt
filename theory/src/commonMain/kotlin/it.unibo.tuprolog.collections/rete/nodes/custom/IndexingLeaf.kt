package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.core.Clause

internal interface IndexingLeaf : ReteNode{

    fun getFirst(clause: Clause): IndexedClause?

    fun getAny(clause: Clause): IndexedClause?

    fun getIndexed(clause: Clause): Sequence<IndexedClause>

    fun retractAllIndexed(clause: Clause): Sequence<IndexedClause>

    fun retractIndexed(indexed: IndexedClause): Sequence<Clause>

}