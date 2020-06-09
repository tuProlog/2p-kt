package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause

/**Denotes a [ReteNode] capable of managing intermediate operation upon its children,
 * so that the results these will provide can be more fine grained elaborated*/
internal interface IndexingLeaf : ReteNode {

    /**Read the single first [SituatedIndexedClause] matching the given [Clause], returning null if
     * such a condition is never met. The concept of "first" may vary between implementations */
    fun getFirstIndexed(clause: Clause): SituatedIndexedClause?

    /** Returns a [Sequence] of [SituatedIndexedClause] based on the union of all the matching claues
     * available to this node
     * */
    fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause>

    /**Retracts all the clauses matching the given [Clause] as a [Sequence] of [SituatedIndexedClause]*/
    fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause>

    /**Variant of [getIndexed] aimed at preventing a proper traversal of the children of this node,
     * retrieving all the clauses matching the given [Clause] without performing any branch selection*/
    fun extractGlobalIndexedSequence(clause: Clause): Sequence<SituatedIndexedClause>

}