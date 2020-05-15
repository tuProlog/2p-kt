package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.impl.MutableReteClauseMultiSet
import it.unibo.tuprolog.collections.impl.ReteClauseMultiSet
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope

interface MutableClauseMultiSet : ClauseMultiSet {

    /** Adds a [Clause] to this [MutableClauseMultiSet] **/
    override fun add(clause: Clause): MutableClauseMultiSet

    /** Adds all the given [Clause] to this [MutableClauseMultiSet] **/
    override fun addAll(clauses: Iterable<Clause>): MutableClauseMultiSet

    /** Retrieves the first unifying [Clause] from this [MutableClauseMultiSet] as a [RetrieveResult]**/
    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseMultiSet>

    /** Retrieves all the unifying [Clause] from this [MutableClauseMultiSet] as a [RetrieveResult]**/
    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseMultiSet>

    companion object {

        /** Creates an empty [MutableClauseMultiSet] **/
        fun empty(): MutableClauseMultiSet = of(emptyList())

        /** Creates a [MutableClauseMultiSet] with given clauses */
        fun of(vararg clause: Clause): MutableClauseMultiSet = of(clause.asIterable())

        /** Let developers easily create a [MutableClauseMultiSet] programmatically while avoiding variables names clashing */
        fun of(vararg clause: Scope.() -> Clause): MutableClauseMultiSet =
            of(clause.map {
                Scope.empty(it)
            })

        /** Creates a [MutableClauseMultiSet] from the given [Sequence] of [Clause] */
        fun of(clauses: Sequence<Clause>): MutableClauseMultiSet = of(clauses.asIterable())

        /** Creates a [MutableClauseMultiSet] from the given [Iterable] of [Clause] */
        fun of(clauses: Iterable<Clause>): MutableClauseMultiSet =
            MutableReteClauseMultiSet(clauses)
    }

}