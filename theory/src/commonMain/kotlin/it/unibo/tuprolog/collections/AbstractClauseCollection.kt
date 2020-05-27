package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.TheoryUtils

internal abstract class AbstractClauseCollection<Self : AbstractClauseCollection<Self>>
protected constructor(protected val rete: ReteTree) : ClauseCollection {

    override val size: Int
        get() = rete.clauses.count()

    override fun isEmpty(): Boolean =
        size == 0

    override fun contains(element: Clause): Boolean =
        rete.get(element).any()

    override fun containsAll(elements: Iterable<Clause>): Boolean =
        elements.all { it in this }

    abstract override fun add(clause: Clause): Self

    abstract override fun addAll(clauses: Iterable<Clause>): Self

    abstract override fun retrieve(clause: Clause): RetrieveResult<out Self>

    abstract override fun retrieveAll(clause: Clause): RetrieveResult<out Self>

    override fun iterator(): Iterator<Clause> =
        rete.clauses.iterator()

}