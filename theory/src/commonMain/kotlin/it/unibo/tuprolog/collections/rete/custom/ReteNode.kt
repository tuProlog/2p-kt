package it.unibo.tuprolog.collections.rete.custom

import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import kotlin.js.JsName

/**A data structure to manage the basic behaviour of the [ReteTree]*/
internal interface ReteNode : Cacheable<SituatedIndexedClause> {

    @JsName("unificator")
    val unificator: Unificator

    val size: Int

    val isEmpty: Boolean

    /**Reads all the clauses matching the given [Clause]*/
    fun get(clause: Clause): Sequence<Clause>

    /**Insert an [IndexedClause] at the beginning of the rightful place it should be stored in*/
    fun assertA(clause: IndexedClause)

    /**Insert an [IndexedClause] at the end of the rightful place it should be stored in*/
    fun assertZ(clause: IndexedClause)

    /**Removes all the clauses matching the given [Clause], returning all of them as a [Sequence] of [Clause]*/
    fun retractAll(clause: Clause): Sequence<Clause>
}
