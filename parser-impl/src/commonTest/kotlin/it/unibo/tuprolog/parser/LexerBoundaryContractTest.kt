package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.exceptions.MalformedNumericLiteralException
import it.unibo.tuprolog.parser.exceptions.UnterminatedBlockCommentException
import it.unibo.tuprolog.parser.exceptions.UnterminatedQuotedLiteralException
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenKind
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LexerBoundaryContractTest {
    @Test
    fun fullStopTerminationContextMatrix() {
        val terminatingSuffixes = listOf("", " ", "\t", "\n", "\r\n", "% comment", "/* comment */")
        for (suffix in terminatingSuffixes) {
            val source = lex("a.$suffix")
            assertEquals(TokenKind.FULL_STOP, source.significantTokens()[1].kind, "suffix=${suffix.escape()}")
        }

        val embeddedSuffixes = listOf("b", "+", ".", "/x")
        for (suffix in embeddedSuffixes) {
            val source = lex("a.$suffix")
            assertEquals(TokenKind.GRAPHIC_ATOM, source.significantTokens()[1].kind, "suffix=$suffix")
        }
    }

    @Test
    fun lineCommentsTerminateAtEverySupportedLineEnding() {
        val atEof = lex("a % comment")
        assertEquals(listOf(TokenKind.WORD_ATOM, TokenKind.END_OF_INPUT), atEof.significantTokens().map(Token::kind))

        for (ending in listOf("\n", "\r", "\r\n")) {
            val input = lex("a % comment${ending}b")
            assertEquals(
                listOf(TokenKind.WORD_ATOM, TokenKind.WORD_ATOM, TokenKind.END_OF_INPUT),
                input.significantTokens().map(Token::kind),
            )
        }
    }

    @Test
    fun emptyAndDoubledQuotedLiteralsAreDecoded() {
        val input = lex("'' \"\" 'can''t' \"say \"\"hi\"\"\"")
        val significant = input.significantTokens().dropLast(1)
        assertEquals(
            listOf("", "", "can't", "say \"hi\""),
            significant.map {
                (it.payload as it.unibo.tuprolog.parser.tokens.TokenPayload.QuotedText).decoded
            },
        )
    }

    @Test
    fun malformedNumericPrefixesAreRejectedCaseInsensitively() {
        for (input in listOf("0x", "0X", "0o", "0O", "0b", "0B")) {
            assertFailsWith<MalformedNumericLiteralException>(input) { lex(input).materialize() }
        }
    }

    @Test
    fun unterminatedConstructsRemainTypedAcrossChunkBoundaries() {
        forEverySingleSplit("'unterminated") { source ->
            assertFailsWith<UnterminatedQuotedLiteralException> { source.materialize() }
        }
        forEverySingleSplit("/* unterminated") { source ->
            assertFailsWith<UnterminatedBlockCommentException> { source.materialize() }
        }
    }

    @Test
    fun lexingIsInvariantUnderEverySingleChunkSplit() {
        val cases =
            listOf(
                "foo(X) :- X = 0xff.",
                "'I''m' \"a\\n\\x41\\\" 0'\\n",
                "a /* block */ + b. % tail\nnext.",
                "[a,b|T] {x,y} 2.0e-3",
            )
        for (text in cases) {
            val expected =
                signature(
                    testLexer
                        .lex(
                            it.unibo.tuprolog.parser.sources
                                .SourceText(text),
                        ).materialize(),
                )
            forEverySingleSplit(text) { source ->
                assertEquals(expected, signature(source.materialize()), "split input: $text")
            }
        }
    }

    private fun forEverySingleSplit(
        text: String,
        block: (it.unibo.tuprolog.parser.sources.LexedSource) -> Unit,
    ) {
        for (split in 0..text.length) {
            var step = 0
            val chunks = listOf(text.substring(0, split), text.substring(split))
            val source =
                testLexer.lex(
                    TextChunkSource {
                        if (step < chunks.size) chunks[step++].takeIf(String::isNotEmpty) ?: "" else null
                    },
                )
            block(source)
        }
    }

    private fun signature(source: it.unibo.tuprolog.parser.sources.LexedSource): List<String> =
        source.tokens.map { token ->
            "${token.kind}|${token.channel}|${source.textOf(token)}|" +
                "${token.span.start.offset}:${token.span.endExclusive.offset}|${token.payload}"
        }

    private fun String.escape(): String = replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")
}
