package it.unibo.tuprolog.parser.tree

import it.unibo.tuprolog.parser.Representable
import it.unibo.tuprolog.parser.sources.MaterializedLexedSource
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.sources.TokenStore

class SyntaxTree<out T : SyntaxNode> internal constructor(
    val lexedSource: MaterializedLexedSource,
    val root: T,
    val semanticTokens: List<SemanticToken>,
) : Representable {
    private val semanticByToken: Map<Int, SemanticToken> by lazy {
        semanticTokens.associateBy(SemanticToken::tokenId)
    }

    val source: SourceText
        get() = lexedSource.source

    val tokens: TokenStore
        get() = lexedSource.tokens

    fun semanticToken(tokenId: Int): SemanticToken? = semanticByToken[tokenId]

    override fun toRepresentation(): String = tokens.toList().joinToString { it.toRepresentation() }
}
