package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.parser.tree.BlockNode
import it.unibo.tuprolog.parser.tree.ListNode
import it.unibo.tuprolog.parser.tree.NumberKind
import it.unibo.tuprolog.parser.tree.NumberNode
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.StructureNode
import it.unibo.tuprolog.parser.tree.SyntaxNodeVisitor
import it.unibo.tuprolog.parser.tree.TheoryNode
import it.unibo.tuprolog.parser.tree.VariableNode

open class PrologTermParserVisitor(
    val scope: Scope,
) : SyntaxNodeVisitor<Term> {
    override fun visitTheory(node: TheoryNode): Term =
        error("[BUG] ${node::class.simpleName} should not be visited by ${PrologTermParserVisitor::class.simpleName}")

    override fun visitOperator(node: OperatorExpressionNode): Term =
        scope.structOf(
            node.operator.definition.name,
            node.children.map { it.accept(this) },
        )

    override fun visitNumber(node: NumberNode): Term {
        val signedDigits = if (node.sign < 0) "-${node.digits}" else node.digits
        return when (node.numberKind) {
            NumberKind.REAL -> scope.realOf(signedDigits)
            NumberKind.CHARACTER_CODE -> scope.intOf(node.sign * node.characterCode!!)
            else -> scope.intOf(signedDigits, node.radix!!)
        }
    }

    override fun visitVariable(node: VariableNode): Term =
        if (node.isAnonymous) {
            scope.anonymous()
        } else {
            scope.varOf(node.name)
        }

    override fun visitStructure(node: StructureNode): Term =
        scope.structOf(node.functor, node.children.map { it.accept(this) })

    override fun visitList(node: ListNode): Term =
        scope.logicListFrom(
            terms = node.items.map { it.accept(this) },
            last = node.tail?.accept(this) ?: scope.emptyLogicList,
        )

    override fun visitBlock(node: BlockNode): Term = scope.blockOf(node.items.map { it.accept(this) })
}
