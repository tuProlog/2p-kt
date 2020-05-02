package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.ReteClauseDeque
import it.unibo.tuprolog.collections.impl.ReteClauseMultiSet
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.theory.IndexedClauseDatabase

interface ClauseMultiset : ClauseCollection {

    /** Gives the number of [Clause] that would unify over the given clause. **/
    fun count(clause: Clause): Long

    /** Produces a [Sequence] of the clauses that would unify over the given [Clause]. **/
    operator fun get(clause: Clause): Sequence<Clause>

    /** Gives a freshly produced [ClauseMultiset] including the given [Clause] and the content of this one **/
    override fun add(clause: Clause): ClauseMultiset

    /** Gives a freshly produced [ClauseMultiset] including all the given [Clause] and the content of this one **/
    override fun addAll(clauses: Iterable<Clause>): ClauseMultiset

    /** Produces a [RetrieveResult] as a consequence of the attempt at deleting the given [Clause]
     *  from this [ClauseMultiset] **/
    override fun retrieve(clause: Clause): RetrieveResult<out ClauseMultiset>

    /** Produces a [RetrieveResult] as a consequence of the attempt at deleting all the given [Clause]
     *  from this [ClauseMultiset] **/
    override fun retrieveAll(clause: Clause): RetrieveResult<out ClauseMultiset>

    companion object {

        /** Creates an empty [ClauseMultiset] **/
        fun empty(): ClauseMultiset = of(emptyList())

        /** Creates a [ClauseMultiset] with given clauses */
        fun of(vararg clause: Clause): ClauseMultiset = of(clause.asIterable())

        /** Let developers easily create a [ClauseMultiset] programmatically while avoiding variables names clashing */
        fun of(vararg clause: Scope.() -> Clause): ClauseMultiset =
            of(clause.map {
                Scope.empty(it)
            })

        /** Creates a [ClauseMultiset] from the given [Sequence] of [Clause] */
        fun of(clauses: Sequence<Clause>): ClauseMultiset = of(clauses.asIterable())

        /** Creates a [ClauseMultiset] from the given [Iterable] of [Clause] */
        fun of(clauses: Iterable<Clause>): ClauseMultiset =
            ReteClauseMultiSet(clauses)
    }
}

