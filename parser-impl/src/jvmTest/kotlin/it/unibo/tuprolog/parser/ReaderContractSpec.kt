package it.unibo.tuprolog.parser

import io.kotest.core.spec.style.FunSpec
import java.io.Reader
import java.io.StringReader
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ReaderContractSpec :
    FunSpec({
        class ZeroThenDataReader(
            private val text: String,
        ) : Reader() {
            private var first = true
            private var offset = 0

            @Suppress("ReturnCount")
            override fun read(
                target: CharArray,
                offset: Int,
                length: Int,
            ): Int {
                if (first) {
                    first = false
                    return 0
                }
                if (this.offset >= text.length) return -1
                val count = length.coerceAtMost(text.length - this.offset)
                text.toCharArray(target, offset, this.offset, this.offset + count)
                this.offset += count
                return count
            }

            override fun close() = Unit
        }

        test("non-positive-chunk-sizes-are-rejected-immediately") {
            assertFailsWith<IllegalArgumentException> { StringReader("a").toSource(chunkSize = 0) }
            assertFailsWith<IllegalArgumentException> { StringReader("a").toSource(chunkSize = -1) }
        }

        test("auto-close-controls-reader-ownership") {
            for (autoClose in listOf(false, true)) {
                val reader = TrackingReader("a.")
                PrologLexer.default().lex(reader.toSource(chunkSize = 1, autoClose = autoClose)).materialize()
                assertEquals(autoClose, reader.closed)
                assertEquals(if (autoClose) 1 else 0, reader.closeCount)
            }
        }

        test("empty-intermediate-reads-do-not-prematurely-end-the-source") {
            val reader = ZeroThenDataReader("abc")
            val input = PrologLexer.default().lex(reader.toSource(chunkSize = 2, autoClose = false))
            assertEquals("abc", input.textOf(input.significantToken(0)))
        }
    })

private class TrackingReader(
    private val text: String,
) : Reader() {
    private var offset = 0
    var closeCount = 0
        private set
    val closed: Boolean
        get() = closeCount > 0

    override fun read(
        target: CharArray,
        offset: Int,
        length: Int,
    ): Int {
        if (this.offset >= text.length) return -1
        val count = length.coerceAtMost(text.length - this.offset)
        text.toCharArray(target, offset, this.offset, this.offset + count)
        this.offset += count
        return count
    }

    override fun close() {
        closeCount++
    }
}
