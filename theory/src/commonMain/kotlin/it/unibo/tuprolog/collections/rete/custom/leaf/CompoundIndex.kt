package it.unibo.tuprolog.collections.rete.custom.leaf

import it.unibo.tuprolog.collections.rete.custom.AbstractReteNode
import it.unibo.tuprolog.collections.rete.custom.IndexingNode
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.Utils.functorOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.collections.rete.custom.nodes.FunctorIndexing
import it.unibo.tuprolog.collections.rete.custom.nodes.FunctorIndexingNode
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.unify.Unificator
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.dequeOf

internal class CompoundIndex(
    unificator: Unificator,
    private val ordered: Boolean,
    private val nestingLevel: Int
) : IndexingNode, AbstractReteNode(unificator) {

    private val functors: MutableMap<String, FunctorIndexing> = mutableMapOf()
    private val theoryCache: Cached<MutableList<SituatedIndexedClause>> = Cached.of(this::regenerateCache)

    override val size: Int
        get() = functors.values.asSequence().map { it.size }.sum()

    override val isEmpty: Boolean
        get() = functors.isEmpty() || functors.values.all { it.isEmpty }

    override fun get(clause: Clause): Sequence<Clause> =
        if (clause.isGlobal()) {
            if (ordered) {
                Utils.merge(
                    functors.values.asSequence().flatMap {
                        it.getIndexed(clause)
                    }
                ).map { it.innerClause }
            } else {
                Utils.flatten(
                    functors.values.asSequence().flatMap {
                        it.get(clause)
                    }
                )
            }
        } else {
            functors[clause.nestedFunctor()]?.get(clause) ?: emptySequence()
        }

    override fun assertA(clause: IndexedClause) =
        clause.nestedFunctor().let {
            if (ordered) {
                functors.getOrPut(it) {
                    FunctorIndexingNode(unificator, ordered, nestingLevel)
                }.assertA(clause + this)
            } else {
                assertZ(clause)
            }
        }

    override fun assertZ(clause: IndexedClause) =
        clause.nestedFunctor().let {
            functors.getOrPut(it) {
                FunctorIndexingNode(unificator, ordered, nestingLevel)
            }.assertZ(clause + this)
        }

    override fun retractAll(clause: Clause): Sequence<Clause> =
        if (ordered) {
            retractAllOrdered(clause)
        } else {
            retractAllUnordered(clause)
        }

    override fun getCache(): Sequence<SituatedIndexedClause> =
        theoryCache.value.asSequence()

    private fun retractAllOrdered(clause: Clause): Sequence<Clause> =
        if (clause.isGlobal()) {
            Utils.merge(
                functors.values.map {
                    it.retractAllIndexed(clause)
                }
            ).map { it.innerClause }
        } else {
            functors[clause.nestedFunctor()]
                ?.retractAll(clause)
                ?: emptySequence()
        }

    private fun retractAllUnordered(clause: Clause): Sequence<Clause> =
        if (clause.isGlobal()) {
            Utils.flatten(
                functors.values.map {
                    it.retractAll(clause)
                }
            )
        } else {
            functors[clause.nestedFunctor()]
                ?.retractAll(clause)
                ?: emptySequence()
        }

    override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? =
        if (clause.isGlobal()) {
            Utils.merge(
                sequenceOf(
                    functors.values.mapNotNull {
                        it.getFirstIndexed(clause)
                    }.asSequence()
                )
            ).firstOrNull()
        } else {
            functors[clause.nestedFunctor()]
                ?.getFirstIndexed(clause)
        }

    override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        if (clause.isGlobal()) {
            Utils.merge(
                functors.values.map {
                    it.extractGlobalIndexedSequence(clause)
                }
            )
        } else {
            functors[clause.nestedFunctor()]
                ?.getIndexed(clause)
                ?: emptySequence()
        }

    override fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
        if (clause.isGlobal()) {
            Utils.merge(
                functors.values.map {
                    it.retractAllIndexed(clause)
                }
            )
        } else {
            functors[clause.nestedFunctor()]
                ?.retractAllIndexed(clause)
                ?: emptySequence()
        }

    override fun extractGlobalIndexedSequence(clause: Clause): Sequence<SituatedIndexedClause> {
        return if (ordered) {
            Utils.merge(
                functors.values.map {
                    it.extractGlobalIndexedSequence(clause)
                }
            )
        } else {
            Utils.flattenIndexed(
                functors.values.map {
                    it.extractGlobalIndexedSequence(clause)
                }
            )
        }
    }

    private fun Clause.nestedFunctor(): String =
        this.head!!.functorOfNestedFirstArgument(nestingLevel)

    private fun IndexedClause.nestedFunctor(): String =
        this.innerClause.head!!.functorOfNestedFirstArgument(nestingLevel)

    private fun Clause.isGlobal(): Boolean =
        this.head!!.nestedFirstArgument(nestingLevel).isVar

    override fun invalidateCache() {
        theoryCache.invalidate()
//        functors.values.forEach { it.invalidateCache() }
    }

    private fun regenerateCache(): MutableList<SituatedIndexedClause> =
        dequeOf(
            if (ordered) {
                Utils.merge(
                    functors.values.map {
                        it.getCache()
                    }
                )
            } else {
                Utils.flattenIndexed(
                    functors.values.map { outer ->
                        outer.getCache()
                    }
                )
            }
        )
}
