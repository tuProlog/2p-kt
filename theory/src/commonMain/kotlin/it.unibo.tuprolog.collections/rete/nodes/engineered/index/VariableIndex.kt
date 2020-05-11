package it.unibo.tuprolog.collections.rete.nodes.engineered.index

import it.unibo.tuprolog.collections.rete.nodes.engineered.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.engineered.IndexingLeaf
import it.unibo.tuprolog.collections.rete.nodes.engineered.ReteNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator.Companion.matches

internal sealed class VariableIndex(private val globalSelector: Struct) : IndexingLeaf {

    protected val globalIndex: MutableList<IndexedClause> = dequeOf()

    override fun get(clause: Clause): Sequence<Clause> {
        return if(clause structurallyEquals globalSelector)
            globalIndex.asSequence().map { it.innerClause }
        else extractGlobalSequence(clause)
    }

    override fun assertZ(clause: IndexedClause) {
        globalIndex.add(clause)
    }

    override fun getFirst(clause: Clause): IndexedClause? {
        return extractFirst(clause, globalIndex)
    }

    private fun extractFirst(clause: Clause, index: MutableList<IndexedClause>): IndexedClause? {
        val actualIndex = index.indexOfFirst { it.innerClause matches clause }

        return if (actualIndex == -1) null
        else index[actualIndex]
    }

    override fun getAny(clause: Clause): IndexedClause? =
        this.getFirst(clause)

    override fun getIndexed(clause: Clause): Sequence<IndexedClause> {
        return if (clause structurallyEquals globalSelector)
            globalIndex.asSequence()
        else extractGlobalIndexedSequence(clause)
    }

    override fun retractIndexed(indexed: IndexedClause): Sequence<Clause> {
        globalIndex.remove(indexed)
        return sequenceOf(indexed.innerClause)
    }

    override fun retractAllIndexed(clause: Clause): Sequence<IndexedClause> {
        return if (clause structurallyEquals globalSelector)
            globalIndex.asSequence()
        else
            retractFromMutableList(clause, globalIndex)
    }

    private fun retractFromMutableList(clause: Clause, index: MutableList<IndexedClause>): Sequence<IndexedClause> {
        val result = index.filter { it.innerClause matches clause }
        result.forEach { index.remove(it) }
        return result.asSequence()
    }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        retractAllIndexed(clause).map { it.innerClause }

    private fun extractGlobalIndexedSequence(clause: Clause): Sequence<IndexedClause> =
        globalIndex.asSequence()
            .filter { it.innerClause matches clause }

    private fun extractGlobalSequence(clause: Clause): Sequence<Clause> =
        extractGlobalIndexedSequence(clause)
            .map { it.innerClause }

    internal class OrderedIndex(globalSelector: Struct)
        : VariableIndex(globalSelector) {

        override fun assertA(clause: IndexedClause) =
            globalIndex.addFirst(clause)

    }

    internal class UnorderedIndex(globalSelector: Struct)
        : VariableIndex(globalSelector) {

        override fun assertA(clause: IndexedClause) =
            assertZ(clause)

    }
}