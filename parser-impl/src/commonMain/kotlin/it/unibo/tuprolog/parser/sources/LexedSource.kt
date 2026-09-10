package it.unibo.tuprolog.parser.sources

import it.unibo.tuprolog.parser.tokens.Token

/** A tokenized source whose token production may be lazy. */
interface LexedSource {
    /** Possibly partial source view backing currently retained tokens. */
    val source: Source

    /** ID-addressable token view; requesting tokens may force lazy lexing. */
    val tokens: TokenStore

    /**
     * Absolute index of the first retained significant token.
     *
     * A lazily lexed source that has not produced a significant token yet forces production of
     * one to answer this, so reading it can fail exactly like requesting a token.
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologLexingException if lexing fails
     */
    val firstSignificantTokenIndex: Int

    /**
     * Returns the token with absolute [id], forcing lexing forward if necessary.
     *
     * A lazy source reports a released [id] (already discarded under
     * [it.unibo.tuprolog.parser.TokenRetention.RELEASE_COMMITTED]) as [IllegalArgumentException]
     * and an [id] beyond EOF as [IndexOutOfBoundsException]; a materialized source reports every
     * [id] outside its retained range as [IndexOutOfBoundsException].
     *
     * @throws IndexOutOfBoundsException if [id] lies beyond EOF, or outside a materialized
     * source's retained range
     * @throws IllegalArgumentException if [id] was already released by a lazy source
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
     * @throws IndexOutOfBoundsException if [tokenId] lies beyond EOF, or outside a materialized
     * source's retained range
     * @throws IllegalArgumentException if [tokenId] was already released by a lazy source
     * @throws it.unibo.tuprolog.parser.exceptions.PrologLexingException if lexing fails
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
