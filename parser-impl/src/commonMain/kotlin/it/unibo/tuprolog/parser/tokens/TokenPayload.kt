package it.unibo.tuprolog.parser.tokens

import it.unibo.tuprolog.parser.Representable

sealed interface TokenPayload : Representable {
    data class Name(
        val value: String,
    ) : TokenPayload {
        override fun toRepresentation(): String = value
    }

    data class IntegerDigits(
        val radix: Int,
        val digits: String,
    ) : TokenPayload {
        override fun toRepresentation(): String =
            when (radix) {
                10 -> digits
                2 -> "0b$digits"
                8 -> "0$digits"
                16 -> "0x$digits"
                else -> throw IllegalArgumentException("Invalid integer digits $radix")
            }
    }

    data class QuotedText(
        val decoded: String,
        val quoteKind: QuoteKind,
    ) : TokenPayload {
        override fun toRepresentation(): String =
            when (quoteKind) {
                QuoteKind.SINGLE -> "'$decoded'"
                QuoteKind.DOUBLE -> "\"$decoded\""
            }
    }

    data class CharacterCode(
        val codePoint: Int,
    ) : TokenPayload {
        override fun toRepresentation(): String = codePoint.toString()
    }
}
