package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.exceptions.InvalidEscapeException
import it.unibo.tuprolog.parser.exceptions.MalformedNumericLiteralException
import it.unibo.tuprolog.parser.exceptions.SyntaxErrorCode
import it.unibo.tuprolog.parser.exceptions.UnterminatedBlockCommentException
import it.unibo.tuprolog.parser.exceptions.UnterminatedQuotedLiteralException
import it.unibo.tuprolog.parser.sources.SourcePosition
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenChannel
import it.unibo.tuprolog.parser.tokens.TokenKind
import it.unibo.tuprolog.parser.tokens.TokenPayload
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SourceAndLexerTest {
    @Test
    fun positionsTreatCrLfAsOneLineBreak() {
        val source = SourceText("a\r\nb\rc\nd")
        assertEquals(SourcePosition(0, 0, 0), source.positionAt(0))
        assertEquals(SourcePosition(3, 1, 0), source.positionAt(3))
        assertEquals(SourcePosition(5, 2, 0), source.positionAt(5))
        assertEquals(SourcePosition(7, 3, 0), source.positionAt(7))
    }

    @Test
    fun tokenSlicesReconstructTheOriginalSource() {
        val text = "foo(X) /* block */ :- X = 1.\n% tail\n"
        val source = lex(text)
        val reconstructed =
            source.tokens
                .filter { it.kind != TokenKind.END_OF_INPUT }
                .joinToString(separator = "") { source.textOf(it) }
        assertEquals(text, reconstructed)
    }

    @Test
    fun lexesVariablesAndAtomCandidatesWithoutOperatorState() {
        val tokens = lex("_ _X X foo foo_bar ++ ;").significantTokens().dropLast(1)
        assertEquals(
            listOf(
                TokenKind.VARIABLE,
                TokenKind.VARIABLE,
                TokenKind.VARIABLE,
                TokenKind.WORD_ATOM,
                TokenKind.WORD_ATOM,
                TokenKind.GRAPHIC_ATOM,
                TokenKind.GRAPHIC_ATOM,
            ),
            tokens.map(Token::kind),
        )
        assertEquals("_", (tokens[0].payload as TokenPayload.Name).value)
    }

    @Test
    fun lexesAllNumericFamilies() {
        val tokens = lex("42 0xff 0O17 0b101 1.25 2.0e-3 0'a").significantTokens().dropLast(1)
        assertEquals(
            listOf(
                TokenKind.DECIMAL_INTEGER,
                TokenKind.HEX_INTEGER,
                TokenKind.OCTAL_INTEGER,
                TokenKind.BINARY_INTEGER,
                TokenKind.FLOAT,
                TokenKind.FLOAT,
                TokenKind.CHARACTER_CODE,
            ),
            tokens.map(Token::kind),
        )
        assertEquals(97, (tokens.last().payload as TokenPayload.CharacterCode).codePoint)
    }

    @Test
    fun singleSignsAreDedicatedButLongerGraphicSequencesAreAtoms() {
        val tokens = lex("+ - +- -- ->").significantTokens().dropLast(1)
        assertEquals(
            listOf(
                TokenKind.SIGN,
                TokenKind.SIGN,
                TokenKind.GRAPHIC_ATOM,
                TokenKind.GRAPHIC_ATOM,
                TokenKind.GRAPHIC_ATOM,
            ),
            tokens.map(Token::kind),
        )
    }

    @Test
    fun quotedTextIsDecodedWithoutLosingRawCoordinates() {
        val source = lex("'I''m' \"a\\n\\x41\\\"")
        val tokens = source.significantTokens().dropLast(1)
        assertEquals("I'm", (tokens[0].payload as TokenPayload.QuotedText).decoded)
        assertEquals("a\nA", (tokens[1].payload as TokenPayload.QuotedText).decoded)
        assertEquals("'I''m'", source.textOf(tokens[0]))
        assertEquals(0, tokens[0].span.start.column)
        assertEquals(6, tokens[0].span.endExclusive.column)
    }

    @Test
    fun octalAndHexadecimalEscapesAreDecoded() {
        val tokens = lex("'\\101\\' '\\x41\\'").significantTokens().dropLast(1)
        assertEquals("A", (tokens[0].payload as TokenPayload.QuotedText).decoded)
        assertEquals("A", (tokens[1].payload as TokenPayload.QuotedText).decoded)
    }

    @Test
    fun commentsAndWhitespaceAreRetainedAsTrivia() {
        val tokens = lex("a % line\n /* block */ b").tokens
        assertTrue(tokens.any { it.kind == TokenKind.LINE_COMMENT && it.channel == TokenChannel.TRIVIA })
        assertTrue(tokens.any { it.kind == TokenKind.BLOCK_COMMENT && it.channel == TokenChannel.TRIVIA })
        assertTrue(tokens.any { it.kind == TokenKind.WHITESPACE && it.channel == TokenChannel.TRIVIA })
        assertFalse(tokens.first().channel == TokenChannel.TRIVIA)
    }

    @Test
    fun aPeriodIsAFullStopOnlyInTerminatingContext() {
        val terminating = lex("a. b").significantTokens().dropLast(1)
        assertEquals(TokenKind.FULL_STOP, terminating[1].kind)

        val embedded = lex("a.b").significantTokens().dropLast(1)
        assertEquals(TokenKind.GRAPHIC_ATOM, embedded[1].kind)
        assertEquals(".", lex("a.b").textOf(embedded[1]))
    }

    @Test
    fun emptyDelimitersRemainSeparateTokens() {
        val tokens = lex("[ ] {\n}").significantTokens().dropLast(1)
        assertEquals(
            listOf(
                TokenKind.LEFT_BRACKET,
                TokenKind.RIGHT_BRACKET,
                TokenKind.LEFT_BRACE,
                TokenKind.RIGHT_BRACE,
            ),
            tokens.map(Token::kind),
        )
    }

    @Test
    fun unterminatedQuoteReportsItsOpeningCoordinate() {
        val error = assertFailsWith<UnterminatedQuotedLiteralException> { lex("a\n'bad").materialize() }
        assertEquals(SyntaxErrorCode.UNTERMINATED_QUOTED_LITERAL, error.code)
        assertEquals(1, error.span.start.line)
        assertEquals(0, error.span.start.column)
    }

    @Test
    fun unterminatedBlockCommentIsTyped() {
        val error = assertFailsWith<UnterminatedBlockCommentException> { lex("a /* bad").materialize() }
        assertEquals(SyntaxErrorCode.UNTERMINATED_BLOCK_COMMENT, error.code)
        assertEquals("/* bad", error.offendingText)
    }

    @Test
    fun malformedBasePrefixIsRejected() {
        val error = assertFailsWith<MalformedNumericLiteralException> { lex("0x").materialize() }
        assertEquals(SyntaxErrorCode.MALFORMED_NUMERIC_LITERAL, error.code)
    }

    @Test
    fun aLoneCarriageReturnEscapeIsRejected() {
        val error = assertFailsWith<InvalidEscapeException> { lex("'\\\rstill quoted'").materialize() }
        assertEquals(SyntaxErrorCode.INVALID_ESCAPE, error.code)
    }

    @Test
    fun unsupportedEscapesAreRejected() {
        val error = assertFailsWith<InvalidEscapeException> { lex("'\\q'").materialize() }
        assertEquals(SyntaxErrorCode.INVALID_ESCAPE, error.code)
        assertEquals("\\q", error.offendingText)
    }
}
