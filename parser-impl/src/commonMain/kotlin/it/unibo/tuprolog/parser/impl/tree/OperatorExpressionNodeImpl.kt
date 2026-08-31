package it.unibo.tuprolog.parser.impl.tree

import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.OperatorExpressionNode
import it.unibo.tuprolog.parser.tree.OperatorUse
import it.unibo.tuprolog.parser.tree.SyntaxKind
import it.unibo.tuprolog.parser.tree.SyntaxNode

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
