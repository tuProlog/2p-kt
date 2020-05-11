package it.unibo.tuprolog.collections.rete.nodes.engineered

import it.unibo.tuprolog.core.Clause

internal class FamilyFunctorNode(
    private val functor: String,
    private val ordered: Boolean
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
        children[clause.head!!.arity]

    private fun chooseAssertionBranch(clause: IndexedClause, op: ReteNode.(IndexedClause) -> Unit) {
        clause.innerClause.head!!.arity.let {
            when(it) {
                0 -> children.getOrPut(it) {
                    AbstractArityNode.ZeroArityNode(functor, it, ordered)
                }
                else -> children.getOrPut(it) {
                    AbstractArityNode.FamilyArityNode(functor, it, ordered)
                }
            }
        }.run { op(clause) }
    }
}