package it.unibo.tuprolog.parser.exceptions

import it.unibo.tuprolog.parser.operators.OperatorDefinition
import it.unibo.tuprolog.parser.sources.Source
import it.unibo.tuprolog.parser.sources.SourceSpan
import it.unibo.tuprolog.parser.tokens.Token

sealed class PrologParsingException(
    code: SyntaxErrorCode,
    source: Source,
    span: SourceSpan,
    offendingText: String?,
    expected: Set<SyntaxExpectation>,
    rulePath: List<String>,
    message: String,
) : PrologSyntaxException(
        code,
        source.id,
        span,
        offendingText,
        expected,
        rulePath,
        message,
    )

class UnexpectedTokenException(
    source: Source,
    token: Token,
    offendingText: String,
    expected: Set<SyntaxExpectation>,
    rulePath: List<String>,
) : PrologParsingException(
        SyntaxErrorCode.UNEXPECTED_TOKEN,
        source,
        token.span,
        offendingText,
        expected,
        rulePath,
        "Unexpected token '$offendingText' at ${token.span.start.line}:${token.span.start.column}; " +
            "expected ${expected.joinToString { it.description }}",
    )

class UnexpectedEndOfInputException(
    source: Source,
    token: Token,
    expected: Set<SyntaxExpectation>,
    rulePath: List<String>,
) : PrologParsingException(
        SyntaxErrorCode.UNEXPECTED_END_OF_INPUT,
        source,
        token.span,
        null,
        expected,
        rulePath,
        "Unexpected end of input at ${token.span.start.line}:${token.span.start.column}; " +
            "expected ${expected.joinToString { it.description }}",
    )

class MissingOperatorOperandException(
    source: Source,
    operatorToken: Token,
    operatorText: String,
    val definition: OperatorDefinition,
    val side: String,
    rulePath: List<String>,
) : PrologParsingException(
        SyntaxErrorCode.MISSING_OPERATOR_OPERAND,
        source,
        operatorToken.span,
        operatorText,
        setOf(SyntaxExpectation("$side operand")),
        rulePath,
        "Operator '$operatorText' (${definition.specifier.name.lowercase()}, priority ${definition.priority}) " +
            "is missing its $side operand",
    )

class OperatorPriorityException(
    source: Source,
    operatorToken: Token,
    operatorText: String,
    val definition: OperatorDefinition,
    val operandPriority: Int,
    val side: String,
    rulePath: List<String>,
) : PrologParsingException(
        SyntaxErrorCode.OPERATOR_PRIORITY_VIOLATION,
        source,
        operatorToken.span,
        operatorText,
        emptySet(),
        rulePath,
        "Operator '$operatorText' (${definition.specifier.name.lowercase()}, priority ${definition.priority}) " +
            "cannot accept a $side operand with priority $operandPriority",
    )

class AmbiguousOperatorUseException(
    source: Source,
    operatorToken: Token,
    operatorText: String,
    val candidates: List<OperatorDefinition>,
    rulePath: List<String>,
) : PrologParsingException(
        SyntaxErrorCode.AMBIGUOUS_OPERATOR_USE,
        source,
        operatorToken.span,
        operatorText,
        candidates.mapTo(linkedSetOf()) {
            SyntaxExpectation("${it.specifier.name.lowercase()} at priority ${it.priority}")
        },
        rulePath,
        "Ambiguous operator '$operatorText': " +
            candidates.joinToString { "${it.specifier.name.lowercase()}/${it.priority}" },
    )

class MissingClauseTerminatorException(
    source: Source,
    token: Token,
    offendingText: String?,
    rulePath: List<String>,
) : PrologParsingException(
        SyntaxErrorCode.MISSING_CLAUSE_TERMINATOR,
        source,
        token.span,
        offendingText,
        setOf(SyntaxExpectation("clause-terminating full stop")),
        rulePath,
        "Expected a clause-terminating full stop at ${token.span.start.line}:${token.span.start.column}",
    )

class NestingLimitExceededException(
    source: Source,
    token: Token,
    val maximumDepth: Int,
    rulePath: List<String>,
) : PrologParsingException(
        SyntaxErrorCode.NESTING_LIMIT_EXCEEDED,
        source,
        token.span,
        null,
        emptySet(),
        rulePath,
        "Maximum parser nesting depth $maximumDepth exceeded at " +
            "${token.span.start.line}:${token.span.start.column}",
    )
