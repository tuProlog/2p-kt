package it.unibo.tuprolog.parser.sources

import it.unibo.tuprolog.parser.tokens.Token

class LexedSource internal constructor(
    val source: SourceText,
    val tokens: List<Token>,
    internal val significantTokenIndices: IntArray,
) {
    fun token(id: Int): Token = tokens[id]

    fun textOf(token: Token): String = source.text.substring(token.span.start.offset, token.span.endExclusive.offset)

    fun textOf(tokenId: Int): String = textOf(tokens[tokenId])

    /** Returns a fresh list to keep the compact internal index representation private. */
    fun significantTokens(): List<Token> = significantTokenIndices.map(tokens::get)
}
