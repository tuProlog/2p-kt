package it.unibo.tuprolog.core.parsing.test

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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame

class TermReaderParseExceptionWrappingTest {
    @Test
    fun lexicalErrorsFromReadersAndStreamsAreWrapped() {
        val readerError =
            assertFailsWith<ParseException> {
                TermReader.withDefaultOperators().readTerms(StringReader("ok.\n'\\q'.")).toList()
            }
        assertWrapper<InvalidEscapeException>(readerError, line = 2, column = 2, offending = "\\q")
        assertEquals("ok.\n'\\q'.", readerError.input)

        val streamError =
            assertFailsWith<ParseException> {
                TermReader
                    .withDefaultOperators()
                    .readTerms(ByteArrayInputStream("f().".encodeToByteArray()))
                    .toList()
            }
        assertWrapper<UnexpectedTokenException>(streamError, line = 1, column = 3, offending = ")")
        assertEquals("f().", streamError.input)
    }

    @Test
    fun lazyReaderParsingWrapsOnlyWhenTheInvalidTermIsRequested() {
        val iterator = TermReader.withDefaultOperators().readTerms(StringReader("first. f().")).iterator()

        assertEquals("first", assertIs<Struct>(iterator.next()).functor)
        val wrapper = assertFailsWith<ParseException> { iterator.hasNext() }
        assertWrapper<UnexpectedTokenException>(wrapper, line = 1, column = 10, offending = ")")
    }

    @Test
    fun sourceReadFailuresAreWrappedAndRetainTheOriginalCause() {
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

        val wrapper = assertFailsWith<ParseException> { TermReader.withNoOperator().readTerms(reader).toList() }
        val sourceError = assertIs<SourceReadException>(wrapper.cause)
        assertSame(failure, sourceError.cause)
        assertEquals("", wrapper.input)
        assertEquals(1, wrapper.line)
        assertEquals(1, wrapper.column)
        assertEquals(sourceError.offendingText, wrapper.offendingSymbol)
        assertEquals(sourceError.message, wrapper.message)
    }

    @Test
    fun configuredTokenBufferFailuresAreWrapped() {
        val reader =
            TermReaderImpl(
                Scope.empty(),
                OperatorSet.DEFAULT,
                LexerOptions(
                    retention = TokenRetention.RELEASE_COMMITTED,
                    maximumRetainedTokens = 3,
                ),
            )

        val wrapper = assertFailsWith<ParseException> { reader.readTerms("f(a, b, c).").toList() }
        assertWrapper<TokenBufferLimitExceededException>(wrapper, line = 1, column = 4, offending = null)
    }

    private inline fun <reified T : Throwable> assertWrapper(
        wrapper: ParseException,
        line: Int,
        column: Int,
        offending: String?,
    ) {
        val cause = assertIs<T>(wrapper.cause)
        assertEquals(line, wrapper.line)
        assertEquals(column, wrapper.column)
        assertEquals(offending, wrapper.offendingSymbol)
        assertEquals(cause.message, wrapper.message)
    }
}
