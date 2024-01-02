package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause

/**Functional Interface aimed at marking a particular member of the [ReteTree] able of effectfully remove
 * the given [SituatedIndexedClause] from the storage*/
internal interface Retractable {
    /**Removes the given [SituatedIndexedClause] from the storage of this node*/
    fun retractIndexed(indexed: SituatedIndexedClause)
}
