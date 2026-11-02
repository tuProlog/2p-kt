package it.unibo.tuprolog.collections.prototypes

import io.kotest.core.spec.style.funSpec
import io.kotest.matchers.shouldBe
import it.unibo.tuprolog.collections.ClauseCollection
import it.unibo.tuprolog.collections.ClauseMultiSet
import it.unibo.tuprolog.collections.RetrieveResult
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import it.unibo.tuprolog.utils.permutations

fun clauseMultiSetTests(
    emptyGenerator: () -> ClauseMultiSet,
    collectionGenerator: (Iterable<Clause>) -> ClauseMultiSet,
) = funSpec {
    val presentClause =
        Fact.of(Struct.of("f", Atom.of("a")))

    val absentClause =
        Fact.of(Struct.of("f", Atom.of("z")))

    val clauses =
        listOf(
            Fact.of(Struct.of("f", Atom.of("a"))),
            Fact.of(Struct.of("f", Atom.of("b"))),
            Fact.of(Struct.of("f", Atom.of("c"))),
        )

    val emptyCollection = emptyGenerator()

    fun getClauses(
        collection: ClauseCollection,
        query: Clause,
    ): Sequence<Clause> = (collection as ClauseMultiSet).get(query)

    fun retractClauses(
        collection: ClauseCollection,
        query: Clause,
    ): Sequence<Clause> =
        when (val res = (collection as ClauseMultiSet).retrieve(query)) {
            is RetrieveResult.Success -> res.clauses.asSequence()
            else -> emptySequence()
        }

    include(clauseCollectionTests(emptyGenerator, collectionGenerator, ::getClauses, ::retractClauses))

    test("counting-on-present-clause-answer-the-right-number") {
        val count = collectionGenerator(clauses).count(presentClause)
        val count2 = collectionGenerator(clauses + presentClause).count(presentClause)

        count shouldBe 1
        count2 shouldBe 2
    }

    test("counting-on-absent-clause-answer-zero") {
        val count = collectionGenerator(clauses).count(absentClause)
        val count2 = emptyCollection.count(absentClause)

        count shouldBe 0
        count2 shouldBe 0
    }

    test("get-with-present-clause-returns-the-correct-sequence") {
        val sequence = collectionGenerator(clauses + presentClause)[presentClause]

        assertClausesHaveSameLengthAndContent(listOf(presentClause, presentClause), sequence.toList())
    }

    test("get-with-absent-clause-returns-an-empty-sequence") {
        val result = collectionGenerator(clauses)[absentClause]

        result.toList() shouldBe emptyList()
    }

    test("equals-is-order-independent") {
        val actualClauses = clauses + clauses[0]

        val tester = collectionGenerator(actualClauses)

        val perm = actualClauses.permutations().iterator()

        collectionGenerator(perm.next()) shouldBe tester

        while (perm.hasNext()) {
            collectionGenerator(perm.next()) shouldBe tester
        }
    }

    test("hash-code-is-order-independent") {
        val actualClauses = clauses + clauses[0]

        val permutations = actualClauses.permutations().toList()

        (permutations.map { collectionGenerator(it) }.toSet().size) shouldBe 1
    }
}
