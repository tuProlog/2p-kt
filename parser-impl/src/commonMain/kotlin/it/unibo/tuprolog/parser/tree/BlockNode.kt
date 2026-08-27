package it.unibo.tuprolog.parser.tree

/** Non-empty brace syntax. Empty braces are represented as an empty-block [StructureNode]. */
interface BlockNode : TermNode {
    val items: List<ExpressionNode>
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T =
        visitor.visitBlock(this)
}