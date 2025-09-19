package it.unibo.tuprolog.collections.prototypes

import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.PrototypeClauseMultiSetTest
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import it.unibo.tuprolog.utils.permutations
import kotlin.test.assertEquals

internal class PrototypeClauseMultiSetTestImpl(
    private val emptyGenerator: () -> ClauseMultiSet,
    private val collectionGenerator: (Iterable<Clause>) -> ClauseMultiSet,
) : PrototypeClauseCollectionTestImpl(emptyGenerator, collectionGenerator),
    PrototypeClauseMultiSetTest {
    private val presentClause =
        Fact.of(Struct.of("f", Atom.of("a")))

    private val absentClause =
        Fact.of(Struct.of("f", Atom.of("z")))

    private val clauses =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("c"))),
        )

    private val emptyCollection = emptyGenerator()

    override fun getClauses(
        collection: ClauseCollection,
        query: Clause,
    ): Sequence<Clause> = (collection as ClauseMultiSet).get(query)

    override fun retractClauses(
        collection: ClauseCollection,
        query: Clause,
    ): Sequence<Clause> =
        when (val res = (collection as ClauseMultiSet).retrieve(query)) {
            is RetrieveResult.Success -> res.clauses.asSequence()
            else -> emptySequence()
        }

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

        assertEquals(emptyList(), result.toList())
    }

    override fun equalsIsOrderIndependent() {
        val actualClauses = clauses + clauses[0]

        val tester = collectionGenerator(actualClauses)

        val perm = actualClauses.permutations().iterator()

        assertEquals(tester, collectionGenerator(perm.next()))

        while (perm.hasNext()) {
            assertEquals(tester, collectionGenerator(perm.next()))
        }
    }

    override fun hashCodeIsOrderIndependent() {
        val actualClauses = clauses + clauses[0]

        val permutations = actualClauses.permutations().toList()

        assertEquals(
            1,
            permutations.map { collectionGenerator(it) }.toSet().size,
        )
    }
}
