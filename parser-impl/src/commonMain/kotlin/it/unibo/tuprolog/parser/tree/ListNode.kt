package it.unibo.tuprolog.parser.tree

/**
 * Non-empty list syntax such as `[a, b | Tail]`.
 *
 * Empty lists are represented as an empty-list [StructureNode].
 *
 * @property items expressions before an optional tail delimiter
 * @property tail expression after `|`, or `null` for a proper list
 */
interface ListNode : TermNode {
    val items: List<ExpressionNode>
    val tail: ExpressionNode?

    /** Dispatches this list to [SyntaxNodeVisitor.visitList]. */
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitList(this)
}
