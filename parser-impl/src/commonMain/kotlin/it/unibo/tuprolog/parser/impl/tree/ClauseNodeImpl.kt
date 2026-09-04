package it.unibo.tuprolog.parser.impl.tree

import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange
import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.SyntaxKind
import it.unibo.tuprolog.parser.tree.SyntaxNode

internal data class ClauseNodeImpl(
    override val span: SourceSpan,
    override val tokenRange: TokenRange,
    override val expression: ExpressionNode,
    override val terminatorTokenId: Int,
) : ClauseNode {
    override val kind: SyntaxKind = SyntaxKind.CLAUSE
    override val children: List<SyntaxNode> = listOf(expression)
}
