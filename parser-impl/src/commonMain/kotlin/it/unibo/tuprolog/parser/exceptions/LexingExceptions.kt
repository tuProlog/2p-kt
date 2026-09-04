package it.unibo.tuprolog.parser.exceptions

import it.unibo.tuprolog.parser.sources.Source
import it.unibo.tuprolog.parser.sources.SourceSpan

/** Base class for failures encountered while lazily converting source characters into tokens. */
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

/** Reports a character that cannot begin any supported Prolog token. */
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

/** Reports a single- or double-quoted literal that reaches EOF without its closing quote. */
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

/** Reports a block comment that reaches EOF without a matching closing delimiter. */
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

/** Reports an unsupported, incomplete, or out-of-range escape sequence in quoted text. */
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

/** Reports a numeric spelling whose radix, digits, exponent, or character code is incomplete. */
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

/**
 * Reports that an uncommitted parse item exceeded its configured token-buffer bound.
 *
 * @property maximumRetainedTokens configured maximum number of simultaneously retained tokens
 */
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

/**
 * Wraps a failure thrown while obtaining the next synchronous or asynchronous source chunk.
 *
 * The original failure is available as [cause].
 */
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
