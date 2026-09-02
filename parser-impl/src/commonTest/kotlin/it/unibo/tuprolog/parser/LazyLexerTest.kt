package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.exceptions.SourceReadException
import it.unibo.tuprolog.parser.exceptions.TokenBufferLimitExceededException
import it.unibo.tuprolog.parser.exceptions.UnterminatedQuotedLiteralException
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tokens.TokenKind
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LazyLexerTest {
    @Test
    fun creatingALexedSourceDoesNotReadOrTokenize() {
        var reads = 0
        val source =
            testLexer.lex(
                TextChunkSource {
                    reads += 1
                    if (reads == 1) "atom " else null
                },
            )

        assertEquals(0, reads)
        assertEquals(TokenKind.WORD_ATOM, source.significantToken(0).kind)
        assertEquals(1, reads)
    }

    @Test
    fun lexicalFailuresAreDeferredUntilTheirTokenIsRequested() {
        val source = testLexer.lex(SourceText("'unterminated"))
        assertFailsWith<UnterminatedQuotedLiteralException> { source.significantToken(0) }
    }

    @Test
    fun everyCharacterChunkingProducesTheSameTokensAndCoordinates() {
        val text = "foo(X) /* c */ :- X = 0xff, Y = 1.25e-3, Z = '\\x41\\'.\r\nnext."
        val expected = testLexer.lex(SourceText(text)).materialize()
        val expectedTokens = expected.tokens.toList()

        for (chunkSize in 1..12) {
            var offset = 0
            val actual =
                testLexer
                    .lex(
                        TextChunkSource {
                            if (offset == text.length) {
                                null
                            } else {
                                val end = (offset + chunkSize).coerceAtMost(text.length)
                                text.substring(offset, end).also { offset = end }
                            }
                        },
                    ).materialize()

            assertEquals(expectedTokens, actual.tokens.toList(), "chunk size $chunkSize")
            assertEquals(
                expectedTokens.map(expected::textOf),
                actual.tokens.toList().map(actual::textOf),
                "chunk size $chunkSize",
            )
        }
    }

    @Test
    fun streamedSessionSnapshotsSurviveInputRelease() {
        val text = "first.\nsecond.\nthird."
        var offset = 0
        val input =
            testLexer.lex(
                TextChunkSource {
                    if (offset == text.length) null else text.substring(offset, ++offset)
                },
                options = LexerOptions(retention = TokenRetention.RELEASE_COMMITTED),
            )
        val session = testParser.openSession(input)

        val first = assertNotNull(session.parseNextClause())
        val firstRootText = first.source.text(first.root.span)
        assertTrue(input.tokens.retainedCount <= 1)
        val second = assertNotNull(session.parseNextClause())
        val third = assertNotNull(session.parseNextClause())

        assertEquals("first.", firstRootText)
        assertEquals("first.", first.source.text(first.root.span))
        assertEquals("second.", second.source.text(second.root.span))
        assertEquals("third.", third.source.text(third.root.span))
        assertTrue(second.tokens.firstTokenId > first.tokens.firstTokenId)
        assertEquals(
            TokenKind.WORD_ATOM,
            second.tokens[second.root.expression.tokenRange.startInclusive].kind,
        )
        assertTrue(session.isAtEnd)
    }

    @Test
    fun aHardRetentionLimitRejectsAnOversizedCurrentClause() {
        val input =
            testLexer.lex(
                SourceText("f(a, b, c)."),
                LexerOptions(
                    retention = TokenRetention.RELEASE_COMMITTED,
                    maximumRetainedTokens = 3,
                ),
            )
        assertFailsWith<TokenBufferLimitExceededException> { testParser.openSession(input).parseNextClause() }
    }

    @Test
    fun sourceReadFailuresPreserveTheirCause() {
        val failure = IllegalStateException("broken source")
        val input = testLexer.lex(TextChunkSource { throw failure })
        val error = assertFailsWith<SourceReadException> { input.significantToken(0) }
        assertTrue(error.cause === failure)
    }

    @Test
    fun suspendingSessionsReadAndParseOneClauseAtATime() {
        val chunks = ArrayDeque(listOf("first", ".\nsec", "ond."))
        val source =
            object : SuspendingTextChunkSource {
                override suspend fun readChunk(): String? = chunks.removeFirstOrNull()
            }
        val session = testParser.openSession(source, sourceId = "async.pl")

        val first = runSuspend { session.parseNextClause() }
        assertNotNull(first)
        assertFalse(session.isAtEnd)
        val second = runSuspend { session.parseNextClause() }
        assertNotNull(second)
        assertEquals("first.", first.source.text(first.root.span))
        assertEquals("second.", second.source.text(second.root.span))
        assertNull(runSuspend { session.parseNextClause() })
        assertTrue(session.isAtEnd)
    }

    private fun <T> runSuspend(block: suspend () -> T): T {
        var outcome: Result<T>? = null
        block.startCoroutine(
            object : Continuation<T> {
                override val context = EmptyCoroutineContext

                override fun resumeWith(result: Result<T>) {
                    outcome = result
                }
            },
        )
        if (outcome == null) {
            error("Test coroutine did not complete synchronously")
        }
        return outcome.getOrThrow()
    }
}
