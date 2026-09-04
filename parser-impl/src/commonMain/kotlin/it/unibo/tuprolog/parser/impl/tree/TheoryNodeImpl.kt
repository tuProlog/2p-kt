package it.unibo.tuprolog.parser.impl.tree

import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange
import it.unibo.tuprolog.parser.tree.ClauseNode
import it.unibo.tuprolog.parser.tree.SyntaxKind
import it.unibo.tuprolog.parser.tree.SyntaxNode
import it.unibo.tuprolog.parser.tree.TheoryNode

internal data class TheoryNodeImpl(
    override val span: SourceSpan,
    override val tokenRange: TokenRange,
    override val clauses: List<ClauseNode>,
) : TheoryNode {
    override val kind: SyntaxKind = SyntaxKind.THEORY
    override val children: List<SyntaxNode> = clauses
}
