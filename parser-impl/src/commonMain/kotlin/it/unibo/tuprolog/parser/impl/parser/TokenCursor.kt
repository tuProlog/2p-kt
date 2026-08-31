package it.unibo.tuprolog.parser.impl.parser

import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenKind

internal class TokenCursor(
    private val input: LexedSource,
    startSignificantIndex: Int? = null,
) {
    private var significantIndex: Int? = startSignificantIndex

    val isAtEnd: Boolean
        get() = peek().kind == TokenKind.END_OF_INPUT

    val currentPosition: SourcePosition
        get() = peek().span.start

    fun peek(relative: Int = 0): Token {
        require(relative >= 0) { "Cannot look behind the token cursor" }
        return input.significantToken(currentIndex() + relative)
    }

    fun consume(): Token {
        val token = peek()
        if (token.kind != TokenKind.END_OF_INPUT) {
            significantIndex = currentIndex() + 1
        }
        return token
    }

    fun mark(): Int = currentIndex()

    fun restore(mark: Int) {
        require(mark >= input.firstSignificantTokenIndex)
        significantIndex = mark
    }

    private fun currentIndex(): Int =
        significantIndex ?: input.firstSignificantTokenIndex.also { significantIndex = it }
}
