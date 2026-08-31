package it.unibo.tuprolog.parser

import it.unibo.tuprolog.parser.tree.StructureNode
import java.io.Reader
import java.io.StringReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ReaderParsingTest {
    @Test
    fun readerLexingIsLazy() {
        val reader = CountingReader("first. second.")
        val input = PrologLexer.default().lex(reader.toSource(chunkSize = 1))

        assertEquals(0, reader.charactersRead)
        assertEquals("first", input.textOf(input.significantToken(0)))
        assertTrue(reader.charactersRead < reader.textLength)
    }

    @Test
    fun readerSessionsDoNotLoadTheWholeFile() {
        for (autoClose in listOf(false, true)) {
            val reader = CountingReader("first.\nsecond.\nthird.")
            val session =
                buildParserFor(reader, chunkSize = 1, autoClose = autoClose) { parser, lexedSource ->
                    parser.openSession(lexedSource)
                }

            assertEquals(0, reader.charactersRead)
            val first = assertNotNull(session.parseNextClause())
            assertTrue(reader.charactersRead < reader.textLength)
            assertEquals("first", (first.root.expression as StructureNode).functor)

            assertNotNull(session.parseNextClause())
            assertNotNull(session.parseNextClause())
            assertTrue(session.isAtEnd)
            assertEquals(reader.textLength, reader.charactersRead)
            assertEquals(autoClose, reader.closed)
        }
    }

    @Test
    fun parserConvenienceFunctionsAcceptReaders() {
        val reader = StringReader("a.\nb.")
        val tree =
            buildParserFor(reader) { parser, lexedSource ->
                parser.parseTheory(lexedSource)
            }
        assertEquals(2, tree.root.clauses.size)
        assertEquals("a.\nb.", tree.source.text(tree.source.start.offset, tree.source.endExclusive.offset))
    }

    private class CountingReader(
        private val input: String,
    ) : Reader() {
        var charactersRead: Int = 0
            private set
        var closed: Boolean = false
            private set

        val textLength: Int
            get() = input.length

        override fun read(
            target: CharArray,
            offset: Int,
            length: Int,
        ): Int {
            if (charactersRead == input.length) {
                return -1
            }
            val count = length.coerceAtMost(input.length - charactersRead)
            input.toCharArray(target, offset, charactersRead, charactersRead + count)
            charactersRead += count
            return count
        }

        override fun close() {
            closed = true
        }
    }
}
