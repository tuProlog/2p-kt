package it.unibo.tuprolog.parser.tree

/**
 * Expression explicitly enclosed in parentheses.
 *
 * Parentheses restart parsing at the top-level priority and make the node atomic to its enclosing
 * expression.
 *
 * @property expression enclosed expression
 */
interface ParenthesizedExpressionNode : TermNode {
    val expression: ExpressionNode

    /** Dispatches this node to [SyntaxNodeVisitor.visitParenthesizedExpression]. */
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitParenthesizedExpression(this)
}
