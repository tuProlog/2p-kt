package it.unibo.tuprolog.parser

import io.kotest.core.spec.style.FunSpec
import it.unibo.tuprolog.parser.operators.Associativity
import it.unibo.tuprolog.parser.operators.OperatorTables
import it.unibo.tuprolog.parser.tree.BlockNode
import it.unibo.tuprolog.parser.tree.ListNode
import it.unibo.tuprolog.parser.tree.NumberNode
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.StructureNode
import it.unibo.tuprolog.parser.tree.SyntaxNodeVisitor
import it.unibo.tuprolog.parser.tree.TheoryNode
import it.unibo.tuprolog.parser.tree.VariableNode
import kotlin.test.assertEquals

/** Renders a tree as nested node kinds, relying on the visitor's default dispatch for everything else. */
private object KindsVisitor : SyntaxNodeVisitor<String> {
    override fun visitTheory(node: TheoryNode) = node.clauses.joinToString(" ") { visit(it) }

    override fun visitOperator(node: OperatorExpressionNode) =
        "op(${listOfNotNull(node.leftOperand, node.rightOperand).joinToString { visit(it) }})"

    override fun visitNumber(node: NumberNode) = "number"

    override fun visitVariable(node: VariableNode) = "var"

    override fun visitStructure(node: StructureNode) =
        if (node.arguments.isEmpty()) "atom" else "struct(${node.arguments.joinToString { visit(it) }})"

    override fun visitList(node: ListNode) = "list(${node.items.joinToString { visit(it) }})"

    override fun visitBlock(node: BlockNode) = "block(${node.items.joinToString { visit(it) }})"
}

class SyntaxNodeVisitorSpec :
    FunSpec({
        test("default-dispatch/reaches-every-node-kind") {
            val operators = OperatorTables.of(op("+", Associativity.YFX, 500))
            val theory = testParser.parseTheory(lex("f([a], {b}, (c), 1, X). a + b."), operators).root
            assertEquals(
                "struct(list(atom), block(atom), atom, number, var) op(atom, atom)",
                KindsVisitor.visit(theory),
            )
        }
    })
