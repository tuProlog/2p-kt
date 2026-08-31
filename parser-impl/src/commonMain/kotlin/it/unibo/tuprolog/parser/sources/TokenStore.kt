package it.unibo.tuprolog.parser.sources

import it.unibo.tuprolog.parser.tokens.Token

/**
 * An ID-addressable token view.
 *
 * Unlike [List], the first retained token need not have ID zero. Iteration is in lexical order,
 * while [get] always uses the absolute [Token.id].
 */
interface TokenStore : Iterable<Token> {
    val firstTokenId: Int

    val lastTokenId: Int

    /** Number of tokens currently retained without forcing additional lexing. */
    val retainedCount: Int

    operator fun get(tokenId: Int): Token

    fun toList(): List<Token> = iterator().asSequence().toList()
}
