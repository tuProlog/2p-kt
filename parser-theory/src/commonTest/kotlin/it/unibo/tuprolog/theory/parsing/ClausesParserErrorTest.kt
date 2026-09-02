package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.parser.LexerOptions
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class ClausesParserErrorTest {
    private val parser = ClausesParser.withDefaultOperators()

    @Test
    fun syntaxAndLexicalErrorsAreTranslatedWithClauseCoordinates() {
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

    @Test
    fun parserSafetyAndAmbiguityErrorsAreTranslated() {
        val nested = "(".repeat(1_025) + "a" + ")".repeat(1_025) + "."
        val nestingError = assertFailsWith<ParseException> { parser.parseClauses(nested) }
        assertIs<NestingLimitExceededException>(nestingError.cause)
        assertWrapperMirrorsCause(nestingError)
        assertEquals(0, nestingError.clauseIndex)

        val ambiguousParser =
            ClausesParser.withOperators(
                Operator("~", Specifier.FX, 500),
                Operator("~", Specifier.FY, 500),
            )
        val ambiguityError = assertFailsWith<ParseException> { ambiguousParser.parseClauses("~ a.") }
        assertIs<AmbiguousOperatorUseException>(ambiguityError.cause)
        assertWrapperMirrorsCause(ambiguityError)
        assertEquals(0, ambiguityError.clauseIndex)

        val boundedParser =
            ClausesParserImpl(
                defaultOperatorSet = OperatorSet.DEFAULT,
                lexerOptions =
                    LexerOptions(
                        retention = TokenRetention.RELEASE_COMMITTED,
                        maximumRetainedTokens = 3,
                    ),
            )
        val bufferError = assertFailsWith<ParseException> { boundedParser.parseClauses("f(a, b, c).") }
        assertIs<TokenBufferLimitExceededException>(bufferError.cause)
        assertWrapperMirrorsCause(bufferError)
        assertEquals(0, bufferError.clauseIndex)
    }

    @Test
    fun invalidOperatorDefinitionsAreTranslatedAndIdentifyTheirDirective() {
        for (input in listOf(":- op(0, xfx, bad).", ":- op(1201, xfx, bad).", ":- op(500, xfx, '').")) {
            val error = assertFailsWith<ParseException> { parser.parseClauses(input) }

            assertEquals(0, error.clauseIndex)
            assertEquals(1, error.line)
            assertEquals(1, error.column)
            assertEquals(input, error.input)
            assertIs<InvalidOperatorDefinitionException>(error.cause)
            assertTrue(error.offendingSymbol.orEmpty().contains("op"))
        }
    }

    @Test
    fun lazyParsingDefersErrorsUntilTheFailingClauseIsRequested() {
        val clauses = parser.parseClausesLazily("first. second. broken(")
        val iterator = clauses.iterator()

        assertEquals("first", (iterator.next() as Fact).head.functor)
        assertEquals("second", (iterator.next() as Fact).head.functor)
        val error = assertFailsWith<ParseException> { iterator.hasNext() }
        assertEquals(2, error.clauseIndex)
    }

    private inline fun <reified T : PrologSyntaxException> assertTranslatedError(
        input: String,
        clauseIndex: Int,
        line: Int,
        column: Int,
    ) {
        val error = assertFailsWith<ParseException> { parser.parseClauses(input) }
        assertEquals(input, error.input)
        assertEquals(clauseIndex, error.clauseIndex)
        assertEquals(line, error.line)
        assertEquals(column, error.column)
        val cause = assertIs<T>(error.cause)
        assertEquals(cause.offendingText, error.offendingSymbol)
        assertEquals(cause.message, error.message)
    }

    private fun assertWrapperMirrorsCause(error: ParseException) {
        val cause = assertIs<PrologSyntaxException>(error.cause)
        assertEquals(cause.span.start.line + 1, error.line)
        assertEquals(cause.span.start.column + 1, error.column)
        assertEquals(cause.offendingText, error.offendingSymbol)
        assertEquals(cause.message, error.message)
    }
}
