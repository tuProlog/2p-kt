package it.unibo.tuprolog.core.parsing

import it.unibo.tuprolog.parser.Token

fun parseException(
    input: Any?,
    token: Token,
    message: String?,
    throwable: Throwable?
) = ParseException(input, token.text, token.line, token.column, message, throwable)

fun parseException(token: Token, message: String?, throwable: Throwable?) = ParseException(
    null,
    token.text,
    token.line,
    token.column,
    message,
    throwable
)

fun parseException(token: Token, message: String?) = ParseException(
    null,
    token.text,
    token.line,
    token.column,
    message,
    null
)

fun parseException(token: Token, throwable: Throwable?) = ParseException(
    null,
    token.text,
    token.line,
    token.column,
    "",
    throwable
)
