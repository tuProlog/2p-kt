package it.unibo.tuprolog.collections.rete.custom.nodes

import it.unibo.tuprolog.collections.rete.custom.IndexingNode
import it.unibo.tuprolog.collections.rete.custom.clause.IndexedClause
import it.unibo.tuprolog.collections.rete.custom.Utils.arityOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.custom.ReteNode
import it.unibo.tuprolog.collections.rete.custom.TopLevelReteNode
import it.unibo.tuprolog.collections.rete.custom.clause.SituatedIndexedClause
import it.unibo.tuprolog.core.Clause

internal interface FunctorRete: ReteNode, TopLevelReteNode

internal interface FunctorIndexing: ReteNode, IndexingNode

internal sealed class FunctorNode : ReteNode {

    internal class TopLevelFunctorReteNode(
        private val ordered: Boolean,
        private val nestingLevel: Int
    ) : FunctorNode(), FunctorRete {

        private val children: MutableMap<Int, TopLevelReteNode> = mutableMapOf()

        override fun get(clause: Clause): Sequence<Clause> =
            selectArity(clause)?.get(clause) ?: emptySequence()

        override fun assertA(clause: IndexedClause) {
            chooseAssertionBranch(clause, ReteNode::assertA)
        }

        override fun assertZ(clause: IndexedClause) {
            chooseAssertionBranch(clause, ReteNode::assertZ)
        }

        override fun retractFirst(clause: Clause): Sequence<Clause> =
            selectArity(clause)?.retractFirst(clause) ?: emptySequence()

        override fun retractAll(clause: Clause): Sequence<Clause> =
            selectArity(clause)?.retractAll(clause) ?: emptySequence()

        private fun selectArity(clause: Clause) =
            children[clause.head!!.arityOfNestedFirstArgument(nestingLevel)]

        private fun chooseAssertionBranch(clause: IndexedClause, op: ReteNode.(IndexedClause) -> Unit) {
            clause.innerClause.head!!.arityOfNestedFirstArgument(nestingLevel).let {
                when(it) {
                    0 -> children.getOrPut(it) {
                        ArityNode.ZeroArityReteNode(ordered, nestingLevel)
                    }
                    else -> children.getOrPut(it) {
                        ArityNode.FamilyArityReteNode(ordered, nestingLevel)
                    }
                }
            }.run { op(clause) }
        }
    }

    internal class FunctorIndexingNode(
        private val ordered: Boolean,
        private val nestingLevel: Int
    ) : FunctorNode(), FunctorIndexing {
        private val arities: MutableMap<Int, ArityIndexing> = mutableMapOf()

        override fun get(clause: Clause): Sequence<Clause> =
            arities[clause.nestedArity()]?.get(clause) ?: emptySequence()

        override fun assertA(clause: IndexedClause) {
            clause.nestedArity().let{
                when(it){
                    0 -> arities.getOrPut(it){
                        ArityNode.ZeroArityIndexingNode(ordered, nestingLevel)
                    }
                    else -> arities.getOrPut(it){
                        ArityNode.FamilyArityIndexingNode(ordered, nestingLevel)
                    }
                }.assertA(clause)
            }
        }

        override fun assertZ(clause: IndexedClause) {
            clause.nestedArity().let{
                when(it){
                    0 -> arities.getOrPut(it){
                        ArityNode.ZeroArityIndexingNode(ordered, nestingLevel)
                    }
                    else -> arities.getOrPut(it){
                        ArityNode.FamilyArityIndexingNode(ordered, nestingLevel)
                    }
                }.assertZ(clause)
            }
        }

        override fun retractAll(clause: Clause): Sequence<Clause> =
            arities[clause.nestedArity()]?.retractAll(clause) ?: emptySequence()

        override fun getFirstIndexed(clause: Clause): SituatedIndexedClause? =
            arities[clause.nestedArity()]?.getFirstIndexed(clause)

        override fun getIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
            arities[clause.nestedArity()]?.getIndexed(clause) ?: emptySequence()

        override fun retractAllIndexed(clause: Clause): Sequence<SituatedIndexedClause> =
            arities[clause.nestedArity()]?.retractAllIndexed(clause) ?: emptySequence()

        private fun Clause.nestedArity(): Int =
            this.head!!.arityOfNestedFirstArgument(nestingLevel)

        private fun IndexedClause.nestedArity(): Int =
            this.innerClause.head!!.arityOfNestedFirstArgument(nestingLevel)
    }

}