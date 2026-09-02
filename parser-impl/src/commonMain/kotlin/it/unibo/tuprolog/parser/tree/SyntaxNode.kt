package it.unibo.tuprolog.parser.tree

import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.TokenRange

/**
 * Base element of the immutable concrete syntax tree.
 *
 * Nodes retain syntactic distinctions such as parentheses and operator use. [span] and
 * [tokenRange] are end-exclusive and contain every child range, but exclude surrounding trivia.
 */
interface SyntaxNode {
    /** Stable structural category of this node. */
    val kind: SyntaxKind

    /** End-exclusive range of source characters belonging to this node. */
    val span: SourceSpan

    /** End-exclusive range of absolute token IDs belonging to this node. */
    val tokenRange: TokenRange

    /** Immediate syntax children in source order. */
    val children: List<SyntaxNode>

    /** Dispatches this node to [visitor]. */
    fun <T> accept(visitor: SyntaxNodeVisitor<T>): T = visitor.visit(this)
}
