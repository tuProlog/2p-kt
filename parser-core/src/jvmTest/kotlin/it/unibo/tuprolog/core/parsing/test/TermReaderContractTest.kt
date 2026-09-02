package it.unibo.tuprolog.core.parsing.test

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.operators.Operator
import it.unibo.tuprolog.core.operators.OperatorSet
import it.unibo.tuprolog.core.operators.Specifier
import it.unibo.tuprolog.core.parsing.TermReader
import java.io.Reader
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class TermReaderContractTest {
    @Test
    fun readsMultipleTermsFromOneReader() {
        val terms = TermReader.withNoOperator().readTerms("first. second. third").toList()
        assertEquals(listOf("first", "second", "third"), terms.map { assertIs<Struct>(it).functor })
    }

    @Test
    fun customOperatorsArePropagatedToTheParseSession() {
        val operators = OperatorSet(Operator("++", Specifier.YFX, 500))
        val term = TermReader.withNoOperator().readTerms("a ++ b.", operators).single()
        val structure = assertIs<Struct>(term)
        assertEquals("++", structure.functor)
        assertEquals(2, structure.arity)
    }

    @Test
    fun returnedSequenceDoesNotReadBeforeIteration() {
        val reader = CountingReader("first. second.")
        val terms = TermReader.withNoOperator().readTerms(reader)
        assertEquals(0, reader.charactersRead)

        val iterator = terms.iterator()
        assertEquals(0, reader.charactersRead)
        assertEquals("first", assertIs<Struct>(iterator.next()).functor)
        assertEquals(true, reader.charactersRead > 0)
    }

    @Test
    fun readsAVeryLongLazilyGeneratedInput() {
        val termCount = 50_000
        val reader = GeneratedTermReader(termCount)
        val terms = TermReader.withNoOperator().readTerms(reader)

        var parsed = 0
        for (term in terms) {
            val structure = assertIs<Struct>(term)
            assertEquals("item", structure.functor)
            assertEquals(parsed.toString(), structure[0].toString())
            parsed++
        }

        assertEquals(termCount, parsed)
        assertEquals(termCount, reader.generatedTerms)
    }

    private class CountingReader(
        private val input: String,
    ) : Reader() {
        var charactersRead: Int = 0
            private set

        override fun read(
            target: CharArray,
            offset: Int,
            length: Int,
        ): Int {
            if (charactersRead >= input.length) return -1
            val count = length.coerceAtMost(input.length - charactersRead)
            input.toCharArray(target, offset, charactersRead, charactersRead + count)
            charactersRead += count
            return count
        }

        override fun close() = Unit
    }

    private class GeneratedTermReader(
        private val termCount: Int,
    ) : Reader() {
        private var nextTerm = 0
        private var pending = ""
        private var pendingOffset = 0

        var generatedTerms = 0
            private set

        @Suppress("ReturnCount")
        override fun read(
            target: CharArray,
            offset: Int,
            length: Int,
        ): Int {
            if (length == 0) return 0
            if (pendingOffset == pending.length) {
                if (nextTerm == termCount) return -1
                pending = "item(${nextTerm++}).\n"
                pendingOffset = 0
                generatedTerms++
            }
            val count = length.coerceAtMost(pending.length - pendingOffset)
            pending.toCharArray(target, offset, pendingOffset, pendingOffset + count)
            pendingOffset += count
            return count
        }

        override fun close() = Unit
    }
}
