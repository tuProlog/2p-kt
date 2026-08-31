package it.unibo.tuprolog.parser.impl.lexer

import it.unibo.tuprolog.parser.exceptions.InvalidEscapeException
import it.unibo.tuprolog.parser.exceptions.MalformedNumericLiteralException
import it.unibo.tuprolog.parser.exceptions.UnexpectedCharacterException
import it.unibo.tuprolog.parser.exceptions.UnterminatedBlockCommentException
import it.unibo.tuprolog.parser.exceptions.UnterminatedQuotedLiteralException
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.tokens.QuoteKind
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenChannel
import it.unibo.tuprolog.parser.tokens.TokenKind
import it.unibo.tuprolog.parser.tokens.TokenPayload

/** A retryable scanner over a growable character buffer. */
internal class IncrementalTokenScanner(
    sourceId: String?,
    startTokenId: Int = 0,
) {
    @Suppress("ObjectInheritsException")
    private object NeedMoreInput : Throwable()

    val source: BufferedSource = BufferedSource(sourceId)

    private var offset: Int = 0
    private var position: SourcePosition = SourcePosition(0, 0, 0)
    private var nextTokenId: Int = startTokenId
    private var eofToken: ScannedToken? = null

    val currentPosition: SourcePosition
        get() = position

    fun append(chunk: String) {
        source.append(chunk)
    }

    fun finish() {
        source.finish()
    }

    /** Returns `null` when more characters are required. */
    fun pollToken(): ScannedToken? {
        eofToken?.let { return null }
        if (offset == source.endOffset) {
            if (!source.isFinished) {
                return null
            }
            return ScannedToken(
                Token(
                    id = nextTokenId++,
                    kind = TokenKind.END_OF_INPUT,
                    channel = TokenChannel.SIGNIFICANT,
                    span = SourceSpan(position, position),
                ),
                "",
            ).also { eofToken = it }
        }

        return try {
            scanToken()
        } catch (_: NeedMoreInput) {
            null
        }
    }

    private fun scanToken(): ScannedToken {
        val current = requiredChar(offset)
        return when {
            current.isLayout() -> scanRegex(WHITESPACE, TokenKind.WHITESPACE, TokenChannel.TRIVIA)
            source.startsWith("/*", offset) -> scanBlockComment()
            current == '%' -> scanLineComment()
            current == '\'' -> scanQuoted('\'', TokenKind.SINGLE_QUOTED_ATOM, QuoteKind.SINGLE)
            current == '"' -> scanQuoted('"', TokenKind.DOUBLE_QUOTED_TEXT, QuoteKind.DOUBLE)
            current == '0' && charAt(offset + 1) == '\'' -> scanCharacterCode()
            current in '0'..'9' -> scanNumber()
            current == '_' || current in 'A'..'Z' ->
                scanRegex(VARIABLE, TokenKind.VARIABLE) { TokenPayload.Name(it) }
            current in 'a'..'z' ->
                scanRegex(WORD_ATOM, TokenKind.WORD_ATOM) { TokenPayload.Name(it) }
            current == '(' -> scanSingleCharacter(TokenKind.LEFT_PARENTHESIS)
            current == ')' -> scanSingleCharacter(TokenKind.RIGHT_PARENTHESIS)
            current == '[' -> scanSingleCharacter(TokenKind.LEFT_BRACKET)
            current == ']' -> scanSingleCharacter(TokenKind.RIGHT_BRACKET)
            current == '{' -> scanSingleCharacter(TokenKind.LEFT_BRACE)
            current == '}' -> scanSingleCharacter(TokenKind.RIGHT_BRACE)
            current == ',' -> scanSingleCharacter(TokenKind.COMMA)
            current == '|' -> scanSingleCharacter(TokenKind.PIPE)
            current == '!' -> scanSingleCharacter(TokenKind.CUT)
            current.isGraphicCharacter() -> scanGraphic()
            else -> {
                val end = offset + 1
                throw UnexpectedCharacterException(
                    source,
                    source.span(offset, end),
                    source.text(offset, end),
                )
            }
        }
    }

    private fun scanRegex(
        regex: Regex,
        kind: TokenKind,
        channel: TokenChannel = TokenChannel.SIGNIFICANT,
        payload: ((String) -> TokenPayload?)? = null,
    ): ScannedToken {
        val localOffset = offset - source.baseOffset
        val match =
            regex.matchAt(source.content, localOffset)
                ?: error("Regex $regex did not match at a dispatched position")
        val end = source.baseOffset + match.range.last + 1
        if (end == source.endOffset && !source.isFinished) {
            throw NeedMoreInput
        }
        return complete(kind, channel, end, payload?.invoke(match.value))
    }

    private fun scanNumber(): ScannedToken {
        if (source.startsWith("0x", offset) || source.startsWith("0X", offset)) {
            return scanPrefixedInteger(HEX_INTEGER, TokenKind.HEX_INTEGER, 16, "hexadecimal")
        }
        if (source.startsWith("0o", offset) || source.startsWith("0O", offset)) {
            return scanPrefixedInteger(OCTAL_INTEGER, TokenKind.OCTAL_INTEGER, 8, "octal")
        }
        if (source.startsWith("0b", offset) || source.startsWith("0B", offset)) {
            return scanPrefixedInteger(BINARY_INTEGER, TokenKind.BINARY_INTEGER, 2, "binary")
        }

        val localOffset = offset - source.baseOffset
        val float = FLOAT.matchAt(source.content, localOffset)
        if (float != null) {
            val end = source.baseOffset + float.range.last + 1
            if (end == source.endOffset && !source.isFinished) {
                throw NeedMoreInput
            }
            if (isPartialExponent(end)) {
                throw NeedMoreInput
            }
            return complete(TokenKind.FLOAT, TokenChannel.SIGNIFICANT, end, null)
        }

        val integer = DECIMAL_INTEGER.matchAt(source.content, localOffset)!!
        val end = source.baseOffset + integer.range.last + 1
        if (end == source.endOffset && !source.isFinished) {
            throw NeedMoreInput
        }
        if (charAt(end) == '.' && charAt(end + 1) == null && !source.isFinished) {
            throw NeedMoreInput
        }
        return complete(
            TokenKind.DECIMAL_INTEGER,
            TokenChannel.SIGNIFICANT,
            end,
            TokenPayload.IntegerDigits(10, integer.value),
        )
    }

    private fun isPartialExponent(floatEnd: Int): Boolean {
        val marker = charAt(floatEnd) ?: return false
        if (marker != 'e' && marker != 'E') {
            return false
        }
        var index = floatEnd + 1
        val sign = charAt(index)
        if (sign == '+' || sign == '-') {
            index += 1
        }
        return charAt(index) == null && !source.isFinished
    }

    private fun scanPrefixedInteger(
        regex: Regex,
        kind: TokenKind,
        radix: Int,
        label: String,
    ): ScannedToken {
        val localOffset = offset - source.baseOffset
        val match = regex.matchAt(source.content, localOffset)
        if (match == null) {
            if (source.endOffset <= offset + 2 && !source.isFinished) {
                throw NeedMoreInput
            }
            val end = (offset + 2).coerceAtMost(source.endOffset)
            throw MalformedNumericLiteralException(
                source,
                source.span(offset, end),
                source.text(offset, end),
                "$label prefix is not followed by a valid digit",
            )
        }
        val end = source.baseOffset + match.range.last + 1
        if (end == source.endOffset && !source.isFinished) {
            throw NeedMoreInput
        }
        return complete(
            kind,
            TokenChannel.SIGNIFICANT,
            end,
            TokenPayload.IntegerDigits(radix, match.value.substring(2)),
        )
    }

    private fun scanGraphic(): ScannedToken {
        if (requiredChar(offset) == '.' && isFullStopTerminator(offset + 1)) {
            return complete(TokenKind.FULL_STOP, TokenChannel.SIGNIFICANT, offset + 1, null)
        }

        var end = offset
        while (true) {
            val current = charAt(end)
            if (current == null) {
                if (!source.isFinished) {
                    throw NeedMoreInput
                }
                break
            }
            if (!current.isGraphicCharacter()) {
                break
            }
            end += 1
        }
        val raw = source.text(offset, end)
        val kind =
            if (raw.length == 1 && (raw == "+" || raw == "-")) TokenKind.SIGN else TokenKind.GRAPHIC_ATOM
        return complete(
            kind,
            TokenChannel.SIGNIFICANT,
            end,
            if (kind == TokenKind.GRAPHIC_ATOM) TokenPayload.Name(raw) else null,
        )
    }

    private fun scanSingleCharacter(kind: TokenKind): ScannedToken =
        complete(kind, TokenChannel.SIGNIFICANT, offset + 1, null)

    private fun scanLineComment(): ScannedToken {
        var end = offset + 1
        while (true) {
            val current = charAt(end)
            if (current == null) {
                if (!source.isFinished) {
                    throw NeedMoreInput
                }
                break
            }
            if (current == '\r' || current == '\n') {
                break
            }
            end += 1
        }
        return complete(TokenKind.LINE_COMMENT, TokenChannel.TRIVIA, end, null)
    }

    private fun scanBlockComment(): ScannedToken {
        var index = offset + 2
        while (true) {
            val current = charAt(index)
            if (current == null) {
                if (!source.isFinished) {
                    throw NeedMoreInput
                }
                throw UnterminatedBlockCommentException(source, source.span(offset, source.endOffset))
            }
            if (current == '*' && charAt(index + 1) == '/') {
                return complete(TokenKind.BLOCK_COMMENT, TokenChannel.TRIVIA, index + 2, null)
            }
            if (current == '*' && index + 1 == source.endOffset && !source.isFinished) {
                throw NeedMoreInput
            }
            index += 1
        }
    }

    private fun scanQuoted(
        quote: Char,
        kind: TokenKind,
        quoteKind: QuoteKind,
    ): ScannedToken {
        var index = offset + 1
        val decoded = StringBuilder()
        while (true) {
            val current = charAt(index)
            if (current == null) {
                if (!source.isFinished) {
                    throw NeedMoreInput
                }
                throw UnterminatedQuotedLiteralException(source, source.span(offset, source.endOffset), quote)
            }
            when {
                current == quote -> {
                    val next = charAt(index + 1)
                    if (next == null && !source.isFinished) {
                        throw NeedMoreInput
                    }
                    if (next == quote) {
                        decoded.append(quote)
                        index += 2
                    } else {
                        return complete(
                            kind,
                            TokenChannel.SIGNIFICANT,
                            index + 1,
                            TokenPayload.QuotedText(decoded.toString(), quoteKind),
                        )
                    }
                }
                current == '\\' -> {
                    val escape = decodeEscape(index)
                    decoded.append(escape.value)
                    index = escape.endExclusiveOffset
                }
                current == '\r' || current == '\n' ->
                    throw UnterminatedQuotedLiteralException(source, source.span(offset, index), quote)
                else -> {
                    decoded.append(current)
                    index += 1
                }
            }
        }
    }

    private fun scanCharacterCode(): ScannedToken {
        val contentOffset = offset + 2
        val current =
            charAt(contentOffset)
                ?: if (!source.isFinished) {
                    throw NeedMoreInput
                } else {
                    throw MalformedNumericLiteralException(
                        source,
                        source.span(offset, source.endOffset),
                        source.text(offset, source.endOffset),
                        "character-code literal has no character",
                    )
                }

        val value: String
        val end: Int
        if (current == '\\') {
            val escape = decodeEscape(contentOffset)
            value = escape.value
            end = escape.endExclusiveOffset
        } else {
            if (current == '\r' || current == '\n' || current == '\t' || current == '\u000c') {
                throw MalformedNumericLiteralException(
                    source,
                    source.span(offset, contentOffset + 1),
                    source.text(offset, contentOffset + 1),
                    "character-code literal contains forbidden layout",
                )
            }
            value = current.toString()
            end = contentOffset + 1
        }

        if (value.length != 1) {
            throw MalformedNumericLiteralException(
                source,
                source.span(offset, end),
                source.text(offset, end),
                "character-code escape must decode to exactly one UTF-16 code unit",
            )
        }
        return complete(
            TokenKind.CHARACTER_CODE,
            TokenChannel.SIGNIFICANT,
            end,
            TokenPayload.CharacterCode(value[0].code),
        )
    }

    private fun decodeEscape(backslashOffset: Int): DecodedEscape {
        val markerOffset = backslashOffset + 1
        val marker =
            charAt(markerOffset)
                ?: if (!source.isFinished) {
                    throw NeedMoreInput
                } else {
                    throw invalidEscape(backslashOffset, source.endOffset, "escape reaches end of input")
                }

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
                val following = charAt(markerOffset + 1)
                if (following == null && !source.isFinished) {
                    throw NeedMoreInput
                }
                if (following == '\n') {
                    DecodedEscape("", markerOffset + 2)
                } else {
                    throw invalidEscape(
                        backslashOffset,
                        markerOffset + 1,
                        "a carriage-return continuation must be followed by a line feed",
                    )
                }
            }
            'x', 'X' -> decodeNumericEscape(backslashOffset, markerOffset + 1, 16)
            in '0'..'7' -> decodeNumericEscape(backslashOffset, markerOffset, 8)
            else ->
                throw invalidEscape(
                    backslashOffset,
                    markerOffset + 1,
                    "unsupported escape marker '$marker'",
                )
        }
    }

    private fun decodeNumericEscape(
        backslashOffset: Int,
        digitsStart: Int,
        radix: Int,
    ): DecodedEscape {
        var index = digitsStart
        while (true) {
            val current = charAt(index)
            if (current == null) {
                if (!source.isFinished) {
                    throw NeedMoreInput
                }
                break
            }
            if (current.digitToIntOrNull(radix) == null) {
                break
            }
            index += 1
        }
        if (index == digitsStart) {
            throw invalidEscape(backslashOffset, index, "missing base-$radix digits")
        }
        if (charAt(index) != '\\') {
            throw invalidEscape(backslashOffset, index, "numeric escapes must end with a backslash")
        }

        var value = 0
        for (digitOffset in digitsStart until index) {
            val digit = requiredChar(digitOffset).digitToInt(radix)
            if (value > (0xffff - digit) / radix) {
                throw invalidEscape(backslashOffset, index + 1, "escaped code unit exceeds U+FFFF")
            }
            value = value * radix + digit
        }
        return DecodedEscape(value.toChar().toString(), index + 1)
    }

    private fun invalidEscape(
        start: Int,
        requestedEnd: Int,
        detail: String,
    ): InvalidEscapeException {
        val safeEnd = requestedEnd.coerceAtLeast(start + 1).coerceAtMost(source.endOffset)
        return InvalidEscapeException(
            source,
            source.span(start, safeEnd),
            source.text(start, safeEnd),
            detail,
        )
    }

    private fun isFullStopTerminator(afterPeriod: Int): Boolean {
        val next = charAt(afterPeriod)
        if (next == null) {
            if (!source.isFinished) {
                throw NeedMoreInput
            }
            return true
        }
        if (next.isLayout() || next == '%') {
            return true
        }
        if (next == '/') {
            val following = charAt(afterPeriod + 1)
            if (following == null && !source.isFinished) {
                throw NeedMoreInput
            }
            return following == '*'
        }
        return false
    }

    private fun complete(
        kind: TokenKind,
        channel: TokenChannel,
        endExclusiveOffset: Int,
        payload: TokenPayload?,
    ): ScannedToken {
        val start = position
        val raw = source.text(offset, endExclusiveOffset)
        position = position.advancePosition(raw)
        offset = endExclusiveOffset
        return ScannedToken(
            Token(
                id = nextTokenId++,
                kind = kind,
                channel = channel,
                span = SourceSpan(start, position),
                payload = payload,
            ),
            raw,
        )
    }

    private fun requiredChar(at: Int): Char = charAt(at) ?: throw NeedMoreInput

    private fun charAt(at: Int): Char? {
        source.charAtOrNull(at)?.let { return it }
        if (!source.isFinished && at >= source.endOffset) {
            throw NeedMoreInput
        }
        return null
    }

    private companion object {
        val WHITESPACE: Regex = Regex("[ \\t\\r\\n]+")
        val VARIABLE: Regex = Regex("[_A-Z][_A-Za-z0-9]*")
        val WORD_ATOM: Regex = Regex("[a-z][A-Za-z0-9_]*")
        val DECIMAL_INTEGER: Regex = Regex("[0-9]+")
        val HEX_INTEGER: Regex = Regex("0[xX][0-9A-Fa-f]+")
        val OCTAL_INTEGER: Regex = Regex("0[oO][0-7]+")
        val BINARY_INTEGER: Regex = Regex("0[bB][01]+")
        val FLOAT: Regex = Regex("[0-9]+\\.[0-9]+(?:[eE][+-]?[0-9]+)?")

        fun Char.isLayout(): Boolean = this == ' ' || this == '\t' || this == '\r' || this == '\n'

        fun Char.isGraphicCharacter(): Boolean =
            this == '+' ||
                this == '*' ||
                this == '/' ||
                this == '\\' ||
                this == '^' ||
                this == '<' ||
                this == '>' ||
                this == '=' ||
                this == '~' ||
                this == ':' ||
                this == '.' ||
                this == '?' ||
                this == '@' ||
                this == '#' ||
                this == '$' ||
                this == '&' ||
                this == '-' ||
                this == ';'
    }
}
