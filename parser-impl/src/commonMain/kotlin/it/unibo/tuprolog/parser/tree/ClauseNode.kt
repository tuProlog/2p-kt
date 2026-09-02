package it.unibo.tuprolog.parser.tree

/**
 * One full-stop-terminated Prolog expression in a theory or parse session.
 *
 * @property expression clause expression without the terminator
 * @property terminatorTokenId absolute ID of the terminating full-stop token
 */
interface ClauseNode : SyntaxNode {
    val expression: ExpressionNode
    val terminatorTokenId: Int

    /** Dispatches this clause to [SyntaxNodeVisitor.visitClause]. */
    override fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visitClause(this)
}
