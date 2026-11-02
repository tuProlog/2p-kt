package it.unibo.tuprolog.parser

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.parser.tree.StructureNode
import java.io.Reader
import java.io.StringReader

class ReaderParsingSpec :
    FunSpec({
        test("reader-lexing-is-lazy") {
            val reader = CountingReader("first. second.")
            val input = PrologLexer.default().lex(reader.toSource(chunkSize = 1))

            reader.charactersRead shouldBe 0
            input.textOf(input.significantToken(0)) shouldBe "first"
            (reader.charactersRead < reader.textLength).shouldBeTrue()
        }

        withTests(
            nameFn = { "reader-sessions-do-not-load-the-whole-file/${if (it) "auto-close" else "no-auto-close"}" },
            listOf(false, true),
        ) { autoClose ->
            val reader = CountingReader("first.\nsecond.\nthird.")
            val session =
                buildParserFor(reader, chunkSize = 1, autoClose = autoClose) { parser, lexedSource ->
                    parser.openSession(lexedSource)
                }

            reader.charactersRead shouldBe 0
            val first = session.parseNextClause().shouldNotBeNull()
            (reader.charactersRead < reader.textLength).shouldBeTrue()
            (first.root.expression as StructureNode).functor shouldBe "first"

            session.parseNextClause().shouldNotBeNull()
            session.parseNextClause().shouldNotBeNull()
            session.isAtEnd.shouldBeTrue()
            reader.charactersRead shouldBe reader.textLength
            reader.closed shouldBe autoClose
        }

        test("parser-convenience-functions-accept-readers") {
            val reader = StringReader("a.\nb.")
            val tree =
                buildParserFor(reader) { parser, lexedSource ->
                    parser.parseTheory(lexedSource)
                }
            tree.root.clauses.size shouldBe 2
            tree.source.text(tree.source.start.offset, tree.source.endExclusive.offset) shouldBe "a.\nb."
        }
    })

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
