package it.unibo.tuprolog.parser.impl.tree

import it.unibo.tuprolog.parser.ExpressionNode
import it.unibo.tuprolog.parser.OperatorExpressionNode
import it.unibo.tuprolog.parser.OperatorUse
import it.unibo.tuprolog.parser.SyntaxKind
import it.unibo.tuprolog.parser.SyntaxNode
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange

internal data class OperatorExpressionNodeImpl(
    override val kind: SyntaxKind,
    override val span: SourceSpan,
    override val tokenRange: TokenRange,
    override val priority: Int,
    override val operator: OperatorUse,
    override val leftOperand: ExpressionNode?,
    override val rightOperand: ExpressionNode?,
) : OperatorExpressionNode {
    override val children: List<SyntaxNode> =
        buildList {
            leftOperand?.let(::add)
            rightOperand?.let(::add)
        }
}
