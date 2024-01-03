package it.unibo.tuprolog.core.parsing

import org.antlr.v4.runtime.Token

fun parseException(
    input: Any?,
    token: Token,
    message: String?,
    throwable: Throwable?,
) = ParseException(input, token.text, token.line, token.charPositionInLine, message, throwable)

fun parseException(
    token: Token,
    message: String?,
    throwable: Throwable?,
) = ParseException(null, token.text, token.line, token.charPositionInLine, message, throwable)

fun parseException(
    token: Token,
    message: String?,
) = ParseException(null, token.text, token.line, token.charPositionInLine, message, null)

fun parseException(
    token: Token,
    throwable: Throwable?,
) = ParseException(null, token.text, token.line, token.charPositionInLine, "", throwable)
