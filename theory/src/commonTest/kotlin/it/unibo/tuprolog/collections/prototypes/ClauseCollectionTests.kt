package it.unibo.tuprolog.collections.prototypes

import io.kotest.core.spec.style.funSpec
import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertTermsAreEqual
import it.unibo.tuprolog.testutils.TheoryUtils.deepClause
import it.unibo.tuprolog.testutils.TheoryUtils.deepQueries
import it.unibo.tuprolog.testutils.TheoryUtils.member
import it.unibo.tuprolog.testutils.TheoryUtils.memberClause
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import it.unibo.tuprolog.core.List as LogicList

fun clauseCollectionTests(
    emptyGenerator: () -> ClauseCollection,
    collectionGenerator: (Iterable<Clause>) -> ClauseCollection,
    getClauses: (ClauseCollection, Clause) -> Sequence<Clause>,
    retractClauses: (ClauseCollection, Clause) -> Sequence<Clause>,
) = funSpec {
    val fFamilySelector =
        Fact.of(Struct.of("f", Var.anonymous()))

    val presentClause =
        Fact.of(Struct.of("f", Atom.of("a")))

    val newClause =
        Fact.of(Struct.of("f", Atom.of("d")))

    val absentClause =
        Fact.of(Struct.of("f", Atom.of("z")))

    val clauses =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("c"))),
        )

    val newClauses =
        listOf(
            Fact.of(Struct.of("f", Atom.of("d"))),
            Fact.of(Struct.of("f", Atom.of("e"))),
            Fact.of(Struct.of("f", Atom.of("f"))),
        )

    val emptyCollection = emptyGenerator()

    test("get-takes-unification-into-account") {
        val collection = collectionGenerator(memberClause)

        assertClausesHaveSameLengthAndContent(
            memberClause.asSequence(),
            getClauses(
                collection,
                member(
                    LogicList.of(Struct.of("a", Var.of("X"))),
                    LogicList.of(LogicList.of(Struct.of("a", Integer.of(1)))),
                ),
            ),
        )
        assertClausesHaveSameLengthAndContent(
            memberClause.asSequence(),
            getClauses(collection, member(Atom.of("a"), LogicList.of(Atom.of("a")))),
        )
        assertClausesHaveSameLengthAndContent(
            sequenceOf(),
            getClauses(
                collection,
                member(
                    LogicList.of(Struct.of("a", Var.of("X"))),
                    LogicList.of(LogicList.of(Struct.of("b", Integer.of(1)))),
                ),
            ),
        )
        assertClausesHaveSameLengthAndContent(
            sequenceOf(),
            getClauses(collection, member(Atom.of("a"), LogicList.of(Atom.of("b")))),
        )
    }

    test("retract-takes-unification-into-account") {
        var collection = collectionGenerator(memberClause)

        assertClausesHaveSameLengthAndContent(
            memberClause.asSequence(),
            retractClauses(
                collection,
                member(
                    LogicList.of(Struct.of("a", Var.of("X"))),
                    LogicList.of(LogicList.of(Struct.of("a", Integer.of(1)))),
                ),
            ),
        )

        collection = collectionGenerator(memberClause)

        assertClausesHaveSameLengthAndContent(
            memberClause.asSequence(),
            retractClauses(collection, member(Atom.of("a"), LogicList.of(Atom.of("a")))),
        )

        collection = collectionGenerator(memberClause)

        assertClausesHaveSameLengthAndContent(
            sequenceOf(),
            retractClauses(
                collection,
                member(
                    LogicList.of(Struct.of("a", Var.of("X"))),
                    LogicList.of(LogicList.of(Struct.of("b", Integer.of(1)))),
                ),
            ),
        )

        collection = collectionGenerator(memberClause)

        assertClausesHaveSameLengthAndContent(
            sequenceOf(),
            retractClauses(collection, member(Atom.of("a"), LogicList.of(Atom.of("b")))),
        )
    }

    test("nested-get-works-at-several-depth-levels") {
        val collection = collectionGenerator(deepClause)

        for (query in deepQueries) {
            assertClausesHaveSameLengthAndContent(deepClause.asSequence(), getClauses(collection, query))
        }
    }

    test("nested-retract-works-at-several-depth-levels") {
        for (query in deepQueries) {
            val collection = collectionGenerator(deepClause)
            assertClausesHaveSameLengthAndContent(deepClause.asSequence(), retractClauses(collection, query))
        }
    }

    test("collection-has-the-correct-size") {
        assertEquals(0, emptyCollection.size)
        assertEquals(clauses.count(), collectionGenerator(clauses).size)
    }

    test("empty-collection-is-empty") {
        assertTrue(emptyCollection.isEmpty())
    }

    test("filled-collection-is-not-empty") {
        assertFalse(collectionGenerator(clauses).isEmpty())
    }

    test("collection-is-empty-after-removing-every-element") {
        val emptiedCollection = collectionGenerator(clauses).retrieveAll(fFamilySelector).collection

        assertTrue(emptiedCollection.isEmpty())
    }

    test("collection-contains-element") {
        assertTrue(collectionGenerator(clauses).contains(presentClause))
    }

    test("collection-does-not-contain-element") {
        assertFalse(collectionGenerator(clauses).contains(absentClause))
    }

    test("collection-contains-all-element") {
        assertTrue(collectionGenerator(clauses).containsAll(clauses))
    }

    test("collection-does-not-contain-all-element") {
        val absentClauses = clauses + absentClause

        assertFalse(collectionGenerator(clauses).containsAll(absentClauses))
    }

    test("single-clause-addition-to-collection-works-correctly") {
        val collectionAfterAddition = collectionGenerator(clauses).add(newClause)

        assertTrue(collectionAfterAddition.contains(newClause))
        assertTrue(collectionAfterAddition.containsAll(clauses))
    }

    test("multiple-clause-addition-to-collection-works-correctly") {
        val collectionAfterMultipleAddition = collectionGenerator(clauses).addAll(newClauses)

        assertTrue(collectionAfterMultipleAddition.containsAll(clauses + newClauses))
    }

    test("retrieving-present-single-clause-retrieves-the-clause") {
        val retrieveResult =
            collectionGenerator(clauses).retrieve(presentClause) as RetrieveResult.Success

        assertTermsAreEqual(presentClause, retrieveResult.firstClause)
        assertClausesHaveSameLengthAndContent(listOf(presentClause), retrieveResult.clauses)
    }

    test("retrieving-absent-single-clause-does-not-alter-collection") {
        val retrieveResult =
            collectionGenerator(clauses).retrieve(absentClause)

        assertClausesHaveSameLengthAndContent(
            collectionGenerator(clauses),
            retrieveResult.collection,
        )
    }

    test("retrieving-present-clause-with-retrieve-all-works-correctly") {
        val slightlyModifiedGenericCollection = collectionGenerator(clauses + presentClause)
        val retrieveResult =
            slightlyModifiedGenericCollection.retrieveAll(presentClause) as RetrieveResult.Success

        assertClausesHaveSameLengthAndContent(
            listOf(presentClause, presentClause),
            retrieveResult.clauses,
        )
        assertClausesHaveSameLengthAndContent(
            clauses - presentClause,
            retrieveResult.collection,
        )
    }

    test("retrieving-absent-clause-with-retrieve-all-does-not-alter-collection") {
        val retrieveResult = collectionGenerator(clauses).retrieveAll(absentClause)

        assertClausesHaveSameLengthAndContent(clauses, retrieveResult.collection)
    }
}
