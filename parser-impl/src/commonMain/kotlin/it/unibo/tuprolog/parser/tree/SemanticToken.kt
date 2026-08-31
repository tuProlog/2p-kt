package it.unibo.tuprolog.parser.tree

data class SemanticToken(
    val tokenId: Int,
    val role: SemanticRole,
    val relatedNodeKind: SyntaxKind,
)
