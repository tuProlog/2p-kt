package it.unibo.tuprolog.parser.sources

import it.unibo.tuprolog.parser.tokens.Token

/**
 * An ID-addressable token view.
 *
 * Unlike [List], the first retained token need not have ID zero. Iteration is in lexical order,
 * while [get] always uses the absolute [Token.id].
 */
interface TokenStore : Iterable<Token> {
    /**
     * Absolute ID of the first retained token.
     *
     * A lazy store that has not produced anything yet forces production of one token to answer
     * this, so it can fail exactly like [get].
     *
     * @throws it.unibo.tuprolog.parser.exceptions.PrologLexingException if token production fails
     */
    val firstTokenId: Int

    /** Absolute ID of the last token, forcing production through EOF for lazy stores. */
    val lastTokenId: Int

    /** Number of tokens currently retained without forcing additional lexing. */
    val retainedCount: Int

    /**
     * Returns the token with absolute [tokenId], forcing forward production when supported.
     *
     * A lazy store reports a released [tokenId] (below [firstTokenId]) as
     * [IllegalArgumentException] and a request beyond EOF as [IndexOutOfBoundsException]; a fully
     * materialized store reports every ID outside its retained range as
     * [IndexOutOfBoundsException].
     *
     * @throws IndexOutOfBoundsException if [tokenId] lies beyond EOF, or outside a materialized
     * store's retained range
     * @throws IllegalArgumentException if [tokenId] was already released by a lazy store
     * @throws it.unibo.tuprolog.parser.exceptions.PrologLexingException if token production fails
     */
    operator fun get(tokenId: Int): Token

    /** Returns retained tokens in lexical order, forcing production through EOF for lazy stores. */
    fun toList(): List<Token> = iterator().asSequence().toList()
}
