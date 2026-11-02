package it.unibo.tuprolog.core.parsing.test

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.kotest.matchers.types.shouldBeSameInstanceAs
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.core.parsing.TermReader
import it.unibo.tuprolog.core.parsing.TermReaderImpl
import it.unibo.tuprolog.parser.LexerOptions
import it.unibo.tuprolog.parser.TokenRetention
import it.unibo.tuprolog.parser.exceptions.InvalidEscapeException
import it.unibo.tuprolog.parser.exceptions.SourceReadException
import it.unibo.tuprolog.parser.exceptions.TokenBufferLimitExceededException
import it.unibo.tuprolog.parser.exceptions.UnexpectedTokenException
import java.io.ByteArrayInputStream
import java.io.Reader
import java.io.StringReader

class TermReaderParseExceptionWrappingSpec :
    FunSpec({
        test("lexical-errors-from-readers-and-streams-are-wrapped") {
            val readerError =
                shouldThrow<ParseException> {
                    TermReader.withDefaultOperators().readTerms(StringReader("ok.\n'\\q'.")).toList()
                }
            assertWrapper<InvalidEscapeException>(readerError, line = 2, column = 2, offending = "\\q")
            readerError.input shouldBe "ok.\n'\\q'."

            val streamError =
                shouldThrow<ParseException> {
                    TermReader
                        .withDefaultOperators()
                        .readTerms(ByteArrayInputStream("f().".encodeToByteArray()))
                        .toList()
                }
            assertWrapper<UnexpectedTokenException>(streamError, line = 1, column = 3, offending = ")")
            streamError.input shouldBe "f()."
        }

        test("lazy-reader-parsing-wraps-only-when-the-invalid-term-is-requested") {
            val iterator = TermReader.withDefaultOperators().readTerms(StringReader("first. f().")).iterator()

            (iterator.next().shouldBeInstanceOf<Struct>().functor) shouldBe "first"
            val wrapper = shouldThrow<ParseException> { iterator.hasNext() }
            assertWrapper<UnexpectedTokenException>(wrapper, line = 1, column = 10, offending = ")")
        }

        test("source-read-failures-are-wrapped-and-retain-the-original-cause") {
            val failure = IllegalStateException("reader failed")
            val reader =
                object : Reader() {
                    override fun read(
                        target: CharArray,
                        offset: Int,
                        length: Int,
                    ): Int = throw failure

                    override fun close() = Unit
                }

            val wrapper = shouldThrow<ParseException> { TermReader.withNoOperator().readTerms(reader).toList() }
            val sourceError = wrapper.cause.shouldBeInstanceOf<SourceReadException>()
            sourceError.cause shouldBeSameInstanceAs failure
            wrapper.input shouldBe ""
            wrapper.line shouldBe 1
            wrapper.column shouldBe 1
            wrapper.offendingSymbol shouldBe sourceError.offendingText
            wrapper.message shouldBe sourceError.message
        }

        test("configured-token-buffer-failures-are-wrapped") {
            val reader =
                TermReaderImpl(
                    Scope.empty(),
                    OperatorSet.DEFAULT,
                    LexerOptions(
                        retention = TokenRetention.RELEASE_COMMITTED,
                        maximumRetainedTokens = 3,
                    ),
                )

            val wrapper = shouldThrow<ParseException> { reader.readTerms("f(a, b, c).").toList() }
            assertWrapper<TokenBufferLimitExceededException>(wrapper, line = 1, column = 4, offending = null)
        }
    })

private inline fun <reified T : Throwable> assertWrapper(
    wrapper: ParseException,
    line: Int,
    column: Int,
    offending: String?,
) {
    val cause = wrapper.cause.shouldBeInstanceOf<T>()
    wrapper.line shouldBe line
    wrapper.column shouldBe column
    wrapper.offendingSymbol shouldBe offending
    wrapper.message shouldBe cause.message
}
