package it.unibo.tuprolog.collections

import it.unibo.tuprolog.collections.rete.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.Theory.checkClausesCorrect

internal abstract class AbstractClauseCollection protected constructor(private val rete: ReteNode<*, Clause>) : ClauseCollection {

    constructor(clauses: Iterable<Clause>) : this(ReteNode.ofSet(clauses)) {
        checkClausesCorrect(clauses)
    }

    override val size: Int
        get() {
            TODO("ArityNode should collect info about its subtree to optimize")
            rete.indexedElements.count()
        }

    override fun isEmpty(): Boolean =
        size == 0

    override fun contains(element: Clause): Boolean =
        rete.get(element).any()

    override fun containsAll(element: Iterable<Clause>): Boolean {
        TODO("whats the correct way of folding over element (can't overturn initial false)")
    }

    abstract override fun add(clause: Clause): ClauseCollection

    abstract override fun addAll(clause: Iterable<Clause>): ClauseCollection

    abstract override fun retrieve(clause: Clause): RetrieveResult<out ClauseCollection>

    abstract override fun retrieveAll(clause: Clause): RetrieveResult<out ClauseCollection>

    override fun iterator(): Iterator<Clause> =
        rete.indexedElements.iterator()
}