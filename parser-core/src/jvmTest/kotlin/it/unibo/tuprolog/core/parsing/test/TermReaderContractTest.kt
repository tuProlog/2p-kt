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
}
