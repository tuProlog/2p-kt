package it.unibo.tuprolog.collections.prototypes

import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.ClauseDeque
import it.unibo.tuprolog.collections.PrototypeClauseDequeTest
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.testutils.ClauseAssertionUtils
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertTermsAreEqual
import kotlin.test.assertEquals

internal class PrototypeClauseDequeTestImpl (
    private val emptyGenerator: () -> ClauseDeque,
    private val collectionGenerator: (Iterable<Clause>) -> ClauseDeque
) : PrototypeClauseDequeTest,
    PrototypeClauseCollectionTestImpl(emptyGenerator, collectionGenerator) {

    private val fFamilySelector =
        Fact.of(Struct.of("f", Var.anonymous()))

    private val presentClause =
        Fact.of(Struct.of("f", Atom.of("a")))

    private val newClause =
        Fact.of(Struct.of("f", Atom.of("d")))

    private val absentClause =
        Fact.of(Struct.of("f", Atom.of("z")))

    private val clauses =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("c")))
        )

    private val newClauses =
        listOf(
            Fact.of(Struct.of("f", Atom.of("d"))),
            Fact.of(Struct.of("f", Atom.of("e"))),
            Fact.of(Struct.of("f", Atom.of("f")))
        )

    private val orderSensitiveClauses =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("g", Numeric.of(1))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("g", Numeric.of(2))),
            Fact.of(Struct.of("f", Atom.of("c"))),
            Fact.of(Struct.of("g", Numeric.of(3)))
        )

    private val numericClauses =
        listOf(
            Fact.of(Struct.of("g", Numeric.of(1))),
            Fact.of(Struct.of("g", Numeric.of(2))),
            Fact.of(Struct.of("g", Numeric.of(3)))
        )

    private val emptyCollection = emptyGenerator()

    override fun getWithPresentClauseReturnsTheCorrectSequence() {
        val sequence = collectionGenerator(clauses + presentClause)[presentClause]

        assertClausesHaveSameLengthAndContent(
            listOf(presentClause, presentClause),
            sequence.toList()
        )
    }

    override fun getWithAbsentClauseReturnsAnEmptySequence() {
        val result = collectionGenerator(clauses)[absentClause]

        assertEquals(emptyList<Clause>(), result.toList())
    }

    override fun addFirstPrependsElement() {
        val prependedCollectionSnapshot = collectionGenerator(clauses).addFirst(newClause)

        assertClausesHaveSameLengthAndContent(
            collectionGenerator(listOf(newClause) + clauses),
            prependedCollectionSnapshot
        )
    }

    override fun addLastAppendsElement() {
        val appendedCollectionSnapshot = collectionGenerator(clauses).addLast(newClause)

        assertClausesHaveSameLengthAndContent(
            collectionGenerator(clauses + listOf(newClause)),
            appendedCollectionSnapshot
        )
    }

    override fun getFirstRetrievesElementsFromFirstPosition() {
        val result = collectionGenerator(orderSensitiveClauses).getFirst(fFamilySelector)

        assertClausesHaveSameLengthAndContent(clauses, result.asIterable())
    }

    override fun getLastRetrievesElementsFromLastPosition() {
        val result = collectionGenerator(orderSensitiveClauses).getLast(fFamilySelector)

        assertClausesHaveSameLengthAndContent(clauses.asReversed(), result.asIterable())
    }

    override fun simpleAddBehavesAsAddLast() {
        val appendedCollectionSnapshot = collectionGenerator(clauses).add(newClause)

        assertClausesHaveSameLengthAndContent(
            collectionGenerator(clauses + listOf(newClause)),
            appendedCollectionSnapshot
        )
    }

    override fun simpleGetBehavesAsGetFirst() {
        val result = collectionGenerator(orderSensitiveClauses).get(fFamilySelector)

        assertClausesHaveSameLengthAndContent(clauses, result.asIterable())
    }

    override fun retrieveFirstRemovesTheFirstUnifyingElement() {
        val result =
            collectionGenerator(clauses).retrieveFirst(presentClause) as RetrieveResult.Success

        assertTermsAreEqual(presentClause, result.firstClause)
        assertClausesHaveSameLengthAndContent(clauses - listOf(presentClause), result.collection)
    }

    override fun retrieveLastRemovesTheLastUnifyingElement() {
        TODO("Not yet implemented")
    }

    override fun simpleRetrieveBehavesAsRetrieveFirst() {
        val result =
            collectionGenerator(clauses).retrieve(presentClause) as RetrieveResult.Success

        assertTermsAreEqual(presentClause, result.firstClause)
        assertClausesHaveSameLengthAndContent(clauses - listOf(presentClause), result.collection)
    }

}