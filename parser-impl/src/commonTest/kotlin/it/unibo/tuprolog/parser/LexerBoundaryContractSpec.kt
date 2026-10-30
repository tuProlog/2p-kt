package it.unibo.tuprolog.parser

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.parser.exceptions.MalformedNumericLiteralException
import it.unibo.tuprolog.parser.exceptions.UnterminatedBlockCommentException
import it.unibo.tuprolog.parser.exceptions.UnterminatedQuotedLiteralException
import it.unibo.tuprolog.parser.tokens.Token
import it.unibo.tuprolog.parser.tokens.TokenKind
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class LexerBoundaryContractSpec :
    FunSpec({
        fun forEverySingleSplit(
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

        fun signature(source: it.unibo.tuprolog.parser.sources.LexedSource): List<String> =
            source.tokens.map { token ->
                "${token.kind}|${token.channel}|${source.textOf(token)}|" +
                    "${token.span.start.offset}:${token.span.endExclusive.offset}|${token.payload}"
            }

        fun String.escape(): String = replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t")

        withTests(nameFn = { "full-stop-termination/${it.id}" }, fullStopSuffixes) { case ->
            val source = lex("a.${case.suffix}")
            assertEquals(case.expected, source.significantTokens()[1].kind, "suffix=${case.suffix.escape()}")
        }

        test("line-comments-terminate-at-every-supported-line-ending") {
            val atEof = lex("a % comment")
            assertEquals(
                listOf(TokenKind.WORD_ATOM, TokenKind.END_OF_INPUT),
                atEof.significantTokens().map(Token::kind),
            )
        }

        withTests(nameFn = { "line-comments-terminate-at-line-ending/${it.id}" }, lineEndings) { case ->
            val input = lex("a % comment${case.ending}b")
            assertEquals(
                listOf(TokenKind.WORD_ATOM, TokenKind.WORD_ATOM, TokenKind.END_OF_INPUT),
                input.significantTokens().map(Token::kind),
            )
        }

        test("empty-and-doubled-quoted-literals-are-decoded") {
            val input = lex("'' \"\" 'can''t' \"say \"\"hi\"\"\"")
            val significant = input.significantTokens().dropLast(1)
            assertEquals(
                listOf("", "", "can't", "say \"hi\""),
                significant.map {
                    (it.payload as it.unibo.tuprolog.parser.tokens.TokenPayload.QuotedText).decoded
                },
            )
        }

        withTests(
            nameFn = {
                "malformed-numeric-prefix-is-rejected/${it.lowercase()}-${if (it[1].isUpperCase()) "upper" else "lower"}"
            },
            listOf("0x", "0X", "0o", "0O", "0b", "0B"),
        ) { input ->
            assertFailsWith<MalformedNumericLiteralException>(input) { lex(input).materialize() }
        }

        test("unterminated-constructs-remain-typed-across-chunk-boundaries/quoted-literal") {
            forEverySingleSplit("'unterminated") { source ->
                assertFailsWith<UnterminatedQuotedLiteralException> { source.materialize() }
            }
        }

        test("unterminated-constructs-remain-typed-across-chunk-boundaries/block-comment") {
            forEverySingleSplit("/* unterminated") { source ->
                assertFailsWith<UnterminatedBlockCommentException> { source.materialize() }
            }
        }

        withTests(nameFn = { "lexing-is-invariant-under-every-single-chunk-split/${it.id}" }, splitTexts) { case ->
            val expected =
                signature(
                    testLexer
                        .lex(
                            it.unibo.tuprolog.parser.sources
                                .SourceText(case.text),
                        ).materialize(),
                )
            forEverySingleSplit(case.text) { source ->
                assertEquals(expected, signature(source.materialize()), "split input: ${case.text}")
            }
        }
    })

private class FullStopSuffixCase(
    val id: String,
    val suffix: String,
    val expected: TokenKind,
)

private val fullStopSuffixes =
    listOf(
        FullStopSuffixCase("terminating/end-of-input", "", TokenKind.FULL_STOP),
        FullStopSuffixCase("terminating/space", " ", TokenKind.FULL_STOP),
        FullStopSuffixCase("terminating/tab", "\t", TokenKind.FULL_STOP),
        FullStopSuffixCase("terminating/line-feed", "\n", TokenKind.FULL_STOP),
        FullStopSuffixCase("terminating/crlf", "\r\n", TokenKind.FULL_STOP),
        FullStopSuffixCase("terminating/line-comment", "% comment", TokenKind.FULL_STOP),
        FullStopSuffixCase("terminating/block-comment", "/* comment */", TokenKind.FULL_STOP),
        FullStopSuffixCase("embedded/letter", "b", TokenKind.GRAPHIC_ATOM),
        FullStopSuffixCase("embedded/plus", "+", TokenKind.GRAPHIC_ATOM),
        FullStopSuffixCase("embedded/dot", ".", TokenKind.GRAPHIC_ATOM),
        FullStopSuffixCase("embedded/slash", "/x", TokenKind.GRAPHIC_ATOM),
    )

private class LineEndingCase(
    val id: String,
    val ending: String,
)

private val lineEndings =
    listOf(
        LineEndingCase("lf", "\n"),
        LineEndingCase("cr", "\r"),
        LineEndingCase("crlf", "\r\n"),
    )

private class SplitTextCase(
    val id: String,
    val text: String,
)

private val splitTexts =
    listOf(
        SplitTextCase("clause-with-hex", "foo(X) :- X = 0xff."),
        SplitTextCase("quoted-literals-and-escapes", "'I''m' \"a\\n\\x41\\\" 0'\\n"),
        SplitTextCase("comments-and-two-clauses", "a /* block */ + b. % tail\nnext."),
        SplitTextCase("list-block-and-real", "[a,b|T] {x,y} 2.0e-3"),
        SplitTextCase("exponents", "1.5e+10 1.5E7 2.5e-3"),
        SplitTextCase("character-codes", "0'a 0''' 0'\\x41\\ 0'\\101\\ 0'\\n"),
        SplitTextCase("escapes-and-continuations", "'a\\\nb' 'c\\\r\nd' '\\101\\' \"\\t\""),
        SplitTextCase("comments-ending-on-stars", "/* x **/ a % end\nb."),
        SplitTextCase("full-stop-at-end", "a."),
        SplitTextCase("graphic-atoms", "a :- \\+ b, c =.. d, e --> f."),
    )
