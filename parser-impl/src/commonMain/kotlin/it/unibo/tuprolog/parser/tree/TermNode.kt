package it.unibo.tuprolog.parser.tree

interface TermNode : ExpressionNode {
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T =
        visitor.visitTerm(this)
}