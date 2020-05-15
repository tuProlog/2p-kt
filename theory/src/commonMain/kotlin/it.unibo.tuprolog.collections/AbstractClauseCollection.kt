package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.rete.generic.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory.checkClausesCorrect

internal abstract class AbstractClauseCollection<Self : AbstractClauseCollection<Self>>

    protected constructor(private val rete: ReteNode<*, Clause>) : ClauseCollection {

    constructor(clauses: Iterable<Clause>) : this(ReteNode.ofSet(clauses)) {
        checkClausesCorrect(clauses)
    }

    override val size: Int
        get() = rete.indexedElements.count()

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
        rete.indexedElements.iterator()

}