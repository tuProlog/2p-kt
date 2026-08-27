package it.unibo.tuprolog.parser.tree

interface ClauseNode : SyntaxNode {
    val expression: ExpressionNode
    val terminatorTokenId: Int
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T =
        visitor.visitClause(this)
}