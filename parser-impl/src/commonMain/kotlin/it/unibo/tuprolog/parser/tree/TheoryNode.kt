package it.unibo.tuprolog.parser.tree

/**
 * Complete input parsed as an ordered, possibly empty collection of clauses.
 *
 * @property clauses clauses in source order
 */
interface TheoryNode : SyntaxNode {
    val clauses: List<ClauseNode>

    /** Dispatches this theory to [SyntaxNodeVisitor.visitTheory]. */
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitTheory(this)
}
