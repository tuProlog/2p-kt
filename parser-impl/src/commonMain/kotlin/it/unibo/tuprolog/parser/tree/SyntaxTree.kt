package it.unibo.tuprolog.parser.tree

import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tokens.Token

class SyntaxTree<out T : SyntaxNode> internal constructor(
    val lexedSource: LexedSource,
    val root: T,
    val semanticTokens: List<SemanticToken>,
) {
    private val semanticByToken: Map<Int, SemanticToken> by lazy {
        semanticTokens.associateBy(SemanticToken::tokenId)
    }

    val source: SourceText
        get() = lexedSource.source

    val tokens: List<Token>
        get() = lexedSource.tokens

    fun semanticToken(tokenId: Int): SemanticToken? = semanticByToken[tokenId]
}
