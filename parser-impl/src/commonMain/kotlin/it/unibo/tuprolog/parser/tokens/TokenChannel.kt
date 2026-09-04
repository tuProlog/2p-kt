package it.unibo.tuprolog.parser.tokens

/** Distinguishes grammar-significant tokens from losslessly retained whitespace and comments. */
enum class TokenChannel {
    SIGNIFICANT,
    TRIVIA,
}
