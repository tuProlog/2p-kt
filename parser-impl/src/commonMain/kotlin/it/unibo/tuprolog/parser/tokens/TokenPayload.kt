package it.unibo.tuprolog.parser.tokens

sealed interface TokenPayload {
    data class Name(
        val value: String,
    ) : TokenPayload

    data class IntegerDigits(
        val radix: Int,
        val digits: String,
    ) : TokenPayload

    data class QuotedText(
        val decoded: String,
        val quoteKind: QuoteKind,
    ) : TokenPayload

    data class CharacterCode(
        val codePoint: Int,
    ) : TokenPayload
}
