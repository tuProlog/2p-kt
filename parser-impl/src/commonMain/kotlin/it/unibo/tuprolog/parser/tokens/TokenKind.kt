package it.unibo.tuprolog.parser.tokens

/**
 * Lexical category assigned without consulting an operator table.
 *
 * [constant] is the normalized representation of punctuation and fixed-spelling tokens. Kinds
 * whose spelling or decoded value varies carry a [TokenPayload] instead.
 *
 * @property constant fixed normalized spelling, or `null` for payload-bearing kinds
 */
enum class TokenKind(
    val constant: String? = null,
) {
    WORD_ATOM,
    GRAPHIC_ATOM,
    VARIABLE,

    DECIMAL_INTEGER,
    HEX_INTEGER,
    OCTAL_INTEGER,
    BINARY_INTEGER,
    FLOAT,
    CHARACTER_CODE,

    SINGLE_QUOTED_ATOM,
    DOUBLE_QUOTED_TEXT,

    LEFT_PARENTHESIS("("),
    RIGHT_PARENTHESIS(")"),
    LEFT_BRACKET("{"),
    RIGHT_BRACKET("}"),
    LEFT_BRACE("["),
    RIGHT_BRACE("]"),

    COMMA(","),
    PIPE("|"),
    CUT("!"),
    SIGN,
    FULL_STOP("."),

    WHITESPACE(" "),
    LINE_COMMENT,
    BLOCK_COMMENT,

    END_OF_INPUT(""),
}
