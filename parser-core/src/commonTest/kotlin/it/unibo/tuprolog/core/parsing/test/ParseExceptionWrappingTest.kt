package it.unibo.tuprolog.core.parsing.test

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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class ParseExceptionWrappingTest {
    private val parser = TermParser.withDefaultOperators()

    @Test
    fun everyApplicableLexerErrorIsWrapped() {
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

    @Test
    fun everyApplicableGrammarErrorIsWrapped() {
        assertWrapped<UnexpectedTokenException>("f()", line = 1, column = 3, offending = ")")
        assertWrapped<UnexpectedEndOfInputException>("f(", line = 1, column = 3)
        assertWrapped<MissingOperatorOperandException>("a +", line = 1, column = 3, offending = "+")
        assertWrapped<OperatorPriorityException>("a = b = c", line = 1, column = 7, offending = "=")

        val ambiguous =
            TermParser.withOperators(
                Operator("~", Specifier.FX, 500),
                Operator("~", Specifier.FY, 500),
            )
        assertWrapped<AmbiguousOperatorUseException>("~ a", parser = ambiguous, line = 1, column = 1, offending = "~")
    }

    @Test
    fun configuredSafetyLimitErrorsAreWrapped() {
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

    private inline fun <reified T : PrologSyntaxException> assertWrapped(
        input: String,
        parser: TermParser = this.parser,
        line: Int,
        column: Int,
        offending: String? = null,
    ) {
        val wrapper = assertFailsWith<ParseException> { parser.parseTerm(input) }
        val cause = assertIs<T>(wrapper.cause)

        assertEquals(input, wrapper.input)
        assertEquals(line, wrapper.line)
        assertEquals(column, wrapper.column)
        assertEquals(offending, wrapper.offendingSymbol)
        assertEquals(cause.offendingText, wrapper.offendingSymbol)
        assertEquals(cause.message, wrapper.message)
    }
}
