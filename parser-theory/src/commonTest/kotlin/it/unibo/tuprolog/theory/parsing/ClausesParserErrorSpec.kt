package it.unibo.tuprolog.theory.parsing

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.parser.LexerOptions
import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.TokenRetention
import it.unibo.tuprolog.parser.exceptions.AmbiguousOperatorUseException
import it.unibo.tuprolog.parser.exceptions.InvalidEscapeException
import it.unibo.tuprolog.parser.exceptions.InvalidOperatorDefinitionException
import it.unibo.tuprolog.parser.exceptions.MalformedNumericLiteralException
import it.unibo.tuprolog.parser.exceptions.MissingClauseTerminatorException
import it.unibo.tuprolog.parser.exceptions.MissingOperatorOperandException
import it.unibo.tuprolog.parser.exceptions.NestingLimitExceededException
import it.unibo.tuprolog.parser.exceptions.OperatorPriorityException
import it.unibo.tuprolog.parser.exceptions.PrologSyntaxException
import it.unibo.tuprolog.parser.exceptions.TokenBufferLimitExceededException
import it.unibo.tuprolog.parser.exceptions.UnexpectedCharacterException
import it.unibo.tuprolog.parser.exceptions.UnexpectedEndOfInputException
import it.unibo.tuprolog.parser.exceptions.UnexpectedTokenException
import it.unibo.tuprolog.parser.exceptions.UnterminatedBlockCommentException
import it.unibo.tuprolog.parser.exceptions.UnterminatedQuotedLiteralException

class ClausesParserErrorSpec :
    FunSpec({
        fun assertWrapperMirrorsCause(error: ParseException) {
            val cause = error.cause.shouldBeInstanceOf<PrologSyntaxException>()
            error.line shouldBe (cause.span.start.line + 1)
            error.column shouldBe (cause.span.start.column + 1)
            error.offendingSymbol shouldBe cause.offendingText
            error.message shouldBe cause.message
        }

        test("syntax-and-lexical-errors-are-translated-with-clause-coordinates") {
            assertTranslatedError<UnexpectedTokenException>("ok.\nf().", clauseIndex = 1, line = 2, column = 3)
            assertTranslatedError<UnexpectedEndOfInputException>("ok.\nf(", clauseIndex = 1, line = 2, column = 3)
            assertTranslatedError<MissingClauseTerminatorException>("ok.\nf(a)", clauseIndex = 1, line = 2, column = 5)
            assertTranslatedError<MissingOperatorOperandException>("ok.\na + .", clauseIndex = 1, line = 2, column = 3)
            assertTranslatedError<UnterminatedQuotedLiteralException>(
                "ok.\n'unclosed",
                clauseIndex = 1,
                line = 2,
                column = 1,
            )
            assertTranslatedError<UnterminatedBlockCommentException>(
                "ok.\n/* unclosed",
                clauseIndex = 1,
                line = 2,
                column = 1,
            )
            assertTranslatedError<InvalidEscapeException>("ok.\n'\\q'.", clauseIndex = 1, line = 2, column = 2)
            assertTranslatedError<MalformedNumericLiteralException>("ok.\n0x.", clauseIndex = 1, line = 2, column = 1)
            assertTranslatedError<UnexpectedCharacterException>("ok.\né.", clauseIndex = 1, line = 2, column = 1)
            assertTranslatedError<OperatorPriorityException>("ok.\na = b = c.", clauseIndex = 1, line = 2, column = 7)
        }

        test("parser-safety-and-ambiguity-errors-are-translated") {
            val depth = ParserOptions.DEFAULT_MAX_NESTING_DEPTH + 1
            val nested = "(".repeat(depth) + "a" + ")".repeat(depth) + "."
            val nestingError = shouldThrow<ParseException> { parser.parseClauses(nested) }
            nestingError.cause.shouldBeInstanceOf<NestingLimitExceededException>()
            assertWrapperMirrorsCause(nestingError)
            nestingError.clauseIndex shouldBe 0

            val ambiguousParser =
                ClausesParser.withOperators(
                    Operator("~", Specifier.FX, 500),
                    Operator("~", Specifier.FY, 500),
                )
            val ambiguityError = shouldThrow<ParseException> { ambiguousParser.parseClauses("~ a.") }
            ambiguityError.cause.shouldBeInstanceOf<AmbiguousOperatorUseException>()
            assertWrapperMirrorsCause(ambiguityError)
            ambiguityError.clauseIndex shouldBe 0

            val boundedParser =
                ClausesParserImpl(
                    defaultOperatorSet = OperatorSet.DEFAULT,
                    lexerOptions =
                        LexerOptions(
                            retention = TokenRetention.RELEASE_COMMITTED,
                            maximumRetainedTokens = 3,
                        ),
                )
            val bufferError = shouldThrow<ParseException> { boundedParser.parseClauses("f(a, b, c).") }
            bufferError.cause.shouldBeInstanceOf<TokenBufferLimitExceededException>()
            assertWrapperMirrorsCause(bufferError)
            bufferError.clauseIndex shouldBe 0
        }

        withTests(
            nameFn = { "invalid-operator-definitions-are-translated/${it.id}" },
            listOf(
                InvalidOperatorCase("zero-priority", ":- op(0, xfx, bad)."),
                InvalidOperatorCase("priority-above-1200", ":- op(1201, xfx, bad)."),
                InvalidOperatorCase("empty-name", ":- op(500, xfx, '')."),
            ),
        ) { case ->
            val error = shouldThrow<ParseException> { parser.parseClauses(case.input) }

            error.clauseIndex shouldBe 0
            error.line shouldBe 1
            error.column shouldBe 1
            error.input shouldBe case.input
            error.cause.shouldBeInstanceOf<InvalidOperatorDefinitionException>()
            error.offendingSymbol
                .orEmpty()
                .contains("op")
                .shouldBeTrue()
        }

        test("lazy-parsing-defers-errors-until-the-failing-clause-is-requested") {
            val clauses = parser.parseClausesLazily("first. second. broken(")
            val iterator = clauses.iterator()

            (iterator.next() as Fact).head.functor shouldBe "first"
            (iterator.next() as Fact).head.functor shouldBe "second"
            val error = shouldThrow<ParseException> { iterator.hasNext() }
            error.clauseIndex shouldBe 2
        }
    })

private val parser = ClausesParser.withDefaultOperators()

private inline fun <reified T : PrologSyntaxException> assertTranslatedError(
    input: String,
    clauseIndex: Int,
    line: Int,
    column: Int,
) {
    val error = shouldThrow<ParseException> { parser.parseClauses(input) }
    error.input shouldBe input
    error.clauseIndex shouldBe clauseIndex
    error.line shouldBe line
    error.column shouldBe column
    val cause = error.cause.shouldBeInstanceOf<T>()
    error.offendingSymbol shouldBe cause.offendingText
    error.message shouldBe cause.message
}

private class InvalidOperatorCase(
    val id: String,
    val input: String,
)
