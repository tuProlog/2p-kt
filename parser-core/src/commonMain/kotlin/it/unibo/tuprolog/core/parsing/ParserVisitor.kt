package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Real
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.parser.tree.BlockNode
import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.ListNode
import it.unibo.tuprolog.parser.tree.NumberKind
import it.unibo.tuprolog.parser.tree.NumberNode
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.StructureNode
import it.unibo.tuprolog.parser.tree.SyntaxNodeVisitor
import it.unibo.tuprolog.parser.tree.TheoryNode
import it.unibo.tuprolog.parser.tree.VariableNode

class ParserVisitor(
    val scope: Scope,
): SyntaxNodeVisitor<Term> {
    override fun visitTheory(node: TheoryNode): Term =
        error("[BUG] ${node::class.simpleName} should not be visited by ${ParserVisitor::class.simpleName}")

    override fun visitOperator(node: OperatorExpressionNode): Term =
        scope.structOf(
            node.operator.definition.name,
            node.children.map { it.accept(this) },
        )

    override fun visitNumber(node: NumberNode): Term =
        when (node.numberKind) {
            NumberKind.REAL -> scope.realOf(node.digits)
            NumberKind.CHARACTER_CODE -> scope.intOf(node.characterCode!!)
            else -> scope.intOf(node.digits, node.radix!!)
        }

    override fun visitVariable(node: VariableNode): Term =
        scope.varOf(node.name)

    override fun visitStructure(node: StructureNode): Term =
        scope.structOf(node.functor, node.children.map { it.accept(this) })

    override fun visitList(node: ListNode): Term =
        scope.logicListFrom(
            terms = node.items.map { it.accept(this) },
            last = node.tail?.accept(this),
        )

    override fun visitBlock(node: BlockNode): Term =
        scope.blockOf(node.items.map { it.accept(this) })
}
