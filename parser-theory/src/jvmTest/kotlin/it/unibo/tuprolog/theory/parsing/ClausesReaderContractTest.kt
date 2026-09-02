package it.unibo.tuprolog.theory.parsing

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.parsing.ParseException
import it.unibo.tuprolog.parser.exceptions.SourceReadException
import it.unibo.tuprolog.theory.Theory
import java.io.ByteArrayInputStream
import java.io.Reader
import java.io.StringReader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ClausesReaderContractTest {
    @Test
    fun readerFactoriesAndAllInputOverloadsDelegateCorrectly() {
        val customOperator = Operator("++", Specifier.YFX, 500)
        val custom = OperatorSet(customOperator)
        val reader = ClausesReader.withNoOperator()

        assertEquals(OperatorSet.EMPTY, reader.defaultOperatorSet)
        assertEquals(OperatorSet.STANDARD, ClausesReader.withStandardOperators().defaultOperatorSet)
        assertEquals(OperatorSet.DEFAULT, ClausesReader.withDefaultOperators().defaultOperatorSet)
        assertEquals(custom, ClausesReader.withOperators(custom).defaultOperatorSet)
        assertEquals(custom, ClausesReader.withOperators(customOperator).defaultOperatorSet)

        assertEquals("a", reader.readClauses(StringReader("a.")).singleHead().functor)
        assertEquals("a", reader.readClauses(stream("a.")).singleHead().functor)
        assertEquals("++", reader.readClauses(StringReader("a ++ b."), custom).singleHead().functor)
        assertEquals("++", reader.readClauses(stream("a ++ b."), custom).singleHead().functor)
        assertEquals("a", reader.readClausesLazily(StringReader("a.")).singleHead().functor)
        assertEquals("a", reader.readClausesLazily(stream("a.")).singleHead().functor)
        assertEquals("++", reader.readClausesLazily(StringReader("a ++ b."), custom).singleHead().functor)
        assertEquals("++", reader.readClausesLazily(stream("a ++ b."), custom).singleHead().functor)
        assertEquals(1L, reader.readTheory(StringReader("a.")).size)
        assertEquals(1L, reader.readTheory(stream("a.")).size)
        assertEquals("++", reader.readTheory(StringReader("a ++ b."), custom).singleHead().functor)
        assertEquals("++", reader.readTheory(stream("a ++ b."), custom).singleHead().functor)
    }

    @Test
    fun returnedSequenceDoesNotReadUntilItIsConsumed() {
        val source = GeneratedClauseReader(10)
        val clauses = ClausesReader.withNoOperator().readClausesLazily(source)

        assertEquals(0, source.generatedClauses)
        val iterator = clauses.iterator()
        assertEquals(0, source.generatedClauses)
        assertEquals("c", (iterator.next() as Fact).head.functor)
        assertTrue(source.generatedClauses in 1 until 10)
    }

    @Test
    fun sourceFailuresAreTranslatedWithoutLosingTheirCause() {
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

        val error = assertFailsWith<ParseException> { ClausesReader.withNoOperator().readClauses(reader) }
        val sourceError = assertIs<SourceReadException>(error.cause)
        assertSame(failure, sourceError.cause)
        assertEquals(0, error.clauseIndex)
        assertEquals(sourceError.span.start.line + 1, error.line)
        assertEquals(sourceError.span.start.column + 1, error.column)
        assertEquals(sourceError.offendingText, error.offendingSymbol)
        assertEquals(sourceError.message, error.message)
    }

    @Test
    fun parsesAVeryLongLazilyGeneratedInput() {
        val clauseCount = 50_000
        val source = GeneratedClauseReader(clauseCount)
        val clauses = ClausesReader.withNoOperator().readClausesLazily(source)

        var parsed = 0
        for (clause in clauses) {
            val head = (clause as Fact).head
            assertEquals("c", head.functor)
            assertEquals(parsed.toLong(), head[0].castToInteger().value.toLong())
            parsed++
        }

        assertEquals(clauseCount, parsed)
        assertEquals(clauseCount, source.generatedClauses)
    }

    private fun stream(text: String) = ByteArrayInputStream(text.encodeToByteArray())

    private fun Iterable<Clause>.singleHead(): Struct = (single() as Fact).head

    private fun Sequence<Clause>.singleHead(): Struct = (single() as Fact).head

    private fun Theory.singleHead(): Struct = (single() as Fact).head

    private class GeneratedClauseReader(
        private val clauseCount: Int,
    ) : Reader() {
        private var nextClause = 0
        private var pending = ""
        private var pendingOffset = 0

        var generatedClauses = 0
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
                pending = "c(${nextClause++}).\n"
                pendingOffset = 0
                generatedClauses++
            }
            val count = length.coerceAtMost(pending.length - pendingOffset)
            pending.toCharArray(target, offset, pendingOffset, pendingOffset + count)
            pendingOffset += count
            return count
        }

        override fun close() = Unit
    }
}
