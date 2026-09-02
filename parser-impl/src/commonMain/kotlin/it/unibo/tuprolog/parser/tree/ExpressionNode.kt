package it.unibo.tuprolog.parser.tree

/** A Prolog term or operator expression that may occur as an operand. */
interface ExpressionNode : SyntaxNode {
    /** Root Prolog priority. Atomic and parenthesized terms have priority zero. */
    val priority: Int

    /** Dispatches this expression to [SyntaxNodeVisitor.visitExpression]. */
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitExpression(this)
}
