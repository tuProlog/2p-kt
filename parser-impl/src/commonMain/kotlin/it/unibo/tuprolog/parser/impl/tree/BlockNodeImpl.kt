package it.unibo.tuprolog.parser.impl.tree

import it.unibo.tuprolog.parser.tree.BlockNode
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.SyntaxKind
import it.unibo.tuprolog.parser.tree.SyntaxNode
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange

internal data class BlockNodeImpl(
    override val span: SourceSpan,
    override val tokenRange: TokenRange,
    override val items: List<ExpressionNode>,
) : BlockNode {
    override val kind: SyntaxKind = SyntaxKind.BLOCK
    override val priority: Int = 0
    override val children: List<SyntaxNode> = items
}
