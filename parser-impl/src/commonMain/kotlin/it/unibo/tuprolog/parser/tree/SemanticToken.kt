package it.unibo.tuprolog.parser.tree

/**
 * Parser-derived semantic annotation for one significant token.
 *
 * It can drive syntax highlighting without discarding the lossless lexical token stream.
 *
 * @property tokenId absolute annotated token ID
 * @property role context-sensitive role at this occurrence
 * @property relatedNodeKind syntax-node category responsible for the annotation
 */
data class SemanticToken(
    val tokenId: Int,
    val role: SemanticRole,
    val relatedNodeKind: SyntaxKind,
)
