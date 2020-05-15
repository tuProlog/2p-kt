package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause

internal interface Retractable {

    fun retractIndexed(indexed: SituatedIndexedClause): Unit

}