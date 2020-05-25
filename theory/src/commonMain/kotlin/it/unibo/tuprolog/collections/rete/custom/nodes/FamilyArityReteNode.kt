package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.IndexingLeaf
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.collections.rete.custom.leaf.AtomIndex
import it.unibo.tuprolog.collections.rete.custom.leaf.CompoundIndex
import it.unibo.tuprolog.collections.rete.custom.leaf.NumericIndex
import it.unibo.tuprolog.collections.rete.custom.leaf.VariableIndex
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.dequeOf

internal open class FamilyArityReteNode(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : ArityNode(), ArityRete {

    protected val numericIndex: IndexingLeaf =
        NumericIndex(ordered, nestingLevel)
    protected val atomicIndex: IndexingLeaf =
        AtomIndex(ordered, nestingLevel)
    protected val variableIndex: IndexingLeaf =
        VariableIndex(ordered)
    protected val compoundIndex: IndexingLeaf =
        CompoundIndex(ordered, nestingLevel + 1)

    private val theoryCache: Cached<MutableList<SituatedIndexedClause>> =
        Cached.of(this::regenerateCache)

    override fun retractFirst(clause: Clause): Sequence<Clause> {
        val result = if (ordered) {
            orderedLookahead(clause)
        } else {
            anyLookahead(clause)
        }

        return if (result == null) {
            emptySequence()
        } else {
            result.removeFromIndex()
            invalidateCache()
            sequenceOf(result.innerClause)
        }
    }


    override fun get(clause: Clause): Sequence<Clause> {
        return if (clause.isGlobal()) {
            if (ordered) {
                Utils.merge(
                    atomicIndex.extractGlobalIndexedSequence(clause),
                    variableIndex.extractGlobalIndexedSequence(clause),
                    numericIndex.extractGlobalIndexedSequence(clause),
                    compoundIndex.extractGlobalIndexedSequence(clause)
                ).map { it.innerClause }
            } else {
                Utils.flattenIndexed(
                    atomicIndex.extractGlobalIndexedSequence(clause),
                    variableIndex.extractGlobalIndexedSequence(clause),
                    numericIndex.extractGlobalIndexedSequence(clause),
                    compoundIndex.extractGlobalIndexedSequence(clause)
                ).map { it.innerClause }
            }
        } else {
            if (ordered) getOrdered(clause)
            else getUnordered(clause)
        }
    }


    override fun assertA(clause: IndexedClause) =
        assertByFirstParameter(clause).assertA(clause + this)

    override fun assertZ(clause: IndexedClause) =
        assertByFirstParameter(clause).assertZ(clause + this)

    override fun retractAll(clause: Clause): Sequence<Clause> =
        if (ordered) {
            retractAllOrdered(clause)
        } else {
            retractAllUnordered(clause)
        }

    override fun getCache(): Sequence<SituatedIndexedClause> =
        theoryCache.value.asSequence()

    private fun retractAllOrdered(clause: Clause): Sequence<Clause> =
        retractAllOrderedIndexed(clause).map { it.innerClause }

    private fun getOrdered(clause: Clause): Sequence<Clause> =
        getOrderedIndexed(clause).map { it.innerClause }

    private fun retractAllUnordered(clause: Clause): Sequence<Clause> =
        retractAllUnorderedIndexed(clause).map { it.innerClause }

    private fun getUnordered(clause: Clause): Sequence<Clause> =
        getUnorderedIndexed(clause).map { it.innerClause }

    protected fun retractAllOrderedIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        when (clause.nestedFirstArgument()) {
            is Numeric -> {
                Utils.merge(
                    variableIndex.retractAllIndexed(clause),
                    numericIndex.retractAllIndexed(clause)
                )
            }
            is Atom -> {
                Utils.merge(
                    variableIndex.retractAllIndexed(clause),
                    atomicIndex.retractAllIndexed(clause)
                )
            }
            is Var -> {
                Utils.merge(
                    variableIndex.retractAllIndexed(clause),
                    numericIndex.retractAllIndexed(clause),
                    atomicIndex.retractAllIndexed(clause),
                    compoundIndex.retractAllIndexed(clause)
                )
            }
            else -> {
                Utils.merge(
                    variableIndex.retractAllIndexed(clause),
                    compoundIndex.retractAllIndexed(clause)
                )
            }
        }

    protected fun retractAllUnorderedIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        when (clause.nestedFirstArgument()) {
            is Numeric -> Utils.flattenIndexed(
                variableIndex.retractAllIndexed(clause),
                numericIndex.retractAllIndexed(clause)
            )
            is Atom -> Utils.flattenIndexed(
                variableIndex.retractAllIndexed(clause),
                atomicIndex.retractAllIndexed(clause)
            )
            is Var -> Utils.flattenIndexed(
                variableIndex.retractAllIndexed(clause),
                numericIndex.retractAllIndexed(clause),
                atomicIndex.retractAllIndexed(clause),
                compoundIndex.retractAllIndexed(clause)
            )
            else -> Utils.flattenIndexed(
                variableIndex.retractAllIndexed(clause),
                compoundIndex.retractAllIndexed(clause)
            )
        }


    protected fun getOrderedIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        when (clause.nestedFirstArgument()) {
            is Numeric ->
                Utils.merge(
                    variableIndex.getIndexed(clause),
                    numericIndex.getIndexed(clause)
                )
            is Atom ->
                Utils.merge(
                    variableIndex.getIndexed(clause),
                    atomicIndex.getIndexed(clause)
                )
            is Var ->
                Utils.merge(
                    variableIndex.getIndexed(clause),
                    numericIndex.getIndexed(clause),
                    atomicIndex.getIndexed(clause),
                    compoundIndex.getIndexed(clause)
                )
            else ->
                Utils.merge(
                    variableIndex.getIndexed(clause),
                    compoundIndex.getIndexed(clause)
                )
        }


    protected fun getUnorderedIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        when (clause.nestedFirstArgument()) {
            is Numeric -> Utils.flattenIndexed(
                variableIndex.getIndexed(clause),
                numericIndex.getIndexed(clause)
            )
            is Atom -> Utils.flattenIndexed(
                variableIndex.getIndexed(clause),
                atomicIndex.getIndexed(clause)
            )
            is Var -> Utils.flattenIndexed(
                variableIndex.getIndexed(clause),
                numericIndex.getIndexed(clause),
                atomicIndex.getIndexed(clause),
                compoundIndex.getIndexed(clause)
            )
            else -> Utils.flattenIndexed(
                variableIndex.getIndexed(clause),
                compoundIndex.getIndexed(clause)
            )
        }


    protected fun anyLookahead(clause: Clause): SituatedIndexedClause? =
        when (clause.nestedFirstArgument()) {
            is Numeric -> {
                variableIndex.getFirstIndexed(clause)
                    ?: numericIndex.getFirstIndexed(clause)
            }
            is Atom -> {
                variableIndex.getFirstIndexed(clause)
                    ?: atomicIndex.getFirstIndexed(clause)
            }
            is Var -> {
                variableIndex.getFirstIndexed(clause)
                    ?: numericIndex.getFirstIndexed(clause)
                    ?: atomicIndex.getFirstIndexed(clause)
                    ?: compoundIndex.getFirstIndexed(clause)
            }
            else -> {
                variableIndex.getFirstIndexed(clause)
                    ?: compoundIndex.getFirstIndexed(clause)
            }
        }


    protected fun orderedLookahead(clause: Clause): SituatedIndexedClause? =
        when (clause.nestedFirstArgument()) {
            is Numeric -> {
                Utils.comparePriority(
                    numericIndex.getFirstIndexed(clause),
                    variableIndex.getFirstIndexed(clause)
                )
            }
            is Atom -> {
                Utils.comparePriority(
                    atomicIndex.getFirstIndexed(clause),
                    variableIndex.getFirstIndexed(clause)
                )
            }
            is Var -> Utils.comparePriority(
                Utils.comparePriority(
                    numericIndex.getFirstIndexed(clause),
                    atomicIndex.getFirstIndexed(clause)
                ),
                Utils.comparePriority(
                    variableIndex.getFirstIndexed(clause),
                    compoundIndex.getFirstIndexed(clause)
                )
            )
            else -> Utils.comparePriority(
                variableIndex.getFirstIndexed(clause),
                compoundIndex.getFirstIndexed(clause)
            )
        }

    override fun invalidateCache() {
        theoryCache.invalidate()
//            numericIndex.invalidateCache()
//            atomicIndex.invalidateCache()
//            variableIndex.invalidateCache()
//            compoundIndex.invalidateCache()
    }

    private fun regenerateCache(): MutableList<SituatedIndexedClause> =
        dequeOf(
            if (ordered) {
                Utils.merge(
                    atomicIndex.getCache(),
                    numericIndex.getCache(),
                    variableIndex.getCache(),
                    compoundIndex.getCache()
                )
            } else {
                Utils.flattenIndexed(
                    atomicIndex.getCache(),
                    numericIndex.getCache(),
                    variableIndex.getCache(),
                    compoundIndex.getCache()
                )
            }
        )

    private fun Clause.nestedFirstArgument(): Term =
        this.head!!.nestedFirstArgument(nestingLevel + 1)

    private fun assertByFirstParameter(clause: IndexedClause): IndexingLeaf =
        when (clause.innerClause.nestedFirstArgument()) {
            is Numeric -> numericIndex
            is Atom -> atomicIndex
            is Var -> variableIndex
            else -> compoundIndex
        }

    private fun Clause.isGlobal(): Boolean =
        this.head!!.nestedFirstArgument(nestingLevel + 1) is Var

}