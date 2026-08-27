package it.unibo.tuprolog.parser.impl.lexer

import it.unibo.tuprolog.parser.PrologLexer
import it.unibo.tuprolog.parser.exceptions.MalformedNumericLiteralException
import it.unibo.tuprolog.parser.exceptions.UnexpectedCharacterException
import it.unibo.tuprolog.parser.exceptions.UnterminatedBlockCommentException
import it.unibo.tuprolog.parser.exceptions.UnterminatedQuotedLiteralException
import it.unibo.tuprolog.parser.sources.LexedSource
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tokens.QuoteKind
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenChannel
import it.unibo.tuprolog.parser.tokens.TokenKind
import it.unibo.tuprolog.parser.tokens.TokenPayload

internal class RegexPrologLexer : PrologLexer {
    override fun lex(source: SourceText): LexedSource {
        val cursor = LexingCursor(source)
        val tokens = mutableListOf<Token>()

        while (!cursor.isAtEnd) {
            val token = scanToken(cursor).copy(id = tokens.size)
            check(token.span.length > 0) { "Lexer produced an empty non-EOF token" }
            tokens += token
        }

        val eofPosition = cursor.mark()
        tokens +=
            Token(
                id = tokens.size,
                kind = TokenKind.END_OF_INPUT,
                channel = TokenChannel.SIGNIFICANT,
                span =
                    SourceSpan(eofPosition, eofPosition),
            )

        val significant =
            tokens
                .asSequence()
                .filter { it.channel == TokenChannel.SIGNIFICANT }
                .map(Token::id)
                .toList()
                .toIntArray()

        return LexedSource(source, tokens.toList(), significant)
    }

    private fun scanToken(cursor: LexingCursor): Token {
        val current = cursor.current()
        return when {
            current.isLayout() -> scanRegex(cursor, WHITESPACE, TokenKind.WHITESPACE, TokenChannel.TRIVIA)
            cursor.startsWith("/*") -> scanBlockComment(cursor)
            current == '%' -> scanLineComment(cursor)
            current == '\'' -> scanQuoted(cursor, '\'', TokenKind.SINGLE_QUOTED_ATOM, QuoteKind.SINGLE)
            current == '"' -> scanQuoted(cursor, '"', TokenKind.DOUBLE_QUOTED_TEXT, QuoteKind.DOUBLE)
            current == '0' && cursor.charAtOrNull(cursor.offset + 1) == '\'' -> scanCharacterCode(cursor)
            current in '0'..'9' -> scanNumber(cursor)
            current == '_' || current in 'A'..'Z' -> scanVariable(cursor)
            current in 'a'..'z' -> scanWordAtom(cursor)
            current == '(' -> scanSingleCharacter(cursor, TokenKind.LEFT_PARENTHESIS)
            current == ')' -> scanSingleCharacter(cursor, TokenKind.RIGHT_PARENTHESIS)
            current == '[' -> scanSingleCharacter(cursor, TokenKind.LEFT_BRACKET)
            current == ']' -> scanSingleCharacter(cursor, TokenKind.RIGHT_BRACKET)
            current == '{' -> scanSingleCharacter(cursor, TokenKind.LEFT_BRACE)
            current == '}' -> scanSingleCharacter(cursor, TokenKind.RIGHT_BRACE)
            current == ',' -> scanSingleCharacter(cursor, TokenKind.COMMA)
            current == '|' -> scanSingleCharacter(cursor, TokenKind.PIPE)
            current == '!' -> scanSingleCharacter(cursor, TokenKind.CUT)
            current.isGraphicCharacter() -> scanGraphic(cursor)
            else -> {
                val end = (cursor.offset + 1).coerceAtMost(cursor.source.text.length)
                throw UnexpectedCharacterException(
                    cursor.source,
                    cursor.source.span(cursor.offset, end),
                    cursor.source.text.substring(cursor.offset, end),
                )
            }
        }
    }

