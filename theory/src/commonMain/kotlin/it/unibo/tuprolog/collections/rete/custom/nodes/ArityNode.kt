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
import it.unibo.tuprolog.utils.dequeOf

internal interface ArityRete: ReteNode, TopLevelReteNode

internal interface ArityIndexing: ReteNode, IndexingNode

internal sealed class ArityNode: ReteNode {

    internal open class FamilyArityReteNode(
        private val ordered: Boolean,
        private val nestingLevel: Int
    ) : ArityNode(), ArityRete {

        private val numericIndex: IndexingLeaf = NumericIndex(ordered, nestingLevel)
        private val atomicIndex: IndexingLeaf = AtomIndex(ordered, nestingLevel)
        private val variableIndex: IndexingLeaf = VariableIndex(ordered)
        private val compoundIndex: IndexingLeaf = CompoundIndex(ordered, nestingLevel + 1)

        private val cache: MutableList<SituatedIndexedClause> = dequeOf()
        private var isCacheValid: Boolean = true

        override fun retractFirst(clause: Clause): Sequence<Clause> {
            val result: SituatedIndexedClause?

            return if(ordered) {
                result = orderedLookahead(clause)

                if(result == null)
                    emptySequence()
                else{
                    result.removeFromIndex()
                    sequenceOf(result.innerClause).let {
                        invalidCache(it)
                        it
                    }
                }
            } else {
                result = anyLookahead(clause)

                if(result == null)
                    emptySequence()
                else {
                    result.removeFromIndex()
                    sequenceOf(result.innerClause).let {
                        invalidCache(it)
                        it
                    }
                }
            }
        }

        override fun get(clause: Clause): Sequence<Clause> =
            if (ordered) getOrdered(clause)
            else getUnordered(clause)

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
            }
            else {
                retractAllUnordered(clause).let {
                    invalidCache(it)
                    it
                }
            }

        override fun getCache(): Sequence<SituatedIndexedClause> {
            regenerateCache()
            return cache.asSequence()
        }

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
                return when(it) {
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
                            (compoundIndex as IndexingNode).retractAllGlobalIndexed(clause)
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
                when(it) {
                    is Numeric -> Utils.flattenIndexed(
                        variableIndex.retractAllIndexed(clause),
                        numericIndex.retractAllIndexed(clause)
                    )
                    is Atom -> Utils.flattenIndexed(
                        variableIndex.retractAllIndexed(clause) +
                        atomicIndex.retractAllIndexed(clause)
                    )
                    is Var -> Utils.flattenIndexed(
                        variableIndex.retractAllIndexed(clause) +
                        numericIndex.retractAllIndexed(clause) +
                        atomicIndex.retractAllIndexed(clause) +
                        (compoundIndex as IndexingNode).retractAllGlobalIndexed(clause)
                    )
                    else -> Utils.flattenIndexed(
                        variableIndex.retractAllIndexed(clause) +
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
                            (compoundIndex as IndexingNode).getGlobalIndexed(clause)
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
                    is Numeric ->
                        variableIndex.getIndexed(clause) +
                        numericIndex.getIndexed(clause)
                    is Atom ->
                        variableIndex.getIndexed(clause) +
                        atomicIndex.getIndexed(clause)
                    is Var ->
                        variableIndex.getIndexed(clause) +
                        numericIndex.getIndexed(clause) +
                        atomicIndex.getIndexed(clause) +
                        (compoundIndex as IndexingNode).getGlobalIndexed(clause)
                    else ->
                        variableIndex.getIndexed(clause) +
                        compoundIndex.getIndexed(clause)
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
                        ?: (compoundIndex as IndexingNode).getGlobalFirstIndexed(clause)
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
                        (compoundIndex as IndexingNode).getGlobalFirstIndexed(clause)
                    )
                )
                else -> Utils.comparePriority(
                    variableIndex.getFirstIndexed(clause),
                    compoundIndex.getFirstIndexed(clause)
                )
            }

        protected fun invalidCache(result: Sequence<*>) {
            if (result.any()) {
                cache.clear()
                isCacheValid = false
            }
        }

        private fun regenerateCache() {
            if(!isCacheValid) {
                cache.addAll(
                    if(ordered) {
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
                isCacheValid = true
            }
        }

        private fun Clause.nestedFirstArgument(): Term =
            this.head!!.nestedFirstArgument(nestingLevel + 1)

        private fun assertByFirstParameter(clause: IndexedClause) : IndexingLeaf =
            clause.innerClause.nestedFirstArgument().let {
                when(it){
                    is Numeric -> numericIndex
                    is Atom -> atomicIndex
                    is Var -> variableIndex
                    else -> compoundIndex
                }
            }

    }

    internal class FamilyArityIndexingNode(
        private val ordered: Boolean,
        nestingLevel: Int
    ) : FamilyArityReteNode(ordered, nestingLevel), ArityIndexing{

        override fun getGlobalFirstIndexed(clause: Clause): SituatedIndexedClause? {
            TODO("Not yet implemented")
        }

        override fun getGlobalIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
            TODO("Not yet implemented")
        }

        override fun retractAllGlobalIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
            TODO("Not yet implemented")
        }

        override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? =
            if(ordered) orderedLookahead(clause)
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
            }
            else {
                retractAllUnorderedIndexed(clause).let {
                    invalidCache(it)
                    it
                }
            }

    }

    internal open class ZeroArityReteNode(
        ordered: Boolean,
        nestingLevel: Int
    ) : ArityNode(), ArityRete {

        protected val atoms: IndexingLeaf = AtomIndex(ordered, nestingLevel)

        private val cache: MutableList<SituatedIndexedClause> = dequeOf()
        private var isCacheValid: Boolean = true

        override fun retractFirst(clause: Clause): Sequence<Clause> {
            val result = atoms.getFirstIndexed(clause)
            return if(result == null) {
                emptySequence()
            } else {
                result.removeFromIndex()
                sequenceOf(result.innerClause).let {
                    invalidCache(it)
                    it
                }
            }
        }

        override fun get(clause: Clause): Sequence<Clause> =
            atoms.get(clause)

        override fun assertA(clause: IndexedClause) =
            atoms.assertA(clause)

        override fun assertZ(clause: IndexedClause) =
            atoms.assertZ(clause)

        override fun retractAll(clause: Clause): Sequence<Clause> =
            atoms.retractAll(clause).let {
                invalidCache(it)
                it
            }

        override fun getCache(): Sequence<SituatedIndexedClause> {
            regenerateCache()
            return cache.asSequence()
        }

        protected fun invalidCache(result: Sequence<*>) {
            if (result.any()) {
                cache.clear()
                isCacheValid = false
            }
        }

        private fun regenerateCache() {
            if(!isCacheValid) {
                cache.addAll(
                    atoms.getCache()
                )
                isCacheValid = true
            }
        }

    }

    internal class ZeroArityIndexingNode(
        ordered: Boolean,
        nestingLevel: Int
    ) : ZeroArityReteNode(ordered, nestingLevel), ArityIndexing{

        override fun getGlobalFirstIndexed(clause: Clause): SituatedIndexedClause? {
            TODO("Not yet implemented")
        }

        override fun getGlobalIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
            TODO("Not yet implemented")
        }

        override fun retractAllGlobalIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
            TODO("Not yet implemented")
        }

        override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? =
            atoms.getFirstIndexed(clause)

        override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
            atoms.getIndexed(clause)

        override fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
            atoms.retractAllIndexed(clause).let {
                invalidCache(it)
                it
            }
    }

}