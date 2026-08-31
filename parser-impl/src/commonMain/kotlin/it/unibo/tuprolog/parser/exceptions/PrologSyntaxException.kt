package it.unibo.tuprolog.parser.exceptions

import it.unibo.tuprolog.parser.sources.SourceSpan

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
