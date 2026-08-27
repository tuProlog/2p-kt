package it.unibo.tuprolog.parser.tree

import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange

interface SyntaxNode {
    val kind: SyntaxKind
    val span: SourceSpan
    val tokenRange: TokenRange
    val children: List<SyntaxNode>
}