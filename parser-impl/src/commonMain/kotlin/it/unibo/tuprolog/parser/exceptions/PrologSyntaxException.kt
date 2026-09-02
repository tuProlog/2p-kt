package it.unibo.tuprolog.parser.exceptions

import it.unibo.tuprolog.parser.sources.SourceSpan

/**
 * Root of all typed lexical and grammatical diagnostics produced by `parser-impl`.
 *
 * Consumers should normally catch this type, then inspect [code] for stable programmatic
 * classification. Coordinates in [span] are zero-based and end-exclusive. Higher-level
 * `parser-core` and `parser-theory` APIs wrap this exception in their domain-facing
 * `ParseException` while preserving it as the cause.
 *
 * @property code stable category of the failure
 * @property sourceId optional diagnostic identifier of the source
 * @property span exact source range associated with the failure
 * @property offendingText offending source spelling, or `null` when no token exists
 * @property expected structured descriptions of acceptable syntax
 * @property rulePath outer-to-inner grammar rules active at the failure
 * @param message human-readable diagnostic
 * @param cause underlying source or decoding failure, if any
 */
sealed class PrologSyntaxException(
    val code: SyntaxErrorCode,
    val sourceId: String?,
    val span: SourceSpan,
    val offendingText: String?,
    val expected: Set<SyntaxExpectation>,
    val rulePath: List<String>,
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
