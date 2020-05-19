package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.IndexingNode
import it.unibo.tuprolog.collections.rete.custom.ReteNode
import it.unibo.tuprolog.collections.rete.custom.TopLevelReteNode
import it.unibo.tuprolog.collections.rete.custom.Utils
import it.unibo.tuprolog.collections.rete.custom.Utils.arityOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.Utils.nestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.utils.dequeOf

internal interface FunctorRete : ReteNode, TopLevelReteNode

internal interface FunctorIndexing : ReteNode, IndexingNode

internal sealed class FunctorNode : ReteNode {

    internal class TopLevelFunctorReteNode(
        private val ordered: Boolean,
        private val nestingLevel: Int
    ) : FunctorNode(), FunctorRete {

        private val children: MutableMap<Int, TopLevelReteNode> = mutableMapOf()
        private val cache: MutableList<SituatedIndexedClause> = dequeOf()
        private var isCacheValid: Boolean = true

        override fun get(clause: Clause): Sequence<Clause> =
            selectArity(clause)?.get(clause) ?: emptySequence()

        override fun assertA(clause: IndexedClause) {
            chooseAssertionBranch(clause, ReteNode::assertA)
        }

        override fun assertZ(clause: IndexedClause) {
            chooseAssertionBranch(clause, ReteNode::assertZ)
        }

        override fun retractFirst(clause: Clause): Sequence<Clause> =
            selectArity(clause)?.retractFirst(clause)?.let {
                invalidCache(it)
                it
            } ?: emptySequence()

        override fun retractAll(clause: Clause): Sequence<Clause> =
            selectArity(clause)?.retractAll(clause)?.let {
                invalidCache(it)
                it
            } ?: emptySequence()

        override fun getCache(): Sequence<SituatedIndexedClause> {
            regenerateCache()
            return cache.asSequence()
        }

        private fun selectArity(clause: Clause) =
            children[clause.head!!.arityOfNestedFirstArgument(nestingLevel)]

        private fun chooseAssertionBranch(clause: IndexedClause, op: ReteNode.(IndexedClause) -> Unit) {
            clause.innerClause.head!!.arityOfNestedFirstArgument(nestingLevel).let {
                when (it) {
                    0 -> children.getOrPut(it) {
                        ArityNode.ZeroArityReteNode(ordered)
                    }
                    else -> children.getOrPut(it) {
                        ArityNode.FamilyArityReteNode(ordered, nestingLevel)
                    }
                }
            }.run { op(clause) }
        }

        private fun invalidCache(result: Sequence<*>) {
            if (result.any()) {
                cache.clear()
                isCacheValid = false
            }
        }

        private fun regenerateCache() {
            if (!isCacheValid) {
                cache.addAll(
                    if (ordered) {
                        Utils.merge(
                            children.values.map {
                                it.getCache()
                            }
                        )
                    } else {
                        Utils.flattenIndexed(
                            children.values.map { outer ->
                                outer.getCache()
                            }
                        )
                    }
                )
                isCacheValid = true
            }
        }
    }

    internal class FunctorIndexingNode(
        private val ordered: Boolean,
        private val nestingLevel: Int
    ) : FunctorNode(), FunctorIndexing {

        private val arities: MutableMap<Int, ArityIndexing> = mutableMapOf()
        private val cache: MutableList<SituatedIndexedClause> = dequeOf()
        private var isCacheValid: Boolean = true

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
            arities[clause.nestedArity()]?.retractAll(clause)?.let {
                invalidCache(it)
                it
            } ?: emptySequence()

        override fun getCache(): Sequence<SituatedIndexedClause> {
            regenerateCache()
            return cache.asSequence()
        }

        override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? =
            arities[clause.nestedArity()]?.getFirstIndexed(clause)

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
            arities[clause.nestedArity()]?.retractAllIndexed(clause)?.let {
                invalidCache(it)
                it
            } ?: emptySequence()

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

        private fun invalidCache(result: Sequence<*>) {
            if (result.any()) {
                cache.clear()
                isCacheValid = false
            }
        }

        private fun regenerateCache() {
            if (!isCacheValid) {
                cache.addAll(
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
                isCacheValid = true
            }
        }
    }

}