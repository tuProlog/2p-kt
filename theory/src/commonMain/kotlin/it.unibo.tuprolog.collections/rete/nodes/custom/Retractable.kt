package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.collections.rete.nodes.custom.clause.SituatedIndexedClause

internal interface Retractable {

    fun retractIndexed(indexed: SituatedIndexedClause): Unit

}