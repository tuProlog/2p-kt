package it.unibo.tuprolog.collections

import it.unibo.tuprolog.core.Clause

interface ClauseMultiset : ClauseCollection {

    /** Gives the number of [Clause] that would unify over the given clause. **/
    fun count(clause: Clause): Long

    /** Produces a [Sequence] of the clauses that would unify over the given [Clause]. **/
    operator fun get(clause: Clause): Sequence<Clause>

    /** Gives a freshly produced [ClauseMultiset] including the given [Clause] and the content of this one **/
    override fun add(clause: Clause): ClauseMultiset

    /** Gives a freshly produced [ClauseMultiset] including all the given [Clause] and the content of this one **/
    override fun addAll(clause: Iterable<Clause>): ClauseMultiset

    /** Produces a [RetrieveResult] as a consequence of the attempt at deleting the given [Clause]
     *  from this [ClauseMultiset] **/
    override fun retrieve(clause: Clause): RetrieveResult<out ClauseMultiset>

    /** Produces a [RetrieveResult] as a consequence of the attempt at deleting all the given [Clause]
     *  from this [ClauseMultiset] **/
    override fun retrieveAll(clause: Clause): RetrieveResult<out ClauseMultiset>
}

