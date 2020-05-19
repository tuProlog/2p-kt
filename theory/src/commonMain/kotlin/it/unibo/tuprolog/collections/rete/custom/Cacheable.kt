package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause

internal interface Cacheable {

    fun getCache(): Sequence<SituatedIndexedClause>

}