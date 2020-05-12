package it.unibo.tuprolog.collections.rete.nodes.custom.leaf

import it.unibo.tuprolog.collections.rete.nodes.custom.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.custom.IndexingLeaf
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.dequeOf

internal class VariableIndex(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingLeaf {

    private val variables: MutableList<IndexedClause> = dequeOf()

    override fun get(clause: Clause): Sequence<Clause> =
        extractGlobalSequence(clause)

    override fun assertA(clause: IndexedClause) =
        if(ordered){
            variables.addFirst(clause)
        } else {
            assertZ(clause)
        }

    override fun assertZ(clause: IndexedClause) {
        variables.add(clause)
    }

    override fun retractFirst(clause: Clause): Sequence<Clause> {
        TODO("Not yet implemented")
    }

    override fun getFirst(clause: Clause): IndexedClause? {
        return extractFirst(clause, variables)
    }

    private fun extractFirst(clause: Clause, index: MutableList<IndexedClause>): IndexedClause? {
        val actualIndex = index.indexOfFirst { it.innerClause matches clause }

        return if (actualIndex == -1) null
        else index[actualIndex]
    }

    override fun getAny(clause: Clause): IndexedClause? =
        this.getFirst(clause)

    override fun getIndexed(clause: Clause): Sequence<IndexedClause> =
        extractGlobalIndexedSequence(clause)

    override fun retractIndexed(indexed: IndexedClause): Sequence<Clause> {
        variables.remove(indexed)
        return sequenceOf(indexed.innerClause)
    }

    override fun retractAllIndexed(clause: Clause): Sequence<IndexedClause> =
        retractFromMutableList(clause, variables)

    private fun retractFromMutableList(clause: Clause, index: MutableList<IndexedClause>): Sequence<IndexedClause> {
        val result = index.filter { it.innerClause matches clause }
        result.forEach { index.remove(it) }
        return result.asSequence()
    }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        retractAllIndexed(clause).map { it.innerClause }

    private fun extractGlobalIndexedSequence(clause: Clause): Sequence<IndexedClause> =
        variables.asSequence()
            .filter { it.innerClause matches clause }

    private fun extractGlobalSequence(clause: Clause): Sequence<Clause> =
        extractGlobalIndexedSequence(clause)
            .map { it.innerClause }

}