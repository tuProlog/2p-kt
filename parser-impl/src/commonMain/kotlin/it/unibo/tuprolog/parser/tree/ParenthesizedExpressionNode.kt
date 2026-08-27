package it.unibo.tuprolog.parser.tree

interface ParenthesizedExpressionNode : TermNode {
    val expression: ExpressionNode
}