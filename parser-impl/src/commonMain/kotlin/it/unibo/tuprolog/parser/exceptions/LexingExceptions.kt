package it.unibo.tuprolog.parser.exceptions

import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.sources.SourceText

sealed class PrologLexingException(
    code: SyntaxErrorCode,
    source: SourceText,
    span: SourceSpan,
    offendingText: String?,
    expected: Set<SyntaxExpectation> = emptySet(),
    message: String,
) : PrologSyntaxException(
        code = code,
        sourceId = source.id,
        span = span,
        offendingText = offendingText,
        expected = expected,
        rulePath = listOf("lexer"),
        message = message,
    )

class UnexpectedCharacterException(
    source: SourceText,
    span: SourceSpan,
    offendingText: String,
) : PrologLexingException(
        SyntaxErrorCode.UNEXPECTED_CHARACTER,
        source,
        span,
        offendingText,
        message = "Unexpected character '$offendingText' at ${span.start.line}:${span.start.column}",
    )

class UnterminatedQuotedLiteralException(
    source: SourceText,
    span: SourceSpan,
    quote: Char,
) : PrologLexingException(
        SyntaxErrorCode.UNTERMINATED_QUOTED_LITERAL,
        source,
        span,
        source.text.substring(span.start.offset, span.endExclusive.offset),
        setOf(SyntaxExpectation("closing $quote")),
        "Unterminated quoted literal beginning at ${span.start.line}:${span.start.column}",
    )

class UnterminatedBlockCommentException(
    source: SourceText,
    span: SourceSpan,
) : PrologLexingException(
        SyntaxErrorCode.UNTERMINATED_BLOCK_COMMENT,
        source,
        span,
        source.text.substring(span.start.offset, span.endExclusive.offset),
        setOf(SyntaxExpectation("*/")),
        "Unterminated block comment beginning at ${span.start.line}:${span.start.column}",
    )

class InvalidEscapeException(
    source: SourceText,
    span: SourceSpan,
    offendingText: String,
    detail: String,
) : PrologLexingException(
        SyntaxErrorCode.INVALID_ESCAPE,
        source,
        span,
        offendingText,
        message = "Invalid escape '$offendingText' at ${span.start.line}:${span.start.column}: $detail",
    )

class MalformedNumericLiteralException(
    source: SourceText,
    span: SourceSpan,
    offendingText: String,
    detail: String,
) : PrologLexingException(
        SyntaxErrorCode.MALFORMED_NUMERIC_LITERAL,
        source,
        span,
        offendingText,
        message = "Malformed numeric literal '$offendingText' at ${span.start.line}:${span.start.column}: $detail",
    )
