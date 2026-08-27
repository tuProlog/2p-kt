package it.unibo.tuprolog.parser.impl.tree

import it.unibo.tuprolog.parser.tree.ExpressionNode
import it.unibo.tuprolog.parser.tree.StructureKind
import it.unibo.tuprolog.parser.tree.StructureNode
import it.unibo.tuprolog.parser.tree.SyntaxKind
import it.unibo.tuprolog.parser.tree.SyntaxNode
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange

internal data class StructureNodeImpl(
    override val span: SourceSpan,
    override val tokenRange: TokenRange,
    override val structureKind: StructureKind,
    override val functor: String,
    override val functorTokenId: Int?,
    override val arguments: List<ExpressionNode>,
) : StructureNode {
    override val kind: SyntaxKind = SyntaxKind.STRUCTURE
    override val priority: Int = 0
    override val children: List<SyntaxNode> = arguments
}
