package it.unibo.tuprolog.parser.sources

import it.unibo.tuprolog.parser.tokens.Token

/** A tokenized source whose token production may be lazy. */
interface LexedSource {
    /** Possibly partial source view backing currently retained tokens. */
    val source: Source

    /** ID-addressable token view; requesting tokens may force lazy lexing. */
    val tokens: TokenStore

    /** Absolute index of the first retained significant token. */
    val firstSignificantTokenIndex: Int

    /**
     * Returns the token with absolute [id], forcing lexing forward if necessary.
     *
     * @throws IndexOutOfBoundsException if [id] is unavailable or lies beyond EOF
     * @throws it.unibo.tuprolog.parser.exceptions.PrologLexingException if lexing fails first
     */
    fun token(id: Int): Token = tokens[id]

    /**
     * Returns the significant token at an absolute significant-token index.
     * Requests beyond the end of input return the unique end-of-input token.
     *
     * @throws IllegalArgumentException if [index] precedes the first retained significant token
     * @throws it.unibo.tuprolog.parser.exceptions.PrologLexingException if lexing fails
     */
    fun significantToken(index: Int): Token

    /** Returns the exact source spelling of [token], including original escapes and trivia. */
    fun textOf(token: Token): String

    /**
     * Returns the exact source spelling of the token with [tokenId].
     *
     * @throws IndexOutOfBoundsException if [tokenId] is not available
     */
    fun textOf(tokenId: Int): String = textOf(token(tokenId))

    /**
     * Forces lexing through EOF and returns retained significant tokens, including EOF.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologLexingException if lexing fails
     */
    fun significantTokens(): List<Token>

    /**
     * Forces lexing through EOF and returns an immutable, self-contained source snapshot.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologLexingException if lexing fails
     */
    fun materialize(): MaterializedLexedSource
}
