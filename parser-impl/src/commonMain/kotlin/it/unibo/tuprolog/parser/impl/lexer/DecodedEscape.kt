package it.unibo.tuprolog.parser.impl.lexer

internal data class DecodedEscape(
    val value: String,
    val endExclusiveOffset: Int,
)
