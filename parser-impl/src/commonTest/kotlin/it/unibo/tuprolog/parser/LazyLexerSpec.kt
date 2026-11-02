package it.unibo.tuprolog.parser

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.parser.exceptions.SourceReadException
import it.unibo.tuprolog.parser.exceptions.TokenBufferLimitExceededException
import it.unibo.tuprolog.parser.exceptions.UnterminatedQuotedLiteralException
import it.unibo.tuprolog.parser.sources.SourceText
import it.unibo.tuprolog.parser.tokens.TokenKind
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

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

            reads shouldBe 0
            source.significantToken(0).kind shouldBe TokenKind.WORD_ATOM
            reads shouldBe 1
        }

        test("lexical-failures-are-deferred-until-their-token-is-requested") {
            val source = testLexer.lex(SourceText("'unterminated"))
            shouldThrow<UnterminatedQuotedLiteralException> { source.significantToken(0) }
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

            withClue("chunk size $chunkSize") { actual.tokens.toList() shouldBe expectedTokens }
            withClue("chunk size $chunkSize") {
                actual.tokens.toList().map(actual::textOf) shouldBe
                    expectedTokens.map(expected::textOf)
            }
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

            val first = session.parseNextClause().shouldNotBeNull()
            val firstRootText = first.source.text(first.root.span)
            (input.tokens.retainedCount <= 1).shouldBeTrue()
            val second = session.parseNextClause().shouldNotBeNull()
            val third = session.parseNextClause().shouldNotBeNull()

            firstRootText shouldBe "first."
            first.source.text(first.root.span) shouldBe "first."
            second.source.text(second.root.span) shouldBe "second."
            third.source.text(third.root.span) shouldBe "third."
            (second.tokens.firstTokenId > first.tokens.firstTokenId).shouldBeTrue()
            second.tokens[second.root.expression.tokenRange.startInclusive].kind shouldBe TokenKind.WORD_ATOM
            session.isAtEnd.shouldBeTrue()
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
            shouldThrow<TokenBufferLimitExceededException> { testParser.openSession(input).parseNextClause() }
        }

        test("source-read-failures-preserve-their-cause") {
            val failure = IllegalStateException("broken source")
            val input = testLexer.lex(TextChunkSource { throw failure })
            val error = shouldThrow<SourceReadException> { input.significantToken(0) }
            error.cause shouldBeSameInstanceAs failure
        }

        test("suspending-sessions-read-and-parse-one-clause-at-a-time") {
            val chunks = ArrayDeque(listOf("first", ".\nsec", "ond."))
            val source =
                object : SuspendingTextChunkSource {
                    override suspend fun readChunk(): String? = chunks.removeFirstOrNull()
                }
            val session = testParser.openSession(source, sourceId = "async.pl")

            val first = runSuspend { session.parseNextClause() }
            first.shouldNotBeNull()
            session.isAtEnd.shouldBeFalse()
            val second = runSuspend { session.parseNextClause() }
            second.shouldNotBeNull()
            first.source.text(first.root.span) shouldBe "first."
            second.source.text(second.root.span) shouldBe "second."
            (runSuspend { session.parseNextClause() }).shouldBeNull()
            session.isAtEnd.shouldBeTrue()
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
            session.currentPosition.offset shouldBe 0
            session.parseNextClause()
            session.currentPosition.offset shouldBe 2
        }

        test("suspending-sessions-end-on-trailing-trivia") {
            val session = testParser.openSession(chunks("a. % only a comment\n"))
            session.parseNextClause().shouldNotBeNull()
            session.parseNextClause().shouldBeNull()
            session.isAtEnd.shouldBeTrue()
        }

        test("suspending-sessions-close-their-source") {
            val source = chunks("a.")
            testParser.openSession(source).close()
            source.toString() shouldBe "closed=true"
        }

        test("suspending-sessions-reject-a-clause-longer-than-the-retention-limit") {
            val session = testParser.openSession(chunks("f(a, b, c)."), maximumRetainedTokens = 3)
            shouldThrow<TokenBufferLimitExceededException> { session.parseNextClause() }
            shouldThrow<TokenBufferLimitExceededException> { session.parseNextClause() }
        }

        test("suspending-sessions-require-a-positive-retention-limit") {
            shouldThrow<IllegalArgumentException> {
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
            val error = shouldThrow<SourceReadException> { testParser.openSession(source).parseNextClause() }
            error.cause shouldBeSameInstanceAs failure
        }
    })