    private fun scanRegex(
        cursor: LexingCursor,
        regex: Regex,
        kind: TokenKind,
        channel: TokenChannel = TokenChannel.SIGNIFICANT,
        payload: ((String) -> TokenPayload?)? = null,
    ): Token {
        val start = cursor.mark()
        val match =
            regex.matchAt(cursor.source.text, cursor.offset)
                ?: error("Regex $regex did not match at a dispatched position")
        cursor.advanceTo(match.range.last + 1)
        return Token(
            id = -1,
            kind = kind,
            channel = channel,
            span = cursor.spanFrom(start),
            payload = payload?.invoke(match.value),
        )
    }

    private fun scanVariable(cursor: LexingCursor): Token =
        scanRegex(cursor, VARIABLE, TokenKind.VARIABLE) { TokenPayload.Name(it) }

    private fun scanWordAtom(cursor: LexingCursor): Token =
        scanRegex(cursor, WORD_ATOM, TokenKind.WORD_ATOM) { TokenPayload.Name(it) }

    private fun scanNumber(cursor: LexingCursor): Token {
        val text = cursor.source.text
        val offset = cursor.offset

        if (text.startsWith("0x", offset) || text.startsWith("0X", offset)) {
            return scanPrefixedInteger(cursor, HEX_INTEGER, TokenKind.HEX_INTEGER, 16, "hexadecimal")
        }
        if (text.startsWith("0o", offset) || text.startsWith("0O", offset)) {
            return scanPrefixedInteger(cursor, OCTAL_INTEGER, TokenKind.OCTAL_INTEGER, 8, "octal")
        }
        if (text.startsWith("0b", offset) || text.startsWith("0B", offset)) {
            return scanPrefixedInteger(cursor, BINARY_INTEGER, TokenKind.BINARY_INTEGER, 2, "binary")
        }

        val float = FLOAT.matchAt(text, offset)
        if (float != null) {
            return scanRegex(cursor, FLOAT, TokenKind.FLOAT)
        }

        return scanRegex(cursor, DECIMAL_INTEGER, TokenKind.DECIMAL_INTEGER) {
            TokenPayload.IntegerDigits(10, it)
        }
    }

    private fun scanPrefixedInteger(
        cursor: LexingCursor,
        regex: Regex,
        kind: TokenKind,
        radix: Int,
        label: String,
    ): Token {
        val match = regex.matchAt(cursor.source.text, cursor.offset)
        if (match == null) {
            val end = (cursor.offset + 2).coerceAtMost(cursor.source.text.length)
            val span = cursor.source.span(cursor.offset, end)
            throw MalformedNumericLiteralException(
                cursor.source,
                span,
                cursor.source.text.substring(cursor.offset, end),
                "$label prefix is not followed by a valid digit",
            )
        }
        return scanRegex(cursor, regex, kind) {
            TokenPayload.IntegerDigits(radix, it.substring(2))
        }
    }

    private fun scanGraphic(cursor: LexingCursor): Token {
        val start = cursor.mark()
        val source = cursor.source
        val startOffset = cursor.offset

        if (source.text[startOffset] == '.' && isFullStopTerminator(source.text, startOffset + 1)) {
            cursor.advanceTo(startOffset + 1)
            return Token(
                id = -1,
                kind = TokenKind.FULL_STOP,
                channel = TokenChannel.SIGNIFICANT,
                span = cursor.spanFrom(start),
            )
        }

        var end = startOffset
        while (end < source.text.length && source.text[end].isGraphicCharacter()) {
            end += 1
        }
        cursor.advanceTo(end)
        val raw = source.text.substring(startOffset, end)
        val kind =
            if (raw.length == 1 && (raw == "+" || raw == "-")) {
                TokenKind.SIGN
            } else {
                TokenKind.GRAPHIC_ATOM
            }
        return Token(
            id = -1,
            kind = kind,
            channel = TokenChannel.SIGNIFICANT,
            span = cursor.spanFrom(start),
            payload = if (kind == TokenKind.GRAPHIC_ATOM) TokenPayload.Name(raw) else null,
        )
    }

    private fun scanSingleCharacter(
        cursor: LexingCursor,
        kind: TokenKind,
    ): Token {
        val start = cursor.mark()
        cursor.advanceTo(cursor.offset + 1)
        return Token(-1, kind, TokenChannel.SIGNIFICANT, cursor.spanFrom(start))
    }

