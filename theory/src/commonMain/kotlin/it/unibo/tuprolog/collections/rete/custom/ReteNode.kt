package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.core.Clause

internal interface ReteNode : Cacheable {

    fun get(clause: Clause): Sequence<Clause>

    fun assertA(clause: IndexedClause)

    fun assertZ(clause: IndexedClause)

    fun retractAll(clause: Clause): Sequence<Clause>

}