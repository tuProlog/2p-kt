package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.ReteNode
import it.unibo.tuprolog.collections.rete.custom.TopLevelReteNode
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.Utils.arityOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.unify.Unificator.Companion.matches
import it.unibo.tuprolog.utils.Cached
import it.unibo.tuprolog.utils.dequeOf

internal sealed class FunctorNode : ReteNode {

    internal class TopLevelFunctorReteNode(
        private val ordered: Boolean,
        private val nestingLevel: Int
    ) : FunctorNode(), FunctorRete {

        private val arities: MutableMap<Int, TopLevelReteNode> = mutableMapOf()
        private val theoryCache: Cached<MutableList<SituatedIndexedClause>> = Cached.of(this::regenerateCache)

        override fun get(clause: Clause): Sequence<Clause> =
            selectArity(clause)?.get(clause) ?: emptySequence()

        override fun assertA(clause: IndexedClause) {
            chooseAssertionBranch(clause, ReteNode::assertA)
        }

        override fun assertZ(clause: IndexedClause) {
            chooseAssertionBranch(clause, ReteNode::assertZ)
        }

        override fun retractFirst(clause: Clause): Sequence<Clause> =
            selectArity(clause)?.retractFirst(clause)?.invalidatingCacheIfNonEmpty()
                ?: emptySequence()

        override fun retractAll(clause: Clause): Sequence<Clause> =
            selectArity(clause)?.retractAll(clause)?.invalidatingCacheIfNonEmpty()
                ?: emptySequence()

        override fun getCache(): Sequence<SituatedIndexedClause> =
            theoryCache.value.asSequence()

        private fun selectArity(clause: Clause) =
            arities[clause.head!!.arityOfNestedFirstArgument(nestingLevel)]

        private fun chooseAssertionBranch(clause: IndexedClause, op: ReteNode.(IndexedClause) -> Unit) {
            clause.innerClause.head!!.arityOfNestedFirstArgument(nestingLevel).let {
                when (it) {
                    0 -> arities.getOrPut(it) {
                        ArityNode.ZeroArityReteNode(ordered)
                    }
                    else -> arities.getOrPut(it) {
                        ArityNode.FamilyArityReteNode(ordered, nestingLevel)
                    }
                }
            }.run { op(clause) }
        }

        override fun invalidateCache() {
            theoryCache.invalidate()
            // arities.values.forEach { it.invalidateCache() }
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

    internal class FunctorIndexingNode(
        private val ordered: Boolean,
        private val nestingLevel: Int
    ) : FunctorNode(), FunctorIndexing {

        private val arities: MutableMap<Int, ArityIndexing> = mutableMapOf()
        private val theoryCache: Cached<MutableList<SituatedIndexedClause>> = Cached.of(this::regenerateCache)

        override fun get(clause: Clause): Sequence<Clause> =
            arities[clause.nestedArity()]?.get(clause) ?: emptySequence()

        override fun assertA(clause: IndexedClause) {
            arities.getOrPut(clause.nestedArity()) {
                ArityNode.FamilyArityIndexingNode(ordered, nestingLevel)
            }.assertA(clause)
        }

        override fun assertZ(clause: IndexedClause) {
            arities.getOrPut(clause.nestedArity()) {
                ArityNode.FamilyArityIndexingNode(ordered, nestingLevel)
            }.assertZ(clause)
        }

        override fun retractAll(clause: Clause): Sequence<Clause> =
            arities[clause.nestedArity()]?.retractAll(clause)?.invalidatingCacheIfNonEmpty()
                ?: emptySequence()

        override fun getCache(): Sequence<SituatedIndexedClause> =
            theoryCache.value.asSequence()

        override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? =
            if (clause.isGlobal()) {
                Utils.merge(
                    arities.values.map {
                        it.extractGlobalIndexedSequence(clause)
                    }
                ).firstOrNull { it.innerClause matches clause }
            }
            else arities[clause.nestedArity()]?.getFirstIndexed(clause)

        override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> {
            return if (clause.isGlobal()) {
                Utils.merge(
                    arities.values.map {
                        it.getIndexed(clause)
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
                    })
                    .filter { it.innerClause matches clause }
                    .toList()
                if (partialResult.isNotEmpty()) {
                    invalidateCache()
                    partialResult.forEach { it.removeFromIndex() }
                }
                partialResult.asSequence()
            }
            else {
                arities[clause.nestedArity()]?.retractAllIndexed(clause)?.invalidatingCacheIfNonEmpty()
                    ?: emptySequence()
            }

        override fun extractGlobalIndexedSequence(clause: Clause): Sequence<SituatedIndexedClause> {
            return if (ordered)
                Utils.merge(
                    arities.values.map {
                        it.extractGlobalIndexedSequence(clause)
                    }
                )
            else
                Utils.flattenIndexed(
                    arities.values.map {
                        it.extractGlobalIndexedSequence(clause)
                    }
                )
        }

        private fun Clause.isGlobal(): Boolean =
            this.head!!.nestedFirstArgument(nestingLevel) is Var

        private fun Clause.nestedArity(): Int =
            this.head!!.arityOfNestedFirstArgument(nestingLevel)

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

}