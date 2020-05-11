package it.unibo.tuprolog.collections.rete.nodes.engineered.index

import it.unibo.tuprolog.collections.rete.nodes.engineered.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.engineered.IndexingLeaf
import it.unibo.tuprolog.collections.rete.nodes.engineered.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator.Companion.matches

internal class DirectiveIndex(ordered: Boolean) : ReteNode, IndexingLeaf {

    private val orderedDirectives: MutableList<IndexedClause> =
        dequeOf()

    override fun get(clause: Clause): Sequence<Clause> =
        orderedDirectives
            .filter { it.innerClause matches clause }
            .map { it -> it.innerClause }
            .asSequence()

    override fun assertA(clause: IndexedClause) {
        orderedDirectives.addFirst(clause)
    }

    override fun assertZ(clause: IndexedClause) {
        orderedDirectives.add(clause)
    }

    override fun retractFirst(clause: Clause): Sequence<Clause> {
        return sequenceOf(
            orderedDirectives.removeAt(
                orderedDirectives.indexOfFirst {
                    it.innerClause matches clause
                }
            ).innerClause)
    }

    override fun retractAll(clause: Clause): Sequence<Clause> {
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