package it.unibo.tuprolog.parser.tree

interface OperatorExpressionNode : ExpressionNode {
    val operator: OperatorUse
    val leftOperand: ExpressionNode?
    val rightOperand: ExpressionNode?
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T =
        visitor.visitOperator(this)
}