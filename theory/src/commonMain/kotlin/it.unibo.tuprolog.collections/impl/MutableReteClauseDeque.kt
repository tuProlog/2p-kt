package it.unibo.tuprolog.collections.impl

import it.unibo.tuprolog.collections.AbstractClauseCollection
import it.unibo.tuprolog.collections.MutableClauseDeque
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.collections.rete.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal class MutableReteClauseDeque private constructor(
    private val rete: ReteNode<*, Clause>
) : MutableClauseDeque, AbstractClauseCollection(rete) {

    /** Construct a [MutableReteClauseDeque] from given clauses */
    constructor(clauses: Iterable<Clause>) : this(ReteNode.ofSet(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override fun addFirst(clause: Clause): MutableClauseDeque {
        rete.put(clause, beforeOthers = true)
        return this
    }

    override fun addLast(clause: Clause): MutableClauseDeque {
        rete.put(clause)
        return this
    }

    override fun add(clause: Clause): MutableClauseDeque {
        addLast(clause)
        return this
    }

    override fun addAll(clauses: Iterable<Clause>): MutableClauseDeque {
        rete.put(clauses)
        return this
    }

    override fun retrieveFirst(clause: Clause): RetrieveResult<out MutableClauseDeque> {
        val retracted = rete.remove(clause)

        return when {
            retracted.none() ->
                RetrieveResult.Failure(this)
            else ->
                RetrieveResult.Success(
                    this, retracted.toList()
                )
        }
    }

    override fun retrieveLast(clause: Clause): RetrieveResult<out MutableClauseDeque> {
        TODO("Not yet implemented")
    }

    override fun retrieve(clause: Clause): RetrieveResult<out MutableClauseDeque> =
        retrieve(clause)

    override fun retrieveAll(clause: Clause): RetrieveResult<out MutableClauseDeque> {
        val retracted = rete.removeAll(clause)

        return when {
            retracted.none() ->
                RetrieveResult.Failure(this)
            else ->
                RetrieveResult.Success(
                    this, retracted.toList()
                )
        }
    }

    override fun getFirst(clause: Clause): Sequence<Clause> =
        rete.get(clause)

    override fun getLast(clause: Clause): Sequence<Clause> =
        getFirst(clause).toList().asReversed().asSequence()
}