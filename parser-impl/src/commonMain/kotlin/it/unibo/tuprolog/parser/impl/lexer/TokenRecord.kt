package it.unibo.tuprolog.parser.impl.lexer

import it.unibo.tuprolog.parser.tokens.Token

internal data class TokenRecord(
    val token: Token,
    val rawText: String,
    val significantIndex: Int?,
)
