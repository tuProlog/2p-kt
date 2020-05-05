package it.unibo.tuprolog.collections.prototypes

import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.PrototypeClauseMultiSetTest
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.testutils.ClauseAssertionUtils
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import kotlin.test.assertEquals

internal class PrototypeClauseMultiSetTestImpl (
    private val emptyGenerator: () -> ClauseMultiSet,
    private val collectionGenerator: (Iterable<Clause>) -> ClauseMultiSet
) : PrototypeClauseMultiSetTest,
    PrototypeClauseCollectionTestImpl(emptyGenerator, collectionGenerator) {

    private val presentClause =
        Fact.of(Struct.of("f", Atom.of("a")))

    private val absentClause =
        Fact.of(Struct.of("f", Atom.of("z")))

    private val clauses =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("c")))
        )

    private val emptyCollection = emptyGenerator()

    override fun countingOnPresentClauseAnswerTheRightNumber() {
        val count = collectionGenerator(clauses).count(presentClause)
        val count2 = collectionGenerator(clauses + presentClause).count(presentClause)

        assertEquals(1, count)
        assertEquals(2, count2)
    }

    override fun countingOnAbsentClauseAnswerZero() {
        val count = collectionGenerator(clauses).count(absentClause)
        val count2 = emptyCollection.count(absentClause)

        assertEquals(0, count)
        assertEquals(0, count2)
    }

    override fun getWithPresentClauseReturnsTheCorrectSequence() {
        val sequence = collectionGenerator(clauses + presentClause)[presentClause]

        assertClausesHaveSameLengthAndContent(listOf(presentClause, presentClause), sequence.toList())
    }

    override fun getWithAbsentClauseReturnsAnEmptySequence() {
        val result = collectionGenerator(clauses)[absentClause]

        assertEquals(emptyList<Clause>(), result.toList())
    }
}