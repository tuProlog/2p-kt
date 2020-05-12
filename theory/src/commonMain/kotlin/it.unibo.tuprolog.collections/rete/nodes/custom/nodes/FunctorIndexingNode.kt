package it.unibo.tuprolog.collections.rete.nodes.custom.nodes

import it.unibo.tuprolog.collections.rete.nodes.custom.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.custom.IndexingNode
import it.unibo.tuprolog.core.Clause

internal class FunctorIndexingNode(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingNode{

    override fun get(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun assertA(clause: IndexedClause) {
        TODO("Not yet implemented")
    }

    override fun assertZ(clause: IndexedClause) {
        TODO("Not yet implemented")
    }

    override fun retractFirst(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun retractAll(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun retractAllOrdered(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun retractAllUnordered(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun retractFirstResult(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun retractAnyResult(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun getFirst(clause: Clause): IndexedClause? {
        TODO("Not yet implemented")
    }

    override fun getAny(clause: Clause): IndexedClause? {
        TODO("Not yet implemented")
    }

    override fun getIndexed(clause: Clause): Sequence<IndexedClause> {
        TODO("Not yet implemented")
    }

    override fun retractAllIndexed(clause: Clause): Sequence<IndexedClause> {
        TODO("Not yet implemented")
    }

    override fun retractIndexed(indexed: IndexedClause): Sequence<Clause> {
        TODO("Not yet implemented")
    }


}