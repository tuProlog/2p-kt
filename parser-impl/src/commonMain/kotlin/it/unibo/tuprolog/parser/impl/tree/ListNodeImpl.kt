package it.unibo.tuprolog.parser.impl.tree

import it.unibo.tuprolog.parser.ExpressionNode
import it.unibo.tuprolog.parser.ListNode
import it.unibo.tuprolog.parser.SyntaxKind
import it.unibo.tuprolog.parser.SyntaxNode
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange

internal data class ListNodeImpl(
    override val span: SourceSpan,
    override val tokenRange: TokenRange,
    override val items: List<ExpressionNode>,
    override val tail: ExpressionNode?,
) : ListNode {
    override val kind: SyntaxKind = SyntaxKind.LIST
    override val priority: Int = 0
    override val children: List<SyntaxNode> =
        if (tail == null) items else items + tail
}
