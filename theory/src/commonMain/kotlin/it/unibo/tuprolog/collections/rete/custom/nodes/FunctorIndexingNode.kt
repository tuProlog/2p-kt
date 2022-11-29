package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.Utils.arityOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.dequeOf

internal class FunctorIndexingNode(
    unificator: Unificator,
    private val ordered: Boolean,
    private val nestingLevel: Int
) : FunctorNode(unificator), FunctorIndexing {

    private val arities: MutableMap<Int, ArityIndexing> = mutableMapOf()

    private val theoryCache: Cached<MutableList<SituatedIndexedClause>> = Cached.of(this::regenerateCache)

    override val size: Int
        get() = arities.values.asSequence().map { it.size }.sum()

    override val isEmpty: Boolean
        get() = arities.isEmpty() || arities.values.all { it.isEmpty }

    override fun get(clause: Clause): Sequence<Clause> =
        arities[clause.nestedArity()]?.get(clause) ?: emptySequence()

    override fun assertA(clause: IndexedClause) {
        arities.getOrPut(clause.nestedArity()) {
            FamilyArityIndexingNode(unificator, ordered, nestingLevel)
        }.assertA(clause + this)
    }

    override fun assertZ(clause: IndexedClause) {
        arities.getOrPut(clause.nestedArity()) {
            FamilyArityIndexingNode(unificator, ordered, nestingLevel)
        }.assertZ(clause + this)
    }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        arities[clause.nestedArity()]?.retractAll(clause)
            ?: emptySequence()

    override fun getCache(): Sequence<SituatedIndexedClause> =
        theoryCache.value.asSequence()

    override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? =
        if (clause.isGlobal()) {
            Utils.merge(
                arities.values.map {
                    it.extractGlobalIndexedSequence(clause)
                }
            ).firstOrNull { unificator.match(it.innerClause, clause) }
        } else arities[clause.nestedArity()]?.getFirstIndexed(clause)

    override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
        return if (clause.isGlobal()) {
            Utils.merge(
                arities.values.map {
                    it.extractGlobalIndexedSequence(clause)
                }
            )
        } else {
            arities[clause.nestedArity()]?.getIndexed(clause) ?: emptySequence()
        }
    }

    override fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        if (clause.isGlobal()) {
            val partialResult = Utils.merge(
                arities.values.map {
                    it.extractGlobalIndexedSequence(clause)
                }
            )
                .filter { unificator.match(it.innerClause, clause) }
                .toList()
            if (partialResult.isNotEmpty()) {
                invalidateCache()
                partialResult.forEach { it.removeFromIndex() }
            }
            partialResult.asSequence()
        } else {
            arities[clause.nestedArity()]?.retractAllIndexed(clause)
                ?: emptySequence()
        }

    override fun extractGlobalIndexedSequence(clause: Clause): Sequence<SituatedIndexedClause> {
        return if (ordered) {
            Utils.merge(
                arities.values.map {
                    it.extractGlobalIndexedSequence(clause)
                }
            )
        } else {
            Utils.flattenIndexed(
                arities.values.map {
                    it.extractGlobalIndexedSequence(clause)
                }
            )
        }
    }

    private fun Clause.isGlobal(): Boolean =
        this.isRule && this.castToRule().head.nestedFirstArgument(nestingLevel).isVar

    private fun Clause.nestedArity(): Int =
        asRule()?.head?.arityOfNestedFirstArgument(nestingLevel)
            ?: error("The nestedArity method cannot be invoked on non-rule clauses")

    private fun IndexedClause.nestedArity(): Int =
        this.innerClause.head!!.arityOfNestedFirstArgument(nestingLevel)

    override fun invalidateCache() {
        theoryCache.invalidate()
//            arities.values.forEach { it.invalidateCache() }
    }

    private fun regenerateCache(): MutableList<SituatedIndexedClause> =
        dequeOf(
            if (ordered) {
                Utils.merge(
                    arities.values.map {
                        it.getCache()
                    }
                )
            } else {
                Utils.flattenIndexed(
                    arities.values.map { outer ->
                        outer.getCache()
                    }
                )
            }
        )
}
