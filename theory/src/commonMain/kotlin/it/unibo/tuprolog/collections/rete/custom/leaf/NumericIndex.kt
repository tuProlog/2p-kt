package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.Retractable
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.addFirst
import it.unibo.tuprolog.utils.buffered
import it.unibo.tuprolog.utils.dequeOf

internal class NumericIndex(
    unificator: Unificator,
    private val ordered: Boolean,
    private val nestingLevel: Int
) : AbstractIndexingLeaf(unificator), Retractable {

    private val index: MutableMap<Numeric, MutableList<SituatedIndexedClause>> = mutableMapOf()

    override val size: Int
        get() = index.values.asSequence().map { it.size }.sum()

    override val isEmpty: Boolean
        get() = index.isEmpty() || index.values.all { it.isEmpty() }

    override fun get(clause: Clause): Sequence<Clause> {
        return if (clause.nestedFirstArgument().isNumber) {
            index[clause.asInnerNumeric()]
                ?.asSequence()
                ?.filter { unificator.match(it.innerClause, clause) }
                ?.map { it.innerClause }
                ?: emptySequence()
        } else {
            extractGlobalSequence(clause)
        }
    }

    override fun assertA(clause: IndexedClause) {
        if (ordered) {
            clause.asInnerNumeric().let {
                index.getOrPut(it) { dequeOf() }.addFirst(SituatedIndexedClause.of(clause + this, this))
            }
        } else {
            assertZ(clause)
        }
    }

    override fun assertZ(clause: IndexedClause) {
        clause.asInnerNumeric().let {
            index.getOrPut(it) { dequeOf() }.add(SituatedIndexedClause.of(clause + this, this))
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
        }.minOrNull()

    private fun extractFirst(clause: Clause, index: MutableList<SituatedIndexedClause>): SituatedIndexedClause? {
        val actualIndex = index.indexOfFirst { unificator.match(it.innerClause, clause) }

        return if (actualIndex == -1) {
            null
        } else {
            index[actualIndex]
        }
    }

    override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        return if (clause.nestedFirstArgument().isNumber) {
            index[clause.asInnerNumeric()]
                ?.asSequence()
                ?.filter { unificator.match(it.innerClause, clause) }
                ?: emptySequence()
        } else extractGlobalIndexedSequence(clause)
    }

    override fun retractIndexed(indexed: SituatedIndexedClause) {
        index[indexed.asInnerNumeric()]!!.remove(indexed)
    }

    override fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        return if (clause.nestedFirstArgument().isNumber) {
            when (val partialIndex = index[clause.asInnerNumeric()]) {
                null -> emptySequence()
                else -> removeAllLazily(partialIndex, clause).buffered()
            }
        } else {
            Utils.merge(
                index.values.asSequence().map {
                    removeAllLazily(it, clause)
                }
            ).buffered()
        }
    }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        retractAllIndexed(clause).map { it.innerClause }

    override fun getCache(): Sequence<SituatedIndexedClause> =
        Utils.merge(index.values.asSequence().map { it.asSequence() })

    override fun extractGlobalIndexedSequence(clause: Clause): Sequence<SituatedIndexedClause> =
        getCache().filter { unificator.match(it.innerClause, clause) }

    private fun extractGlobalSequence(clause: Clause): Sequence<Clause> =
        extractGlobalIndexedSequence(clause).map { it.innerClause }

    private fun Clause.nestedFirstArgument(): Term =
        this.head!!.nestedFirstArgument(nestingLevel + 1)

    private fun Clause.asInnerNumeric(): Numeric =
        this.nestedFirstArgument().castToNumeric()

    private fun SituatedIndexedClause.asInnerNumeric(): Numeric =
        this.innerClause.nestedFirstArgument().castToNumeric()

    private fun IndexedClause.asInnerNumeric(): Numeric =
        this.innerClause.nestedFirstArgument().castToNumeric()
}
