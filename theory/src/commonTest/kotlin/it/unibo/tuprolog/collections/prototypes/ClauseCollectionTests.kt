package it.unibo.tuprolog.collections.prototypes

import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
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
import it.unibo.tuprolog.testutils.TheoryUtils.deepQueryCases
import it.unibo.tuprolog.testutils.TheoryUtils.member
import it.unibo.tuprolog.testutils.TheoryUtils.memberClause
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

    withTests(nameFn = { "nested-get-works-at-several-depth-levels/${it.id}" }, deepQueryCases) { case ->
        val collection = collectionGenerator(deepClause)

        assertClausesHaveSameLengthAndContent(deepClause.asSequence(), getClauses(collection, case.clause))
    }

    withTests(nameFn = { "nested-retract-works-at-several-depth-levels/${it.id}" }, deepQueryCases) { case ->
        val collection = collectionGenerator(deepClause)
        assertClausesHaveSameLengthAndContent(deepClause.asSequence(), retractClauses(collection, case.clause))
    }

    test("collection-has-the-correct-size") {
        emptyCollection.size shouldBe 0
        collectionGenerator(clauses).size shouldBe clauses.count()
    }

    test("empty-collection-is-empty") {
        emptyCollection.isEmpty().shouldBeTrue()
    }

    test("filled-collection-is-not-empty") {
        collectionGenerator(clauses).isEmpty().shouldBeFalse()
    }

    test("collection-is-empty-after-removing-every-element") {
        val emptiedCollection = collectionGenerator(clauses).retrieveAll(fFamilySelector).collection

        emptiedCollection.isEmpty().shouldBeTrue()
    }

    test("collection-contains-element") {
        collectionGenerator(clauses).contains(presentClause).shouldBeTrue()
    }

    test("collection-does-not-contain-element") {
        collectionGenerator(clauses).contains(absentClause).shouldBeFalse()
    }

    test("collection-contains-all-element") {
        collectionGenerator(clauses).containsAll(clauses).shouldBeTrue()
    }

    test("collection-does-not-contain-all-element") {
        val absentClauses = clauses + absentClause

        collectionGenerator(clauses).containsAll(absentClauses).shouldBeFalse()
    }

    test("single-clause-addition-to-collection-works-correctly") {
        val collectionAfterAddition = collectionGenerator(clauses).add(newClause)

        collectionAfterAddition.contains(newClause).shouldBeTrue()
        collectionAfterAddition.containsAll(clauses).shouldBeTrue()
    }

    test("multiple-clause-addition-to-collection-works-correctly") {
        val collectionAfterMultipleAddition = collectionGenerator(clauses).addAll(newClauses)

        collectionAfterMultipleAddition.containsAll(clauses + newClauses).shouldBeTrue()
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
