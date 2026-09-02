package it.unibo.tuprolog.parser.tokens

import it.unibo.tuprolog.parser.Representable

/** Decoded data attached to a token whose spelling is not fixed by [TokenKind]. */
sealed interface TokenPayload : Representable {
    /**
     * Decoded atom, graphic atom, or variable name.
     *
     * @property value decoded name
     */
    data class Name(
        val value: String,
    ) : TokenPayload {
        /** Returns [value]. */
        override fun toRepresentation(): String = value
    }

    /**
     * Integer digits separated from their lexical radix prefix.
     *
     * @property radix numeric radix
     * @property digits digits without a radix prefix
     */
    data class IntegerDigits(
        val radix: Int,
        val digits: String,
    ) : TokenPayload {
        /**
         * Reconstructs a normalized Prolog integer spelling.
         *
         * @throws IllegalArgumentException if [radix] is not 2, 8, 10, or 16
         */
        override fun toRepresentation(): String =
            when (radix) {
                10 -> digits
                2 -> "0b$digits"
                8 -> "0$digits"
                16 -> "0x$digits"
                else -> throw IllegalArgumentException("Invalid integer digits $radix")
            }
    }

    /**
     * Decoded quoted content and the delimiter family that produced it.
     *
     * @property decoded content after quote and escape decoding
     * @property quoteKind original delimiter family
     */
    data class QuotedText(
        val decoded: String,
        val quoteKind: QuoteKind,
    ) : TokenPayload {
        /** Returns a normalized quoted representation of [decoded]. */
        override fun toRepresentation(): String =
            when (quoteKind) {
                QuoteKind.SINGLE -> "'$decoded'"
                QuoteKind.DOUBLE -> "\"$decoded\""
            }
    }

    /**
     * Decoded character-code literal.
     *
     * @property codePoint decoded UTF-16 code-unit value
     */
    data class CharacterCode(
        val codePoint: Int,
    ) : TokenPayload {
        /** Returns [codePoint] in decimal notation. */
        override fun toRepresentation(): String = codePoint.toString()
    }
}