    private fun scanLineComment(cursor: LexingCursor): Token {
        val start = cursor.mark()
        var end = cursor.offset + 1
        val text = cursor.source.text
        while (end < text.length && text[end] != '\r' && text[end] != '\n') {
            end += 1
        }
        cursor.advanceTo(end)
        return Token(-1, TokenKind.LINE_COMMENT, TokenChannel.TRIVIA, cursor.spanFrom(start))
    }

    private fun scanBlockComment(cursor: LexingCursor): Token {
        val start = cursor.mark()
        val closing = cursor.source.text.indexOf("*/", cursor.offset + 2)
        if (closing < 0) {
            val span = cursor.source.span(cursor.offset, cursor.source.text.length)
            throw UnterminatedBlockCommentException(cursor.source, span)
        }
        cursor.advanceTo(closing + 2)
        return Token(-1, TokenKind.BLOCK_COMMENT, TokenChannel.TRIVIA, cursor.spanFrom(start))
    }

    private fun scanQuoted(
        cursor: LexingCursor,
        quote: Char,
        kind: TokenKind,
        quoteKind: QuoteKind,
    ): Token {
        val start = cursor.mark()
        val source = cursor.source
        val text = source.text
        var index = cursor.offset + 1
        val decoded = StringBuilder()

        while (index < text.length) {
            val current = text[index]
            when {
                current == quote && text.getOrNull(index + 1) == quote -> {
                    decoded.append(quote)
                    index += 2
                }
                current == quote -> {
                    index += 1
                    cursor.advanceTo(index)
                    return Token(
                        id = -1,
                        kind = kind,
                        channel = TokenChannel.SIGNIFICANT,
                        span = cursor.spanFrom(start),
                        payload = TokenPayload.QuotedText(decoded.toString(), quoteKind),
                    )
                }
                current == '\\' -> {
                    val escape = EscapeDecoder.decode(source, index)
                    decoded.append(escape.value)
                    index = escape.endExclusiveOffset
                }
                current == '\r' || current == '\n' -> {
                    val span = source.span(start.offset, index)
                    throw UnterminatedQuotedLiteralException(source, span, quote)
                }
                else -> {
                    decoded.append(current)
                    index += 1
                }
            }
        }

        throw UnterminatedQuotedLiteralException(
            source,
            source.span(start.offset, source.text.length),
            quote,
        )
    }

    private fun scanCharacterCode(cursor: LexingCursor): Token {
        val start = cursor.mark()
        val source = cursor.source
        val text = source.text
        val contentOffset = cursor.offset + 2
        val current =
            text.getOrNull(contentOffset)
                ?: throw MalformedNumericLiteralException(
                    source,
                    source.span(cursor.offset, text.length),
                    text.substring(cursor.offset),
                    "character-code literal has no character",
                )

        val value: String
        val end: Int
        if (current == '\\') {
            val escape = EscapeDecoder.decode(source, contentOffset)
            value = escape.value
            end = escape.endExclusiveOffset
        } else {
            if (current == '\r' || current == '\n' || current == '\t' || current == '\u000c') {
                throw MalformedNumericLiteralException(
                    source,
                    source.span(cursor.offset, contentOffset + 1),
                    text.substring(cursor.offset, contentOffset + 1),
                    "character-code literal contains forbidden layout",
                )
            }
            value = current.toString()
            end = contentOffset + 1
        }

        if (value.length != 1) {
            throw MalformedNumericLiteralException(
                source,
                source.span(cursor.offset, end),
                text.substring(cursor.offset, end),
                "character-code escape must decode to exactly one UTF-16 code unit",
            )
        }

        cursor.advanceTo(end)
        return Token(
            id = -1,
            kind = TokenKind.CHARACTER_CODE,
            channel = TokenChannel.SIGNIFICANT,
            span = cursor.spanFrom(start),
            payload = TokenPayload.CharacterCode(value[0].code),
        )
    }

    private fun isFullStopTerminator(
        text: String,
        offset: Int,
    ): Boolean =
        offset >= text.length ||
            text[offset].isLayout() ||
            text[offset] == '%' ||
            text.startsWith("/*", offset)

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
