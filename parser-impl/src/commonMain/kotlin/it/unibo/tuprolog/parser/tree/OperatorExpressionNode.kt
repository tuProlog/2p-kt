package it.unibo.tuprolog.parser.tree

/**
 * Prefix, infix, or postfix operator application resolved against an operator table.
 *
 * @property operator selected definition and syntactic role
 * @property leftOperand left operand, absent for prefix use
 * @property rightOperand right operand, absent for postfix use
 */
interface OperatorExpressionNode : ExpressionNode {
    val operator: OperatorUse
    val leftOperand: ExpressionNode?
    val rightOperand: ExpressionNode?

    /** Dispatches this application to [SyntaxNodeVisitor.visitOperator]. */
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitOperator(this)
}
