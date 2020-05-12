package it.unibo.tuprolog.collections.rete.nodes.custom.nodes

import it.unibo.tuprolog.collections.rete.nodes.custom.IndexedClause
import it.unibo.tuprolog.collections.rete.nodes.custom.Utils.arityOfNestedFirstArgument
import it.unibo.tuprolog.collections.rete.nodes.custom.ReteNode
import it.unibo.tuprolog.core.Clause

internal class FunctorReteNode(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : ReteNode {

    private val children: MutableMap<Int, ReteNode> = mutableMapOf()

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
                    ArityReteNode.ZeroArityReteNode(ordered)
                }
                else -> children.getOrPut(it) {
                    ArityReteNode.FamilyArityReteNode(ordered)
                }
            }
        }.run { op(clause) }
    }

}