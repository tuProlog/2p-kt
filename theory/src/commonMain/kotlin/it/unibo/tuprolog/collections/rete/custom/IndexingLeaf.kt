package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause

internal interface IndexingLeaf : ReteNode{

    fun getFirstIndexed(clause: Clause): SituatedIndexedClause?

    fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause>

    fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause>

}