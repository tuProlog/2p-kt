package it.unibo.tuprolog.parser.impl.tree

import it.unibo.tuprolog.parser.tree.NumberKind
import it.unibo.tuprolog.parser.tree.NumberNode
import it.unibo.tuprolog.parser.tree.SyntaxKind
import it.unibo.tuprolog.parser.tree.SyntaxNode
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange

internal data class NumberNodeImpl(
    override val kind: SyntaxKind,
    override val span: SourceSpan,
    override val tokenRange: TokenRange,
    override val numberKind: NumberKind,
    override val signTokenId: Int?,
    override val valueTokenId: Int,
    override val sign: Int,
    override val radix: Int?,
    override val digits: String,
    override val characterCode: Int?,
) : NumberNode {
    override val priority: Int = 0
    override val children: List<SyntaxNode> = emptyList()
}
