package it.unibo.tuprolog.collections.prototypes

import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.PrototypeClauseCollectionTest
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertTermsAreEqual
import kotlin.test.*

internal abstract class PrototypeClauseCollectionTestImpl(
    private val emptyGenerator: () -> ClauseCollection,
    private val collectionGenerator: (Iterable<Clause>) -> ClauseCollection
) : PrototypeClauseCollectionTest {

    private val member = Scope.empty {
        factOf(structOf("member", varOf("H"), consOf(varOf("H"), anonymous())))
    }

    private val deep =
        Fact.of(
            LogicList.of(
                LogicList.of(
                    LogicList.of(
                        Atom.of("a"),
                        Atom.of("b")
                    ),
                    Atom.of("c")
                ),
                Atom.of("d")
            )
        )

    private val deepQueries = sequenceOf(
        LogicList.of(Var.of("ABC"), Var.of("D")),
        LogicList.of(LogicList.of(Var.of("AB"), Var.of("C")), Var.of("D")),
        LogicList.of(LogicList.of(LogicList.of(Var.of("A"), Var.of("B")), Var.of("C")), Var.of("D"))
    ).map { Fact.of(it) }.toList()

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

    private val emptyCollection = emptyGenerator()

    protected abstract fun getClauses(collection: ClauseCollection, query: Clause): Sequence<Clause>

    protected abstract fun retractClauses(collection: ClauseCollection, query: Clause): Sequence<Clause>

    override fun getTakesUnificationIntoAccount() {
        val collection = collectionGenerator(listOf(member))

        assertClausesHaveSameLengthAndContent(
            sequenceOf(member),
            getClauses(collection, Fact.of(Struct.of("member", Atom.of("a"), LogicList.of(Atom.of("a")))))
        )
        assertClausesHaveSameLengthAndContent(
            sequenceOf(),
            getClauses(collection, Fact.of(Struct.of("member", Atom.of("a"), LogicList.of(Atom.of("b")))))
        )
    }

    override fun retractTakesUnificationIntoAccount() {
        var collection = collectionGenerator(listOf(member))

        assertClausesHaveSameLengthAndContent(
            sequenceOf(member),
            retractClauses(collection, Fact.of(Struct.of("member", Atom.of("a"), LogicList.of(Atom.of("a")))))
        )

        collection = collectionGenerator(listOf(member))

        assertClausesHaveSameLengthAndContent(
            sequenceOf(),
            retractClauses(collection, Fact.of(Struct.of("member", Atom.of("a"), LogicList.of(Atom.of("b")))))
        )
    }

    override fun nestedGetWorksAtSeveralDepthLevels() {
        val collection = collectionGenerator(listOf(deep))

        for (query in deepQueries) {
            assertClausesHaveSameLengthAndContent(sequenceOf(deep), getClauses(collection, query))
        }
    }

    override fun nestedRetractWorksAtSeveralDepthLevels() {
        for (query in deepQueries) {
            val collection = collectionGenerator(listOf(deep))
            assertClausesHaveSameLengthAndContent(sequenceOf(deep), retractClauses(collection, query))
        }
    }

    override fun collectionHasTheCorrectSize() {
        assertEquals(0, emptyCollection.size)
        assertEquals(clauses.count(), collectionGenerator(clauses).size)
    }

    override fun emptyCollectionIsEmpty() {
        assertTrue(emptyCollection.isEmpty())
    }

    override fun filledCollectionIsNotEmpty() {
        assertFalse(collectionGenerator(clauses).isEmpty())
    }

    override fun collectionIsEmptyAfterRemovingEveryElement() {
        val emptiedCollection =
            collectionGenerator(clauses).retrieveAll(fFamilySelector).collection

        assertTrue(emptiedCollection.isEmpty())
    }

    override fun collectionContainsElement() {
        assertTrue(collectionGenerator(clauses).contains(presentClause))
    }

    override fun collectionDoesNotContainElement() {
        assertFalse(collectionGenerator(clauses).contains(absentClause))
    }

    override fun collectionContainsAllElement() {
        assertTrue(collectionGenerator(clauses).containsAll(clauses))
    }

    override fun collectionDoesNotContainAllElement() {
        val absentClauses = clauses + absentClause

        assertFalse(collectionGenerator(clauses).containsAll(absentClauses))
    }

    override fun singleClauseAdditionToCollectionWorksCorrectly() {
        val collectionAfterAddition = collectionGenerator(clauses).add(newClause)

        assertTrue(collectionAfterAddition.contains(newClause))
        assertTrue(collectionAfterAddition.containsAll(clauses))
    }

    override fun multipleClauseAdditionToCollectionWorksCorrectly() {
        val collectionAfterMultipleAddition = collectionGenerator(clauses).addAll(newClauses)

        assertTrue(collectionAfterMultipleAddition.containsAll(clauses + newClauses))
    }

    override fun retrievingPresentSingleClauseRetrievesTheClause() {
        val retrieveResult =
            collectionGenerator(clauses).retrieve(presentClause) as RetrieveResult.Success

        assertTermsAreEqual(presentClause, retrieveResult.firstClause)
        assertClausesHaveSameLengthAndContent(listOf(presentClause), retrieveResult.clauses)
    }

    override fun retrievingAbsentSingleClauseDoesNotAlterCollection() {
        val retrieveResult =
            collectionGenerator(clauses).retrieve(absentClause)

        assertClausesHaveSameLengthAndContent(
            collectionGenerator(clauses),
            retrieveResult.collection
        )
    }

    override fun retrievingPresentClauseWithRetrieveAllWorksCorrectly() {
        val slightlyModifiedGenericCollection = collectionGenerator(clauses + presentClause)
        val retrieveResult =
            slightlyModifiedGenericCollection.retrieveAll(presentClause) as RetrieveResult.Success

        assertClausesHaveSameLengthAndContent(
            listOf(presentClause, presentClause),
            retrieveResult.clauses
        )
        assertClausesHaveSameLengthAndContent(
            clauses - presentClause,
            retrieveResult.collection
        )
    }

    override fun retrievingAbsentClauseWithRetrieveAllDoesNotAlterCollection() {
        val retrieveResult = collectionGenerator(clauses).retrieveAll(absentClause)

        assertClausesHaveSameLengthAndContent(clauses, retrieveResult.collection)
    }

}