package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.Retractable
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.buffered
import it.unibo.tuprolog.utils.dequeOf

internal class VariableIndex(
    unificator: Unificator,
    private val ordered: Boolean
) : AbstractIndexingLeaf(unificator), Retractable {

    private val variables: MutableList<SituatedIndexedClause> = dequeOf()

    override val size: Int
        get() = variables.size

    override val isEmpty: Boolean
        get() = variables.isEmpty()

    override fun get(clause: Clause): Sequence<Clause> =
        extractGlobalSequence(clause)

    override fun assertA(clause: IndexedClause) =
        if (ordered) {
            variables.addFirst(SituatedIndexedClause.of(clause + this, this))
        } else {
            assertZ(clause)
        }

    override fun assertZ(clause: IndexedClause) {
        variables.add(SituatedIndexedClause.of(clause + this, this))
    }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        retractAllIndexed(clause).map { it.innerClause }

    override fun getCache(): Sequence<SituatedIndexedClause> =
        variables.asSequence()

    override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? {
        return extractFirst(clause, variables)
    }

    override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        extractGlobalIndexedSequence(clause)

    override fun retractIndexed(indexed: SituatedIndexedClause) {
        variables.remove(indexed)
    }

    override fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        removeAllLazily(variables, clause).buffered()

    private fun extractFirst(clause: Clause, index: MutableList<SituatedIndexedClause>): SituatedIndexedClause? {
        val actualIndex = index.indexOfFirst { unificator.match(it.innerClause, clause) }

        return if (actualIndex == -1) {
            null
        } else {
            index[actualIndex]
        }
    }

    override fun extractGlobalIndexedSequence(clause: Clause): Sequence<SituatedIndexedClause> =
        variables.asSequence().filter { unificator.match(it.innerClause, clause) }

    private fun extractGlobalSequence(clause: Clause): Sequence<Clause> =
        extractGlobalIndexedSequence(clause).map { it.innerClause }
}
