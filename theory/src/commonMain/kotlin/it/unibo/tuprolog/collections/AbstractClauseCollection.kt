package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.rete.custom.ReteTree
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory

internal abstract class AbstractClauseCollection<Self : AbstractClauseCollection<Self>>
protected constructor(private val rete: ReteTree) : ClauseCollection {

    constructor(
        clauses: Iterable<Clause>,
        reteNodeBuilder: (Iterable<Clause>) -> ReteTree
    ) : this(reteNodeBuilder(clauses)) {
        Theory.checkClausesCorrect(clauses)
    }

    override val size: Int
        get() = rete.clauses.count()

    override fun isEmpty(): Boolean =
        size == 0

    override fun contains(element: Clause): Boolean =
        rete.get(element).any()

    override fun containsAll(elements: Iterable<Clause>): Boolean {
        var booleanAccumulator = true
        elements.forEach { booleanAccumulator = booleanAccumulator && contains(it) }
        return booleanAccumulator
    }

    abstract override fun add(clause: Clause): Self

    abstract override fun addAll(clauses: Iterable<Clause>): Self

    abstract override fun retrieve(clause: Clause): RetrieveResult<out Self>

    abstract override fun retrieveAll(clause: Clause): RetrieveResult<out Self>

    override fun iterator(): Iterator<Clause> =
        rete.clauses.iterator()

}