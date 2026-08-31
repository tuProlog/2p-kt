package it.unibo.tuprolog.parser.sources

import it.unibo.tuprolog.parser.tokens.Token

/** A tokenized source whose token production may be lazy. */
interface LexedSource {
    val source: Source

    val tokens: TokenStore

    /** Absolute index of the first retained significant token. */
    val firstSignificantTokenIndex: Int

    fun token(id: Int): Token = tokens[id]

    /**
     * Returns the significant token at an absolute significant-token index.
     * Requests beyond the end of input return the unique end-of-input token.
     */
    fun significantToken(index: Int): Token

    fun textOf(token: Token): String

    fun textOf(tokenId: Int): String = textOf(token(tokenId))

    /** Forces lexing through EOF and returns the retained significant tokens. */
    fun significantTokens(): List<Token>

    /** Forces lexing through EOF and returns an immutable source snapshot. */
    fun materialize(): MaterializedLexedSource
}
