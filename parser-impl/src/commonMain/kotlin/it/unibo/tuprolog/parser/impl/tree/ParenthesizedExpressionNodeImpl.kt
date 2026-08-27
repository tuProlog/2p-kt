package it.unibo.tuprolog.parser.impl.tree

import it.unibo.tuprolog.parser.ExpressionNode
import it.unibo.tuprolog.parser.ParenthesizedExpressionNode
import it.unibo.tuprolog.parser.SyntaxKind
import it.unibo.tuprolog.parser.SyntaxNode
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange

internal data class ParenthesizedExpressionNodeImpl(
    override val span: SourceSpan,
    override val tokenRange: TokenRange,
    override val expression: ExpressionNode,
) : ParenthesizedExpressionNode {
    override val kind: SyntaxKind = SyntaxKind.PARENTHESIZED_EXPRESSION
    override val priority: Int = 0
    override val children: List<SyntaxNode> = listOf(expression)
}
