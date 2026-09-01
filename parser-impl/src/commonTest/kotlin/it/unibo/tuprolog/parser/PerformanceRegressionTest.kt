package it.unibo.tuprolog.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PerformanceRegressionTest {
    @Test
    fun releaseCommittedRetainsFarFewerTokensThanKeepAllInStreamingSessions() {
        val text =
            buildString {
                repeat(250) {
                    append("f(a,b,c,d,e).")
                    append('\n')
                }
            }

        fun parseAndCollect(retention: TokenRetention): Pair<Int, Int> {
            var offset = 0
            val input =
                testLexer.lex(
                    TextChunkSource {
                        if (offset == text.length) {
                            null
                        } else {
                            text.substring(offset, ++offset)
                        }
                    },
                    options = LexerOptions(retention = retention),
                )
            val session = testParser.openSession(input)
            var parsedClauses = 0
            var maxRetained = 0
            while (true) {
                val clause = session.parseNextClause() ?: break
                parsedClauses += 1
                maxRetained = maxRetained.coerceAtLeast(input.tokens.retainedCount)
                assertEquals("f(a,b,c,d,e).", clause.source.text(clause.root.span))
            }
            assertEquals(250, parsedClauses)
            return maxRetained to input.tokens.retainedCount
        }

        val (releaseMax, releaseFinal) = parseAndCollect(TokenRetention.RELEASE_COMMITTED)
        val (keepMax, keepFinal) = parseAndCollect(TokenRetention.KEEP_ALL)

        assertTrue(releaseMax < keepMax)
        assertTrue(releaseFinal < keepFinal)
        assertTrue(releaseMax <= 64, "Expected low retained-token peak in RELEASE_COMMITTED mode")
    }

    @Test
    fun incrementalSingleCharInputRequiresOnlyLinearChunkReads() {
        val text =
            buildString {
                repeat(150) {
                    append("x(")
                    append(it)
                    append(").\n")
                }
            }
        var reads = 0
        var offset = 0
        val input =
            testLexer.lex(
                TextChunkSource {
                    reads += 1
                    if (offset == text.length) {
                        null
                    } else {
                        text.substring(offset, ++offset)
                    }
                },
                options = LexerOptions(retention = TokenRetention.RELEASE_COMMITTED),
            )
        val session = testParser.openSession(input)

        var parsedClauses = 0
        while (session.parseNextClause() != null) {
            parsedClauses += 1
        }

        assertEquals(150, parsedClauses)
        assertTrue(reads in (text.length + 1)..(text.length + 2))
    }
}
