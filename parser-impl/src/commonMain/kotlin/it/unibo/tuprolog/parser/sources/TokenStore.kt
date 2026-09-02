package it.unibo.tuprolog.parser.sources

import it.unibo.tuprolog.parser.tokens.Token

/**
 * An ID-addressable token view.
 *
 * Unlike [List], the first retained token need not have ID zero. Iteration is in lexical order,
 * while [get] always uses the absolute [Token.id].
 */
interface TokenStore : Iterable<Token> {
    /** Absolute ID of the first retained token. */
    val firstTokenId: Int

    /** Absolute ID of the last token, forcing production through EOF for lazy stores. */
    val lastTokenId: Int

    /** Number of tokens currently retained without forcing additional lexing. */
    val retainedCount: Int

    /**
     * Returns the token with absolute [tokenId], forcing forward production when supported.
     *
     * @throws IndexOutOfBoundsException if the ID was released or lies beyond EOF
     * @throws it.unibo.tuprolog.parser.exceptions.PrologLexingException if token production fails
     */
    operator fun get(tokenId: Int): Token

    /** Returns retained tokens in lexical order, forcing production through EOF for lazy stores. */
    fun toList(): List<Token> = iterator().asSequence().toList()
}
