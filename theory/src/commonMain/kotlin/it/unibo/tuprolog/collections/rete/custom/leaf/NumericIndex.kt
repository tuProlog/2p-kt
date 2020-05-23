package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.IndexingLeaf
import it.unibo.tuprolog.collections.rete.custom.Retractable
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.dequeOf

internal class NumericIndex(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingLeaf, Retractable {

    private val index: MutableMap<Numeric, MutableList<SituatedIndexedClause>> = mutableMapOf()

    override fun get(clause: Clause): Sequence<Clause> {
        return if (clause.nestedFirstArgument().isNumber)
            index[clause.asInnerNumeric()]
                ?.asSequence()
                ?.filter { it.innerClause matches clause }
                ?.map { it.innerClause }
                ?: emptySequence()
        else extractGlobalSequence(clause)
    }

    override fun assertA(clause: IndexedClause) {
        if (ordered) {
            clause.asInnerNumeric().let {
                index.getOrPut(it) { dequeOf() }
                    .addFirst(SituatedIndexedClause.of(clause, this))
            }
        } else {
            assertZ(clause)
        }
    }

    override fun assertZ(clause: IndexedClause) {
        clause.asInnerNumeric().let {
            index.getOrPut(it) { dequeOf() }
                .add(SituatedIndexedClause.of(clause, this))
        }
    }

    override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? {
        if (clause.nestedFirstArgument().isNumber) {
            index[clause.asInnerNumeric()].let {
                return if (it == null) null
                else extractFirst(clause, it)
            }
        } else {
            return extractFirst(clause)
        }
    }

    private fun extractFirst(clause: Clause): SituatedIndexedClause? =
        index.values.mapNotNull {
            extractFirst(clause, it)
        }.min()

    private fun extractFirst(clause: Clause, index: MutableList<SituatedIndexedClause>): SituatedIndexedClause? {
        val actualIndex = index.indexOfFirst { it.innerClause matches clause }

        return if (actualIndex == -1) null
        else index[actualIndex]
    }

    override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        return if (clause.nestedFirstArgument().isNumber)
            index[clause.asInnerNumeric()]
                ?.asSequence()
                ?.filter { it.innerClause matches clause }
                ?: emptySequence()
        else extractGlobalIndexedSequence(clause)
    }

    override fun retractIndexed(indexed: SituatedIndexedClause) {
        index[indexed.asInnerNumeric()]!!.remove(indexed)
    }

    override fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        return if (clause.nestedFirstArgument().isNumber) {
            val partialIndex = index.getOrElse(clause.asInnerNumeric()) { mutableListOf() }
            return retractFromMutableList(clause, partialIndex)
        } else {
            Utils.merge(
                index.values.map {
                    retractFromMutableList(clause, it)
                }.toList()
            )
        }
    }

    private fun retractFromMutableList(clause: Clause, index: MutableList<SituatedIndexedClause>)
            : Sequence<SituatedIndexedClause> {
        val result = index.filter { it.innerClause matches clause }
        result.forEach { index.remove(it) }
        return result.asSequence()
    }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        retractAllIndexed(clause).map { it.innerClause }

    override fun invalidateCache() {}

    override fun getCache(): Sequence<SituatedIndexedClause> =
        Utils.merge(index.values.asSequence().map { it.asSequence() })

    override fun extractGlobalIndexedSequence(clause: Clause): Sequence<SituatedIndexedClause> =
        getCache().filter { it.innerClause matches clause }

    private fun extractGlobalSequence(clause: Clause): Sequence<Clause> =
        extractGlobalIndexedSequence(clause)
            .map { it.innerClause }

    private fun Clause.nestedFirstArgument(): Term =
        this.head!!.nestedFirstArgument(nestingLevel + 1)

    private fun Term.asNumeric(): Numeric =
        this as Numeric

    private fun Clause.asInnerNumeric(): Numeric =
        this.nestedFirstArgument().asNumeric()

    private fun SituatedIndexedClause.asInnerNumeric(): Numeric =
        this.innerClause.nestedFirstArgument() as Numeric

    private fun IndexedClause.asInnerNumeric(): Numeric =
        this.innerClause.nestedFirstArgument() as Numeric

}