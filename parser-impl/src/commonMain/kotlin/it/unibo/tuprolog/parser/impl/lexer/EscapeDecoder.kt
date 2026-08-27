package it.unibo.tuprolog.parser.impl.lexer

import it.unibo.tuprolog.parser.exceptions.InvalidEscapeException
import it.unibo.tuprolog.parser.sources.SourceText

internal object EscapeDecoder {
    fun decode(
        source: SourceText,
        backslashOffset: Int,
    ): DecodedEscape {
        val text = source.text
        val markerOffset = backslashOffset + 1
        val marker =
            text.getOrNull(markerOffset)
                ?: throw invalid(source, backslashOffset, text.length, "escape reaches end of input")

        return when (marker) {
            'a' -> DecodedEscape("\u0007", markerOffset + 1)
            'b' -> DecodedEscape("\b", markerOffset + 1)
            'f' -> DecodedEscape("\u000c", markerOffset + 1)
            'n' -> DecodedEscape("\n", markerOffset + 1)
            'r' -> DecodedEscape("\r", markerOffset + 1)
            't' -> DecodedEscape("\t", markerOffset + 1)
            'v' -> DecodedEscape("\u000b", markerOffset + 1)
            '\\' -> DecodedEscape("\\", markerOffset + 1)
            '\'' -> DecodedEscape("'", markerOffset + 1)
            '"' -> DecodedEscape("\"", markerOffset + 1)
            '`' -> DecodedEscape("`", markerOffset + 1)
            '\n' -> DecodedEscape("", markerOffset + 1)
            '\r' -> {
                if (text.getOrNull(markerOffset + 1) == '\n') {
                    DecodedEscape("", markerOffset + 2)
                } else {
                    throw invalid(
                        source,
                        backslashOffset,
                        markerOffset + 1,
                        "a carriage-return continuation must be followed by a line feed",
                    )
                }
            }
            'x', 'X' -> decodeNumeric(source, backslashOffset, markerOffset + 1, 16)
            in '0'..'7' -> decodeNumeric(source, backslashOffset, markerOffset, 8)
            else -> throw invalid(
                source,
                backslashOffset,
                (markerOffset + 1).coerceAtMost(text.length),
                "unsupported escape marker '$marker'",
            )
        }
    }

    private fun decodeNumeric(
        source: SourceText,
        backslashOffset: Int,
        digitsStart: Int,
        radix: Int,
    ): DecodedEscape {
        val text = source.text
        var index = digitsStart
        while (index < text.length && text[index].digitToIntOrNull(radix) != null) {
            index += 1
        }
        if (index == digitsStart) {
            throw invalid(source, backslashOffset, index.coerceAtMost(text.length), "missing base-$radix digits")
        }
        if (text.getOrNull(index) != '\\') {
            throw invalid(
                source,
                backslashOffset,
                index.coerceAtMost(text.length),
                "numeric escapes must end with a backslash",
            )
        }

        var value = 0
        for (digitOffset in digitsStart until index) {
            val digit = text[digitOffset].digitToInt(radix)
            if (value > (0xffff - digit) / radix) {
                throw invalid(source, backslashOffset, index + 1, "escaped code unit exceeds U+FFFF")
            }
            value = value * radix + digit
        }
        return DecodedEscape(value.toChar().toString(), index + 1)
    }

    private fun invalid(
        source: SourceText,
        start: Int,
        endExclusive: Int,
        detail: String,
    ): InvalidEscapeException {
        val safeEnd = endExclusive.coerceAtLeast(start + 1).coerceAtMost(source.text.length)
        val span = source.span(start, safeEnd)
        return InvalidEscapeException(
            source,
            span,
            source.text.substring(start, safeEnd),
            detail,
        )
    }
}
