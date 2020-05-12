package it.unibo.tuprolog.collections.rete.nodes.custom

import it.unibo.tuprolog.collections.rete.nodes.custom.Nesting.arityOfNestedFirstArgument
import it.unibo.tuprolog.core.Clause

internal class FunctorNode(
    private val ordered: Boolean,
    private val nestingLevel: Int
) : ReteNode {

    private val children: MutableMap<Arity, ReteNode> = mutableMapOf()

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
                    ArityNode.ZeroArityNode(ordered, nestingLevel)
                }
                else -> children.getOrPut(it) {
                    ArityNode.FamilyArityNode(ordered, nestingLevel)
                }
            }
        }.run { op(clause) }
    }
}