package it.unibo.tuprolog.parser.tree

/**
 * Non-empty brace syntax such as `{a, b}`.
 *
 * Empty braces are represented as an empty-block [StructureNode].
 *
 * @property items expressions between the braces
 */
interface BlockNode : TermNode {
    val items: List<ExpressionNode>

    /** Dispatches this block to [SyntaxNodeVisitor.visitBlock]. */
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitBlock(this)
}
