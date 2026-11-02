package it.unibo.tuprolog.collections.prototypes

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.ClauseQueue
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertTermsAreEqual
import it.unibo.tuprolog.utils.permutations

fun clauseQueueTests(
    emptyGenerator: () -> ClauseQueue,
    collectionGenerator: (Iterable<Clause>) -> ClauseQueue,
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

    val orderSensitiveClauses =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("g", Numeric.of(1))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("g", Numeric.of(2))),
            Fact.of(Struct.of("f", Atom.of("c"))),
            Fact.of(Struct.of("g", Numeric.of(3))),
        )

    val numericClauses =
        listOf(
            Fact.of(Struct.of("g", Numeric.of(1))),
            Fact.of(Struct.of("g", Numeric.of(2))),
            Fact.of(Struct.of("g", Numeric.of(3))),
        )

    val emptyCollection = emptyGenerator()

    fun getClauses(
        collection: ClauseCollection,
        query: Clause,
    ): Sequence<Clause> = (collection as ClauseQueue).get(query)

    fun retractClauses(
        collection: ClauseCollection,
        query: Clause,
    ): Sequence<Clause> =
        when (val res = (collection as ClauseQueue).retrieve(query)) {
            is RetrieveResult.Success -> res.clauses.asSequence()
            else -> emptySequence()
        }

    include(clauseCollectionTests(emptyGenerator, collectionGenerator, ::getClauses, ::retractClauses))

    test("get-with-present-clause-returns-the-correct-sequence") {
        val sequence = collectionGenerator(clauses + presentClause)[presentClause]

        assertClausesHaveSameLengthAndContent(
            listOf(presentClause, presentClause),
            sequence.toList(),
        )
    }

    test("get-with-absent-clause-returns-an-empty-sequence") {
        val result = collectionGenerator(clauses)[absentClause]

        result.toList() shouldBe (emptyList<Clause>())
    }

    test("add-first-prepends-element") {
        val prependedCollectionSnapshot = collectionGenerator(clauses).addFirst(newClause)

        assertClausesHaveSameLengthAndContent(
            collectionGenerator(listOf(newClause) + clauses),
            prependedCollectionSnapshot,
        )
    }

    test("add-last-appends-element") {
        val appendedCollectionSnapshot = collectionGenerator(clauses).addLast(newClause)

        assertClausesHaveSameLengthAndContent(
            collectionGenerator(clauses + listOf(newClause)),
            appendedCollectionSnapshot,
        )
    }

    test("get-first-retrieves-elements-from-first-position") {
        val result = collectionGenerator(orderSensitiveClauses).getFifoOrdered(fFamilySelector)

        assertClausesHaveSameLengthAndContent(clauses, result.asIterable())
    }

    test("get-last-retrieves-elements-from-last-position") {
        val result = collectionGenerator(orderSensitiveClauses).getLifoOrdered(fFamilySelector)

        assertClausesHaveSameLengthAndContent(clauses.asReversed(), result.asIterable())
    }

    test("simple-add-behaves-as-add-last") {
        val appendedCollectionSnapshot = collectionGenerator(clauses).add(newClause)

        assertClausesHaveSameLengthAndContent(
            collectionGenerator(clauses + listOf(newClause)),
            appendedCollectionSnapshot,
        )
    }

    test("simple-get-behaves-as-get-first") {
        val result = collectionGenerator(orderSensitiveClauses).get(fFamilySelector)

        assertClausesHaveSameLengthAndContent(clauses, result.asIterable())
    }

    test("retrieve-first-removes-the-first-unifying-element") {
        val result =
            collectionGenerator(clauses).retrieveFirst(presentClause) as RetrieveResult.Success

        assertTermsAreEqual(presentClause, result.firstClause)
        assertClausesHaveSameLengthAndContent(clauses - listOf(presentClause), result.collection)
    }

    test("simple-retrieve-behaves-as-retrieve-first") {
        val result =
            collectionGenerator(clauses).retrieve(presentClause) as RetrieveResult.Success

        assertTermsAreEqual(presentClause, result.firstClause)
        assertClausesHaveSameLengthAndContent(clauses - listOf(presentClause), result.collection)
    }

    test("equals-is-order-dependent") {
        val actualClauses = clauses

        val tester = collectionGenerator(actualClauses)

        val perm = actualClauses.permutations().iterator()

        collectionGenerator(perm.next()) shouldBe tester

        while (perm.hasNext()) {
            collectionGenerator(perm.next()) shouldNotBe tester
        }
    }

    test("hash-code-is-order-dependent") {
        val actualClauses = clauses

        val permutations = actualClauses.permutations().toList()

        (permutations.map { collectionGenerator(it) }.toSet().size) shouldBe permutations.size
    }
}
