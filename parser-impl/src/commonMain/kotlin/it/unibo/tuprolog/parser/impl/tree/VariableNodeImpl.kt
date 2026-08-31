package it.unibo.tuprolog.parser.impl.tree

import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange
import it.unibo.tuprolog.parser.tree.SyntaxKind
import it.unibo.tuprolog.parser.tree.SyntaxNode
import it.unibo.tuprolog.parser.tree.VariableNode

internal data class VariableNodeImpl(
    override val span: SourceSpan,
    override val tokenRange: TokenRange,
    override val tokenId: Int,
    override val name: String,
    override val isAnonymous: Boolean,
) : VariableNode {
    override val kind: SyntaxKind = SyntaxKind.VARIABLE
    override val priority: Int = 0
    override val children: List<SyntaxNode> = emptyList()
}
