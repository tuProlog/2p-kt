package it.unibo.tuprolog.parser.tree

interface ExpressionNode : SyntaxNode {
    /** Root Prolog priority. Atomic and parenthesized terms have priority zero. */
    val priority: Int
}