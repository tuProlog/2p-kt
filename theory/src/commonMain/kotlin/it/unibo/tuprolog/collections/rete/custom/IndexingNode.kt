package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause

internal interface IndexingNode : ReteNode, IndexingLeaf {

//    fun getGlobalFirstIndexed(clause: Clause): SituatedIndexedClause?
//
//    fun getGlobalIndexed(clause: Clause): Sequence<SituatedIndexedClause>
//
//    fun retractAllGlobalIndexed(clause: Clause): Sequence<SituatedIndexedClause>

}