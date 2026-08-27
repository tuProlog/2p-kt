package it.unibo.tuprolog.parser.impl.parser

import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenKind

internal class TokenCursor(
    private val input: LexedSource,
    startSignificantIndex: Int = 0,
) {
    private var significantIndex: Int = startSignificantIndex

    val isAtEnd: Boolean
        get() = peek().kind == TokenKind.END_OF_INPUT

    val currentPosition: SourcePosition
        get() = peek().span.start

    fun peek(relative: Int = 0): Token {
        val requested =
            (significantIndex + relative)
                .coerceAtMost(input.significantTokenIndices.lastIndex)
        return input.tokens[input.significantTokenIndices[requested]]
    }

    fun consume(): Token {
        val token = peek()
        if (token.kind != TokenKind.END_OF_INPUT) {
            significantIndex += 1
        }
        return token
    }

    fun mark(): Int = significantIndex

    fun restore(mark: Int) {
        require(mark in 0..input.significantTokenIndices.lastIndex)
        significantIndex = mark
    }
}
