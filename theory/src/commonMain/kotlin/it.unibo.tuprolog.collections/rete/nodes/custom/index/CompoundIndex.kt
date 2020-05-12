package it.unibo.tuprolog.collections.rete.nodes.custom.index

import it.unibo.tuprolog.collections.rete.nodes.custom.FunctorNode
import it.unibo.tuprolog.collections.rete.nodes.custom.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.custom.IndexingLeaf
import it.unibo.tuprolog.core.Clause

internal class CompoundIndex(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingLeaf{

    protected val indexedFunctors: MutableMap<String, FunctorNode> = mutableMapOf()

    override fun get(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun assertA(clause: IndexedClause) {
        TODO("Not yet implemented")
    }

    override fun assertZ(clause: IndexedClause) {
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

    override fun retractAll(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun retractAllIndexed(clause: Clause): Sequence<IndexedClause> {
        TODO("Not yet implemented")
    }

    override fun retractIndexed(indexed: IndexedClause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

}