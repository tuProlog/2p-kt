package it.unibo.tuprolog.parser.impl.parser

import it.unibo.tuprolog.parser.tokens.TokenKind

internal enum class DelimiterPolicy(
    private val commaDisabled: Boolean,
    private val pipeDisabled: Boolean,
) {
    ALLOW_ALL(false, false),
    DISABLE_COMMA(true, false),
    DISABLE_COMMA_AND_PIPE(true, true),
    ;

    fun disables(kind: TokenKind): Boolean =
        (commaDisabled && kind == TokenKind.COMMA) ||
            (pipeDisabled && kind == TokenKind.PIPE)
}
