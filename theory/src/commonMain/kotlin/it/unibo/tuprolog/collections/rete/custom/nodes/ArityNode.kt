package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.*
import it.unibo.tuprolog.collections.rete.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.collections.rete.custom.leaf.AtomIndex
import it.unibo.tuprolog.collections.rete.custom.leaf.CompoundIndex
import it.unibo.tuprolog.collections.rete.custom.leaf.NumericIndex
import it.unibo.tuprolog.collections.rete.custom.leaf.VariableIndex
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.dequeOf

internal interface ArityRete : ReteNode, TopLevelReteNode

internal interface ArityIndexing : ReteNode, IndexingNode

internal sealed class ArityNode : ReteNode {

    internal open class FamilyArityReteNode(
        private val ordered: Boolean,
        private val nestingLevel: Int
    ) : ArityNode(), ArityRete {

        protected val numericIndex: IndexingLeaf = NumericIndex(ordered, nestingLevel)
        protected val atomicIndex: IndexingLeaf = AtomIndex(ordered, nestingLevel)
        protected val variableIndex: IndexingLeaf = VariableIndex(ordered)
        protected val compoundIndex: IndexingLeaf = CompoundIndex(ordered, nestingLevel + 1)

        private val theoryCache: Cached<MutableList<SituatedIndexedClause>> = Cached.of(this::regenerateCache)

        override fun retractFirst(clause: Clause): Sequence<Clause> {
            val result = if (ordered)
                orderedLookahead(clause)
            else
                anyLookahead(clause)

            return if (result == null){
                emptySequence()
            } else {
                result.removeFromIndex()
                theoryCache.invalidate()
                sequenceOf(result.innerClause)
            }
        }


        override fun get(clause: Clause): Sequence<Clause> {
            return if (clause.isGlobal()) {
                if (ordered)
                    Utils.merge(
                        atomicIndex.extractGlobalIndexedSequence(clause),
                        variableIndex.extractGlobalIndexedSequence(clause),
                        numericIndex.extractGlobalIndexedSequence(clause),
                        compoundIndex.extractGlobalIndexedSequence(clause)
                    ).map { it.innerClause }
                else
                    Utils.flattenIndexed(
                        atomicIndex.extractGlobalIndexedSequence(clause),
                        variableIndex.extractGlobalIndexedSequence(clause),
                        numericIndex.extractGlobalIndexedSequence(clause),
                        compoundIndex.extractGlobalIndexedSequence(clause)
                    ).map { it.innerClause }
            } else {
                if (ordered) getOrdered(clause)
                else getUnordered(clause)
            }
        }


        override fun assertA(clause: IndexedClause) =
            assertByFirstParameter(clause).assertA(clause)

        override fun assertZ(clause: IndexedClause) =
            assertByFirstParameter(clause).assertZ(clause)

        override fun retractAll(clause: Clause): Sequence<Clause> =
            if (ordered) {
                retractAllOrdered(clause).let {
                    invalidCache(it)
                    it
                }
            } else {
                retractAllUnordered(clause).let {
                    invalidCache(it)
                    it
                }
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
            clause.nestedFirstArgument().let {
                return when (it) {
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
            }

        protected fun retractAllUnorderedIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
            clause.nestedFirstArgument().let {
                when (it) {
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
            }

        protected fun getOrderedIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
            clause.nestedFirstArgument().let {
                when (it) {
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
            }

        protected fun getUnorderedIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
            clause.nestedFirstArgument().let {
                when (it) {
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

        protected fun invalidCache(result: Sequence<*>) {
            if (result.any()) {
                theoryCache.invalidate()
            }
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
            clause.innerClause.nestedFirstArgument().let {
                when (it) {
                    is Numeric -> numericIndex
                    is Atom -> atomicIndex
                    is Var -> variableIndex
                    else -> compoundIndex
                }
            }

        private fun Clause.isGlobal(): Boolean =
            this.head!!.nestedFirstArgument(nestingLevel + 1) is Var

    }

    internal class FamilyArityIndexingNode(
        private val ordered: Boolean,
        nestingLevel: Int
    ) : FamilyArityReteNode(ordered, nestingLevel), ArityIndexing {

        override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? =
            if (ordered) orderedLookahead(clause)
            else anyLookahead(clause)

        override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
            if (ordered) getOrderedIndexed(clause)
            else getUnorderedIndexed(clause)

        override fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
            if (ordered) {
                retractAllOrderedIndexed(clause).let {
                    invalidCache(it)
                    it
                }
            } else {
                retractAllUnorderedIndexed(clause).let {
                    invalidCache(it)
                    it
                }
            }

        override fun extractGlobalIndexedSequence(clause: Clause): Sequence<SituatedIndexedClause> {
            return if (ordered)
                Utils.merge(
                    atomicIndex.extractGlobalIndexedSequence(clause),
                    numericIndex.extractGlobalIndexedSequence(clause),
                    variableIndex.extractGlobalIndexedSequence(clause),
                    compoundIndex.extractGlobalIndexedSequence(clause)
                )
            else {
                Utils.flattenIndexed(
                    atomicIndex.extractGlobalIndexedSequence(clause),
                    numericIndex.extractGlobalIndexedSequence(clause),
                    variableIndex.extractGlobalIndexedSequence(clause),
                    compoundIndex.extractGlobalIndexedSequence(clause)
                )
            }
        }

    }

    internal class ZeroArityReteNode(
        private val ordered: Boolean
    ) : ArityNode(), ArityRete, Retractable {

        private val atoms: MutableList<SituatedIndexedClause> = dequeOf()

        override fun retractFirst(clause: Clause): Sequence<Clause> {
            val actualIndex = atoms.indexOfFirst { it.innerClause matches clause }

            return if (actualIndex == -1) emptySequence()
            else {
                atoms[actualIndex].let {
                    atoms.removeAt(actualIndex)
                    sequenceOf(it.innerClause)
                }
            }
        }

        override fun get(clause: Clause): Sequence<Clause> =
            atoms.asSequence().filter { it.innerClause matches clause }.map { it.innerClause }

        override fun assertA(clause: IndexedClause) {
            if (ordered) {
                atoms.addFirst(SituatedIndexedClause.of(clause, this))
            } else {
                assertZ(clause)
            }
        }

        override fun assertZ(clause: IndexedClause) {
            atoms.add(SituatedIndexedClause.of(clause, this))
        }

        override fun retractAll(clause: Clause): Sequence<Clause> {
            val result = atoms.filter { it.innerClause matches clause }
            result.forEach { atoms.remove(it) }
            return result.map { it.innerClause }.asSequence()
        }

        override fun getCache(): Sequence<SituatedIndexedClause> {
            return atoms.asSequence()
        }

        override fun retractIndexed(indexed: SituatedIndexedClause) {
            atoms.remove(indexed)
        }
    }

}