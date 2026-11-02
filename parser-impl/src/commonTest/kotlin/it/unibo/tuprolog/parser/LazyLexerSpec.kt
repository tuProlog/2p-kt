package it.unibo.tuprolog.parser

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.parser.exceptions.SourceReadException
import it.unibo.tuprolog.parser.exceptions.TokenBufferLimitExceededException
import it.unibo.tuprolog.parser.exceptions.UnterminatedQuotedLiteralException
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tokens.TokenKind
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class LazyLexerSpec :
    FunSpec({
        fun <T> runSuspend(block: suspend () -> T): T {
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

        test("creating-a-lexed-source-does-not-read-or-tokenize") {
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

        test("lexical-failures-are-deferred-until-their-token-is-requested") {
            val source = testLexer.lex(SourceText("'unterminated"))
            assertFailsWith<UnterminatedQuotedLiteralException> { source.significantToken(0) }
        }

        withTests(nameFn = {
            "chunking-produces-the-same-tokens-and-coordinates/chunk-size-$it"
        }, (1..12).toList()) { chunkSize ->
            val text = "foo(X) /* c */ :- X = 0xff, Y = 1.25e-3, Z = '\\x41\\'.\r\nnext."
            val expected = testLexer.lex(SourceText(text)).materialize()
            val expectedTokens = expected.tokens.toList()
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

        test("streamed-session-snapshots-survive-input-release") {
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

        test("a-hard-retention-limit-rejects-an-oversized-current-clause") {
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

        test("source-read-failures-preserve-their-cause") {
            val failure = IllegalStateException("broken source")
            val input = testLexer.lex(TextChunkSource { throw failure })
            val error = assertFailsWith<SourceReadException> { input.significantToken(0) }
            assertTrue(error.cause === failure)
        }

        test("suspending-sessions-read-and-parse-one-clause-at-a-time") {
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

        fun chunks(vararg texts: String): SuspendingTextChunkSource {
            val queue = ArrayDeque(texts.toList())
            return object : SuspendingTextChunkSource {
                var closed = false

                override suspend fun readChunk(): String? = queue.removeFirstOrNull()

                override suspend fun close() {
                    closed = true
                }

                override fun toString() = "closed=$closed"
            }
        }

        test("suspending-sessions-track-their-position") {
            val session = testParser.openSession(chunks("a.", " b."))
            assertEquals(0, session.currentPosition.offset)
            session.parseNextClause()
            assertEquals(2, session.currentPosition.offset)
        }

        test("suspending-sessions-end-on-trailing-trivia") {
            val session = testParser.openSession(chunks("a. % only a comment\n"))
            assertNotNull(session.parseNextClause())
            assertNull(session.parseNextClause())
            assertTrue(session.isAtEnd)
        }

        test("suspending-sessions-close-their-source") {
            val source = chunks("a.")
            testParser.openSession(source).close()
            assertEquals("closed=true", source.toString())
        }

        test("suspending-sessions-reject-a-clause-longer-than-the-retention-limit") {
            val session = testParser.openSession(chunks("f(a, b, c)."), maximumRetainedTokens = 3)
            assertFailsWith<TokenBufferLimitExceededException> { session.parseNextClause() }
            assertFailsWith<TokenBufferLimitExceededException> { session.parseNextClause() }
        }

        test("suspending-sessions-require-a-positive-retention-limit") {
            assertFailsWith<IllegalArgumentException> {
                testParser.openSession(
                    chunks("a."),
                    maximumRetainedTokens = 0,
                )
            }
        }

        test("suspending-sessions-wrap-read-failures") {
            val failure = IllegalStateException("broken source")
            val source =
                object : SuspendingTextChunkSource {
                    override suspend fun readChunk(): String? = throw failure
                }
            val error = assertFailsWith<SourceReadException> { testParser.openSession(source).parseNextClause() }
            assertTrue(error.cause === failure)
        }
    })
