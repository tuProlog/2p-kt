package it.unibo.tuprolog.parser.tree

interface TheoryNode : SyntaxNode {
    val clauses: List<ClauseNode>
}