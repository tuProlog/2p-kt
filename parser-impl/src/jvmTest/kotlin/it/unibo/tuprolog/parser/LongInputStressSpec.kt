package it.unibo.tuprolog.parser

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.parser.tokens.TokenKind
import java.io.Reader

class LongInputStressSpec :
    FunSpec({
        class GeneratedClauseReader(
            private val clauseCount: Int,
        ) : Reader() {
            private var nextClause = 0
            private var pending = ""
            private var pendingOffset = 0
            var generatedClauses = 0
                private set
            var closed = false
                private set

            @Suppress("ReturnCount")
            override fun read(
                target: CharArray,
                offset: Int,
                length: Int,
            ): Int {
                if (length == 0) return 0
                if (pendingOffset == pending.length) {
                    if (nextClause == clauseCount) return -1
                    pending = "item(${nextClause++}).\n"
                    pendingOffset = 0
                    generatedClauses++
                }
                val count = length.coerceAtMost(pending.length - pendingOffset)
                pending.toCharArray(target, offset, pendingOffset, pendingOffset + count)
                pendingOffset += count
                return count
            }

            override fun close() {
                closed = true
            }
        }

        test("streamed-session-parses-very-long-generated-input-with-bounded-retention") {
            val clauseCount = 100_000
            val source = GeneratedClauseReader(clauseCount)
            val lexed =
                PrologLexer.default().lex(
                    source.toSource(chunkSize = 32, autoClose = true),
                    options = LexerOptions(retention = TokenRetention.RELEASE_COMMITTED),
                )
            val session = PrologParser.default().openSession(lexed)

            repeat(clauseCount) { expected ->
                val clause = session.parseNextClause().shouldNotBeNull()
                val expression = clause.root.expression
                clause.tokens[expression.tokenRange.startInclusive].kind shouldBe TokenKind.WORD_ATOM
                clause.source.text(clause.root.span) shouldBe "item($expected)."
                (lexed.tokens.retainedCount <= 1).shouldBeTrue()
            }

            session.parseNextClause() shouldBe null
            source.generatedClauses shouldBe clauseCount
            source.closed.shouldBeTrue()
        }
    })
