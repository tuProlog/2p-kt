package it.unibo.tuprolog.parser.tree

interface ParenthesizedExpressionNode : TermNode {
    val expression: ExpressionNode
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T =
        visitor.visitParenthesizedExpression(this)
}