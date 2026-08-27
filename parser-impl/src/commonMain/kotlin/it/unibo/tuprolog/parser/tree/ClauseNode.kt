package it.unibo.tuprolog.parser.tree

interface ClauseNode : SyntaxNode {
    val expression: ExpressionNode
    val terminatorTokenId: Int
}