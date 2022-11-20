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
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.dequeOf

internal open class FamilyArityReteNode(
    unificator: Unificator,
    private val ordered: Boolean,
    private val nestingLevel: Int
) : ArityNode(unificator), ArityRete {

    protected val numericIndex: IndexingLeaf =
        NumericIndex(unificator, ordered, nestingLevel)

    protected val atomicIndex: IndexingLeaf =
        AtomIndex(unificator, ordered, nestingLevel)

    protected val variableIndex: IndexingLeaf =
        VariableIndex(unificator, ordered)

    protected val compoundIndex: IndexingLeaf =
        CompoundIndex(unificator, ordered, nestingLevel + 1)

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

    override val size: Int
        get() = sequenceOf(numericIndex, atomicIndex, variableIndex, compoundIndex).map { it.size }.sum()

    override val isEmpty: Boolean
        get() = sequenceOf(numericIndex, atomicIndex, variableIndex, compoundIndex).all { it.isEmpty }

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

    protected fun retractAllOrderedIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        val innerFirst = clause.nestedFirstArgument()
        return when {
            innerFirst.isNumber -> {
                Utils.merge(
                    variableIndex.retractAllIndexed(clause),
                    numericIndex.retractAllIndexed(clause)
                )
            }
            innerFirst.isAtom -> {
                Utils.merge(
                    variableIndex.retractAllIndexed(clause),
                    atomicIndex.retractAllIndexed(clause)
                )
            }
            innerFirst.isVar -> {
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
    }

    protected fun retractAllUnorderedIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        val innerFirst = clause.nestedFirstArgument()
        return when {
            innerFirst.isNumber -> Utils.flattenIndexed(
                variableIndex.retractAllIndexed(clause),
                numericIndex.retractAllIndexed(clause)
            )
            innerFirst.isAtom -> Utils.flattenIndexed(
                variableIndex.retractAllIndexed(clause),
                atomicIndex.retractAllIndexed(clause)
            )
            innerFirst.isVar -> Utils.flattenIndexed(
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
    }

    protected fun getOrderedIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        val innerFirst = clause.nestedFirstArgument()
        return when {
            innerFirst.isNumber ->
                Utils.merge(
                    variableIndex.getIndexed(clause),
                    numericIndex.getIndexed(clause)
                )
            innerFirst.isAtom ->
                Utils.merge(
                    variableIndex.getIndexed(clause),
                    atomicIndex.getIndexed(clause)
                )
            innerFirst.isVar ->
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
    }

    protected fun getUnorderedIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        val innerFirst = clause.nestedFirstArgument()
        return when {
            innerFirst.isNumber -> Utils.flattenIndexed(
                variableIndex.getIndexed(clause),
                numericIndex.getIndexed(clause)
            )
            innerFirst.isAtom -> Utils.flattenIndexed(
                variableIndex.getIndexed(clause),
                atomicIndex.getIndexed(clause)
            )
            innerFirst.isVar -> Utils.flattenIndexed(
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
    }

    protected fun anyLookahead(clause: Clause): SituatedIndexedClause? {
        val innerFirst = clause.nestedFirstArgument()
        return when {
            innerFirst.isNumber -> {
                variableIndex.getFirstIndexed(clause)
                    ?: numericIndex.getFirstIndexed(clause)
            }
            innerFirst.isAtom -> {
                variableIndex.getFirstIndexed(clause)
                    ?: atomicIndex.getFirstIndexed(clause)
            }
            innerFirst.isVar -> {
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
    }

    protected fun orderedLookahead(clause: Clause): SituatedIndexedClause? {
        val innerFirst = clause.nestedFirstArgument()
        return when {
            innerFirst.isNumber -> {
                Utils.comparePriority(
                    numericIndex.getFirstIndexed(clause),
                    variableIndex.getFirstIndexed(clause)
                )
            }
            innerFirst.isAtom -> {
                Utils.comparePriority(
                    atomicIndex.getFirstIndexed(clause),
                    variableIndex.getFirstIndexed(clause)
                )
            }
            innerFirst.isVar -> Utils.comparePriority(
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

    private fun assertByFirstParameter(clause: IndexedClause): IndexingLeaf {
        val innerFirst = clause.innerClause.nestedFirstArgument()
        return when {
            innerFirst.isNumber -> numericIndex
            innerFirst.isAtom -> atomicIndex
            innerFirst.isVar -> variableIndex
            else -> compoundIndex
        }
    }

    private fun Clause.isGlobal(): Boolean =
        this.head!!.nestedFirstArgument(nestingLevel + 1).isVar
}
