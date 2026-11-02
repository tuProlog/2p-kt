package it.unibo.tuprolog.parser

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.shouldBe
import java.io.Reader
import java.io.StringReader

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
            shouldThrow<IllegalArgumentException> { StringReader("a").toSource(chunkSize = 0) }
            shouldThrow<IllegalArgumentException> { StringReader("a").toSource(chunkSize = -1) }
        }

        withTests(
            nameFn = { "auto-close-controls-reader-ownership/${if (it) "auto-close" else "no-auto-close"}" },
            listOf(false, true),
        ) { autoClose ->
            val reader = TrackingReader("a.")
            PrologLexer.default().lex(reader.toSource(chunkSize = 1, autoClose = autoClose)).materialize()
            reader.closed shouldBe autoClose
            reader.closeCount shouldBe (if (autoClose) 1 else 0)
        }

        test("empty-intermediate-reads-do-not-prematurely-end-the-source") {
            val reader = ZeroThenDataReader("abc")
            val input = PrologLexer.default().lex(reader.toSource(chunkSize = 2, autoClose = false))
            input.textOf(input.significantToken(0)) shouldBe "abc"
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
