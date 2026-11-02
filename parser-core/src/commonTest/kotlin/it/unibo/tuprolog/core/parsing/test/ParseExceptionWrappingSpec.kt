package it.unibo.tuprolog.core.parsing.test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.TermParser
import it.unibo.tuprolog.core.parsing.TermParserImpl
import it.unibo.tuprolog.parser.LexerOptions
import it.unibo.tuprolog.parser.ParserOptions
import it.unibo.tuprolog.parser.TokenRetention
import it.unibo.tuprolog.parser.exceptions.AmbiguousOperatorUseException
import it.unibo.tuprolog.parser.exceptions.InvalidEscapeException
import it.unibo.tuprolog.parser.exceptions.MalformedNumericLiteralException
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

class ParseExceptionWrappingSpec :
    FunSpec({
        test("every-applicable-lexer-error-is-wrapped") {
            assertWrapped<UnexpectedCharacterException>("ok\né", line = 2, column = 1, offending = "é")
            assertWrapped<UnterminatedQuotedLiteralException>(
                "ok\n'unclosed",
                line = 2,
                column = 1,
                offending = "'unclosed",
            )
            assertWrapped<UnterminatedBlockCommentException>(
                "ok\n/* unclosed",
                line = 2,
                column = 1,
                offending = "/* unclosed",
            )
            assertWrapped<InvalidEscapeException>("ok\n'\\q'", line = 2, column = 2, offending = "\\q")
            assertWrapped<MalformedNumericLiteralException>("ok\n0x", line = 2, column = 1, offending = "0x")
        }

        test("every-applicable-grammar-error-is-wrapped") {
            assertWrapped<UnexpectedTokenException>("f()", line = 1, column = 3, offending = ")")
            assertWrapped<UnexpectedEndOfInputException>("f(", line = 1, column = 3)
            assertWrapped<MissingOperatorOperandException>("a +", line = 1, column = 3, offending = "+")
            assertWrapped<OperatorPriorityException>("a = b = c", line = 1, column = 7, offending = "=")

            val ambiguous =
                TermParser.withOperators(
                    Operator("~", Specifier.FX, 500),
                    Operator("~", Specifier.FY, 500),
                )
            assertWrapped<AmbiguousOperatorUseException>(
                "~ a",
                parser = ambiguous,
                line = 1,
                column = 1,
                offending = "~",
            )
        }

        test("configured-safety-limit-errors-are-wrapped") {
            val shallowParser =
                TermParserImpl(
                    Scope.empty(),
                    OperatorSet.DEFAULT,
                    parserOptions = ParserOptions(maximumNestingDepth = 3),
                )
            assertWrapped<NestingLimitExceededException>("((((a))))", parser = shallowParser, line = 1, column = 4)

            val boundedParser =
                TermParserImpl(
                    Scope.empty(),
                    OperatorSet.DEFAULT,
                    lexerOptions =
                        LexerOptions(
                            retention = TokenRetention.RELEASE_COMMITTED,
                            maximumRetainedTokens = 3,
                        ),
                )
            assertWrapped<TokenBufferLimitExceededException>("f(a, b, c)", parser = boundedParser, line = 1, column = 4)
        }
    })

private val defaultParser = TermParser.withDefaultOperators()

private inline fun <reified T : PrologSyntaxException> assertWrapped(
    input: String,
    parser: TermParser = defaultParser,
    line: Int,
    column: Int,
    offending: String? = null,
) {
    val wrapper = shouldThrow<ParseException> { parser.parseTerm(input) }
    val cause = wrapper.cause.shouldBeInstanceOf<T>()

    wrapper.input shouldBe input
    wrapper.line shouldBe line
    wrapper.column shouldBe column
    wrapper.offendingSymbol shouldBe offending
    wrapper.offendingSymbol shouldBe cause.offendingText
    wrapper.message shouldBe cause.message
}
