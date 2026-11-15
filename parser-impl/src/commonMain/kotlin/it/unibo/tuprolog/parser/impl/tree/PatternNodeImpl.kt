package it.unibo.tuprolog.parser.impl.tree

import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange
import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.PatternNode
import it.unibo.tuprolog.parser.tree.SyntaxKind
import it.unibo.tuprolog.parser.tree.SyntaxNode
import it.unibo.tuprolog.parser.tree.VariableNode

internal data class PatternNodeImpl(
    override val span: SourceSpan,
    override val tokenRange: TokenRange,
    override val functor: VariableNode,
    override val arguments: List<ExpressionNode>,
) : PatternNode {
    override val kind: SyntaxKind = SyntaxKind.PATTERN
    override val priority: Int = 0
    override val children: List<SyntaxNode> = listOf(functor) + arguments
}
