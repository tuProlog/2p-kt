package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.collections.rete.nodes.custom.index.*
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.unify.Unificator.Companion.matches

internal sealed class ArityNode(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : ReteNode {

    protected val orderedClauses: MutableList<IndexedClause> = dequeOf()

    override fun get(clause: Clause): Sequence<Clause> {
        return if(ordered) {
            mergeSortResults(clause)
        } else {
            mergeResults(clause)
        }
    }

    override fun retractFirst(clause: Clause): Sequence<Clause> {
        return if(ordered) {
            retractFirstResult(clause)
        } else {
            retractAnyResult(clause)
        }
    }

    override fun retractAll(clause: Clause): Sequence<Clause> {
        return if (ordered) {
            retractAllOrdered(clause)
        } else {
            retractAllUnordered(clause)
        }
    }

    protected abstract fun mergeSortResults(clause: Clause): Sequence<Clause>

    protected fun mergeSort(vararg sequence: Sequence<IndexedClause>): Sequence<Clause> {
        TODO("MERGESORT NOT IMPLEMENTED")
    }

    protected abstract fun mergeResults(clause: Clause): Sequence<Clause>

    protected abstract fun retractAllOrdered(clause: Clause): Sequence<Clause>

    protected abstract fun retractAllUnordered(clause: Clause): Sequence<Clause>

    protected abstract fun retractFirstResult(clause: Clause): Sequence<Clause>

    protected abstract fun retractAnyResult(clause: Clause): Sequence<Clause>

    internal class FamilyArityNode(
        ordered: Boolean,
        nestingLevel: Int
    ) : ArityNode(ordered, nestingLevel) {

        private val numericIndex =
            if (ordered) NumericIndex.OrderedIndex(nestingLevel)
            else NumericIndex.UnorderedIndex(nestingLevel)
        private val atomicIndex =
            if (ordered) AtomIndex.OrderedIndex(nestingLevel)
            else AtomIndex.UnorderedIndex(nestingLevel)
        private val variableIndex =
            if (ordered) VariableIndex.OrderedIndex(nestingLevel)
            else VariableIndex.UnorderedIndex(nestingLevel)
        private val compoundIndex =
            if (ordered) CompoundIndex.OrderedIndex(nestingLevel + 1)
            else CompoundIndex.UnorderedIndex(nestingLevel + 1)

        override fun assertA(clause: IndexedClause) {
            assertByFirstParameter(clause).assertA(clause)
            orderedClauses.addFirst(clause)
        }

        override fun assertZ(clause: IndexedClause) {
            assertByFirstParameter(clause).assertZ(clause)
            orderedClauses.add(clause)
        }

        override fun retractAnyResult(clause: Clause): Sequence<Clause> =
            retractIndexedClause(anyLookahead(clause))

        override fun retractFirstResult(clause: Clause): Sequence<Clause> =
            retractIndexedClause(singleLookahead(clause))

        override fun retractAllOrdered(clause: Clause): Sequence<Clause> {
            clause.firstParameter().let {
                return when(it) {
                    is Numeric -> {
                        mergeSort(
                            variableIndex.retractAllIndexed(clause),
                            numericIndex.retractAllIndexed(clause)
                        )
                    }
                    is Atom -> {
                        mergeSort(
                            variableIndex.retractAllIndexed(clause),
                            atomicIndex.retractAllIndexed(clause)
                        )
                    }
                    is Var -> {
                        mergeSort(
                            variableIndex.retractAllIndexed(clause),
                            numericIndex.retractAllIndexed(clause),
                            atomicIndex.retractAllIndexed(clause),
                            compoundIndex.retractAllIndexed(clause)
                        )
                    }
                    else -> {
                        mergeSort(
                            variableIndex.retractAllIndexed(clause),
                            compoundIndex.retractAllIndexed(clause)
                        )
                    }
                }
            }
        }

        override fun retractAllUnordered(clause: Clause): Sequence<Clause> {
            clause.firstParameter().let {
                return when(it) {
                    is Numeric ->
                        variableIndex.retractAll(clause) +
                        numericIndex.retractAll(clause)
                    is Atom ->
                        variableIndex.retractAll(clause) +
                        atomicIndex.retractAll(clause)
                    is Var ->
                        variableIndex.retractAll(clause) +
                        numericIndex.retractAll(clause) +
                        atomicIndex.retractAll(clause) +
                        compoundIndex.retractAll(clause)
                    else ->
                        variableIndex.retractAll(clause) +
                        compoundIndex.retractAll(clause)
                }
            }
        }

        private fun retractIndexedClause(indexedClause: IndexedClause?): Sequence<Clause> {
            if (indexedClause == null)
                return emptySequence()
            else
                indexedClause.innerClause.firstParameter().let {
                    return when(it) {
                        is Numeric -> numericIndex.retractIndexed(indexedClause)
                        is Atom -> atomicIndex.retractIndexed(indexedClause)
                        is Var -> variableIndex.retractIndexed(indexedClause)
                        else -> compoundIndex.retractIndexed(indexedClause)
                    }
                }
        }

        private fun anyLookahead(clause: Clause): IndexedClause? =
            when (clause.firstParameter()) {
                is Numeric -> {
                    variableIndex.getAny(clause)
                    ?: numericIndex.getAny(clause)
                }
                is Atom -> {
                    variableIndex.getAny(clause)
                    ?: atomicIndex.getAny(clause)
                }
                is Var -> {
                    variableIndex.getAny(clause)
                    ?: numericIndex.getAny(clause)
                    ?: atomicIndex.getAny(clause)
                    ?: compoundIndex.getAny(clause)
                }
                else -> {
                    variableIndex.getAny(clause)
                    ?: compoundIndex.getAny(clause)
                }
            }


        private fun singleLookahead(clause: Clause): IndexedClause? =
            when (clause.firstParameter()) {
                is Numeric -> {
                    comparePriority(
                        numericIndex.getFirst(clause),
                        variableIndex.getFirst(clause)
                    )
                }
                is Atom -> {
                    comparePriority(
                        atomicIndex.getFirst(clause),
                        variableIndex.getFirst(clause)
                    )
                }
                is Var -> comparePriority(
                    comparePriority(
                        numericIndex.getFirst(clause),
                        atomicIndex.getFirst(clause)
                    ),
                    comparePriority(
                        variableIndex.getFirst(clause),
                        compoundIndex.getFirst(clause)
                    )
                )
                else -> comparePriority(
                    variableIndex.getFirst(clause),
                    compoundIndex.getFirst(clause)
                )
            }

        private fun comparePriority(a: IndexedClause?, b: IndexedClause?): IndexedClause? =
            when {
                a == null && b == null -> null
                a == null -> b
                b == null -> a
                a < b -> a
                else -> b
            }

        private fun Clause.firstParameter(): Term =
            this.args[0]

        private fun assertByFirstParameter(clause: IndexedClause) : IndexingLeaf =
            clause.innerClause.firstParameter().let {
                when(it){
                    is Numeric -> numericIndex
                    is Atom -> atomicIndex
                    is Var -> variableIndex
                    else -> compoundIndex
                }
            }

        override fun mergeSortResults(clause: Clause): Sequence<Clause> {
            clause.firstParameter().let {
                return when (it) {
                    is Numeric ->
                        mergeSort(
                            variableIndex.getIndexed(clause),
                            numericIndex.getIndexed(clause)
                        )
                    is Atom ->
                        mergeSort(
                            variableIndex.getIndexed(clause),
                            atomicIndex.getIndexed(clause)
                        )
                    is Var ->
                        mergeSort(
                            variableIndex.getIndexed(clause),
                            numericIndex.getIndexed(clause),
                            atomicIndex.getIndexed(clause),
                            compoundIndex.getIndexed(clause)
                        )
                    else ->
                        mergeSort(
                            variableIndex.getIndexed(clause),
                            compoundIndex.getIndexed(clause)
                        )
                }
            }
        }


        override fun mergeResults(clause: Clause): Sequence<Clause> {
            clause.firstParameter().let {
                return when (it) {
                    is Numeric ->
                        variableIndex.get(clause) +
                                numericIndex.get(clause)
                    is Atom ->
                        variableIndex.get(clause) +
                                atomicIndex.get(clause)
                    is Var ->
                        variableIndex.get(clause) +
                                numericIndex.get(clause) +
                                atomicIndex.get(clause) +
                                compoundIndex.get(clause)
                    else ->
                        variableIndex.get(clause) +
                                compoundIndex.get(clause)
                }
            }
        }
    }

    internal class ZeroArityNode(
        private val ordered: Boolean,
        nestingLevel: Int
    ) : ArityNode(ordered, nestingLevel) {

        override fun retractFirstResult(clause: Clause): Sequence<Clause> {
            orderedClauses.indexOfFirst { it.innerClause matches clause }.let {
                return when(it){
                    -1 -> emptySequence()
                    else -> {
                        val result = orderedClauses[it]
                        orderedClauses.removeAt(it)
                        sequenceOf(result.innerClause)
                    }
                }
            }
        }

        override fun retractAnyResult(clause: Clause): Sequence<Clause> =
            retractFirst(clause)

        override fun get(clause: Clause): Sequence<Clause> =
            orderedClauses
                .filter { it.innerClause matches clause }
                .map { it.innerClause }
                .asSequence()

        override fun mergeSortResults(clause: Clause): Sequence<Clause> =
            this.get(clause)

        override fun mergeResults(clause: Clause): Sequence<Clause> =
            this.get(clause)

        override fun retractAllOrdered(clause: Clause): Sequence<Clause> {
            val result = orderedClauses.filter { it.innerClause matches clause }
            result.forEach { orderedClauses.remove(it) }
            return result.asSequence().map { it.innerClause }
        }

        override fun retractAllUnordered(clause: Clause): Sequence<Clause> =
            retractAllOrdered(clause)

        override fun assertA(clause: IndexedClause) {
            if(ordered) orderedClauses.addFirst(clause)
            else orderedClauses.add(clause)
        }

        override fun assertZ(clause: IndexedClause) {
            orderedClauses.add(clause)
        }
    }

}