package it.unibo.tuprolog.parser.tree

/** An atomic or structurally delimited expression, excluding implicit operator applications. */
interface TermNode : ExpressionNode {
    /** Dispatches this term to [SyntaxNodeVisitor.visitTerm]. */
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitTerm(this)
}
