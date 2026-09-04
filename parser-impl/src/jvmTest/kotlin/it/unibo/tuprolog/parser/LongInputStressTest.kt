package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.tokens.TokenKind
import java.io.Reader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LongInputStressTest {
    @Test
    fun streamedSessionParsesVeryLongGeneratedInputWithBoundedRetention() {
        val clauseCount = 100_000
        val source = GeneratedClauseReader(clauseCount)
        val lexed =
            PrologLexer.default().lex(
                source.toSource(chunkSize = 32, autoClose = true),
                options = LexerOptions(retention = TokenRetention.RELEASE_COMMITTED),
            )
        val session = PrologParser.default().openSession(lexed)

        repeat(clauseCount) { expected ->
            val clause = assertNotNull(session.parseNextClause())
            val expression = clause.root.expression
            assertEquals(TokenKind.WORD_ATOM, clause.tokens[expression.tokenRange.startInclusive].kind)
            assertEquals("item($expected).", clause.source.text(clause.root.span))
            assertTrue(lexed.tokens.retainedCount <= 1)
        }

        assertEquals(null, session.parseNextClause())
        assertEquals(clauseCount, source.generatedClauses)
        assertTrue(source.closed)
    }

    private class GeneratedClauseReader(
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
}
