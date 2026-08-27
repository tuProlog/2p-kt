package it.unibo.tuprolog.parser.tree

interface TheoryNode : SyntaxNode {
    val clauses: List<ClauseNode>
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T =
        visitor.visitTheory(this)
}