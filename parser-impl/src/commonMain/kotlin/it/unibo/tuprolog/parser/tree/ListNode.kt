package it.unibo.tuprolog.parser.tree

/** Non-empty list syntax. Empty lists are represented as an empty-list [StructureNode]. */
interface ListNode : TermNode {
    val items: List<ExpressionNode>
    val tail: ExpressionNode?

    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitList(this)
}
