package it.unibo.tuprolog.parser.exceptions

import it.unibo.tuprolog.parser.sources.Source
import it.unibo.tuprolog.parser.sources.SourceSpan

sealed class PrologLexingException(
    code: SyntaxErrorCode,
    source: Source,
    span: SourceSpan,
    offendingText: String?,
    expected: Set<SyntaxExpectation> = emptySet(),
    message: String,
    cause: Throwable? = null,
) : PrologSyntaxException(
        code = code,
        sourceId = source.id,
        span = span,
        offendingText = offendingText,
        expected = expected,
        rulePath = listOf("lexer"),
        message = message,
        cause = cause,
    )

class UnexpectedCharacterException(
    source: Source,
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
    source: Source,
    span: SourceSpan,
    quote: Char,
) : PrologLexingException(
        SyntaxErrorCode.UNTERMINATED_QUOTED_LITERAL,
        source,
        span,
        source.text(span),
        setOf(SyntaxExpectation("closing $quote")),
        "Unterminated quoted literal beginning at ${span.start.line}:${span.start.column}",
    )

class UnterminatedBlockCommentException(
    source: Source,
    span: SourceSpan,
) : PrologLexingException(
        SyntaxErrorCode.UNTERMINATED_BLOCK_COMMENT,
        source,
        span,
        source.text(span),
        setOf(SyntaxExpectation("*/")),
        "Unterminated block comment beginning at ${span.start.line}:${span.start.column}",
    )

class InvalidEscapeException(
    source: Source,
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
    source: Source,
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

class TokenBufferLimitExceededException(
    source: Source,
    span: SourceSpan,
    val maximumRetainedTokens: Int,
) : PrologLexingException(
        SyntaxErrorCode.TOKEN_BUFFER_LIMIT_EXCEEDED,
        source,
        span,
        null,
        message = "The uncommitted token buffer exceeded $maximumRetainedTokens tokens",
    )

class SourceReadException(
    source: Source,
    span: SourceSpan,
    cause: Throwable,
) : PrologLexingException(
        SyntaxErrorCode.SOURCE_READ_FAILURE,
        source,
        span,
        null,
        message = "Could not read source at ${span.start.line}:${span.start.column}: ${cause.message}",
        cause = cause,
    )
