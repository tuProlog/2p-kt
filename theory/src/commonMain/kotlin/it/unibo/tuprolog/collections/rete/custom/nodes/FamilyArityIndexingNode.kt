package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator

internal class FamilyArityIndexingNode(
    unificator: Unificator,
    private val ordered: Boolean,
    nestingLevel: Int
) : FamilyArityReteNode(unificator, ordered, nestingLevel), ArityIndexing {

    override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? =
        if (ordered) orderedLookahead(clause)
        else anyLookahead(clause)

    override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        if (ordered) getOrderedIndexed(clause)
        else getUnorderedIndexed(clause)

    override fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        if (ordered) {
            retractAllOrderedIndexed(clause)
        } else {
            retractAllUnorderedIndexed(clause)
        }

    override fun extractGlobalIndexedSequence(clause: Clause): Sequence<SituatedIndexedClause> {
        return if (ordered) {
            Utils.merge(
                atomicIndex.extractGlobalIndexedSequence(clause),
                numericIndex.extractGlobalIndexedSequence(clause),
                variableIndex.extractGlobalIndexedSequence(clause),
                compoundIndex.extractGlobalIndexedSequence(clause)
            )
        } else {
            Utils.flattenIndexed(
                atomicIndex.extractGlobalIndexedSequence(clause),
                numericIndex.extractGlobalIndexedSequence(clause),
                variableIndex.extractGlobalIndexedSequence(clause),
                compoundIndex.extractGlobalIndexedSequence(clause)
            )
        }
    }
}
