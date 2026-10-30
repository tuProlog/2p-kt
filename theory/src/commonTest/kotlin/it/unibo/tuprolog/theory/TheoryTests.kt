package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.funSpec
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertClauseHeadPartialOrderingRespected
import it.unibo.tuprolog.testutils.TheoryUtils
import it.unibo.tuprolog.testutils.TheoryUtils.clausesQueryResultsMap
import it.unibo.tuprolog.testutils.TheoryUtils.deepClause
import it.unibo.tuprolog.testutils.TheoryUtils.deepQueries
import it.unibo.tuprolog.testutils.TheoryUtils.memberClause
import it.unibo.tuprolog.testutils.TheoryUtils.negativeMemberQueries
import it.unibo.tuprolog.testutils.TheoryUtils.notWellFormedClauses
import it.unibo.tuprolog.testutils.TheoryUtils.positiveMemberQueries
import it.unibo.tuprolog.testutils.TheoryUtils.rulesQueryResultByFunctorAndArity
import it.unibo.tuprolog.testutils.TheoryUtils.rulesQueryWithVarBodyResultsMap
import it.unibo.tuprolog.testutils.TheoryUtils.wellFormedClauses
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

/**
 * Test class for all [Theory] subtypes
 *
 * @author Enrico
 */
fun theoryTests(
    emptyTheoryGenerator: () -> Theory,
    theoryGenerator: (Iterable<Clause>) -> Theory,
) = funSpec {
    fun <R> withFreshTheories(
        emptyTheory: Theory = emptyTheoryGenerator(),
        filledTheory: Theory = theoryGenerator(wellFormedClauses),
        action: FreshTheoriesScope.() -> R,
    ): R = FreshTheoriesScope(emptyTheory, filledTheory).action()

    val anIndependentFact: Fact = Fact.of(Atom.of("myTestingFact"))

    val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))

    val successfulRetractQueryResultMap by lazy {
        clausesQueryResultsMap.filterValues { it.isNotEmpty() }
    }

    val successfulRetractQueryWithBodyVarResultsMap by lazy {
        rulesQueryWithVarBodyResultsMap.filterValues { it.isNotEmpty() }
    }

    fun FreshTheoriesScope.assertTrueIfMutableFalseOtherwise(boolean: Boolean) {
        if (filledTheory is MutableTheory) {
            assertTrue(boolean)
        } else {
            assertFalse(boolean)
        }
    }

    fun FreshTheoriesScope.assertFalseIfMutableTrueOtherwise(boolean: Boolean) {
        if (filledTheory is MutableTheory) {
            assertFalse(boolean)
        } else {
            assertTrue(boolean)
        }
    }

    fun assertSameIfMutableNotSameOtherwise(
        first: Theory,
        second: Theory,
    ) {
        if (first is MutableTheory && second is MutableTheory) {
            assertSame(first, second)
        } else if (first !is MutableTheory && second !is MutableTheory) {
            assertNotSame(first, second)
        } else {
            throw IllegalStateException(
                "There must be a problem: trying to compare a mutable theory with an immutable one",
            )
        }
    }

    test("theory-complains-if-providing-not-well-formed-clauses-upon-construction") {
        assertFailsWith<IllegalArgumentException> { theoryGenerator(notWellFormedClauses) }
    }

    test("clauses-correct") {
        withFreshTheories {
            assertTrue(emptyTheory.clauses.none())
            assertClauseHeadPartialOrderingRespected(wellFormedClauses, filledTheory.clauses)
        }
    }

    test("rules-correct") {
        withFreshTheories {
            val rules = wellFormedClauses.filterIsInstance<Rule>()

            assertClauseHeadPartialOrderingRespected(rules, filledTheory.rules)
            assertTrue(emptyTheory.rules.none())
        }
    }

    test("directives-correct") {
        withFreshTheories {
            val directives = wellFormedClauses.filterIsInstance<Directive>()

            assertClauseHeadPartialOrderingRespected(directives, filledTheory.directives)
            assertTrue(emptyTheory.directives.none())
        }
    }

    test("plus-theory-preserves-order") {
        withFreshTheories {
            val (firstHalfClauses, secondHalfClauses) = TheoryUtils.wellFormedClausesHelves
            val toBeTested = theoryGenerator(firstHalfClauses) + theoryGenerator(secondHalfClauses)

            assertClausesHaveSameLengthAndContent(filledTheory.clauses, toBeTested.clauses)
        }
    }

    test("plus-theory-fails-on-bad-theory") {
        withFreshTheories {
            val badTheory =
                object : Theory by emptyTheoryGenerator() {
                    override val clauses: Iterable<Clause> = notWellFormedClauses

                    override fun iterator(): Iterator<Clause> = notWellFormedClauses.iterator()

                    override val isEmpty: Boolean get() = notWellFormedClauses.isEmpty()
                    override val isNonEmpty: Boolean get() = !isEmpty
                }
            assertFailsWith<IllegalArgumentException> { filledTheory + badTheory }
        }
    }

    test("plus-clause") {
        withFreshTheories {
            val (firstHalfClauses, secondHalfClauses) = TheoryUtils.wellFormedClausesHelves
            var toBeTested: Theory = theoryGenerator(firstHalfClauses)
            secondHalfClauses.forEach { toBeTested += it }

            assertClausesHaveSameLengthAndContent(filledTheory.clauses, toBeTested.clauses)
        }
    }

    test("plus-clause-respects-partial-ordering") {
        withFreshTheories {
            val toBeTested = filledTheory + aRule

            assertClauseHeadPartialOrderingRespected(wellFormedClauses + aRule, toBeTested.clauses)
        }
    }

    test("plus-clause-returns-new-unlinked-theory-instance") {
        withFreshTheories {
            val toBeTested = filledTheory + anIndependentFact

            assertTrueIfMutableFalseOtherwise(anIndependentFact in filledTheory)
            assertTrue(anIndependentFact in toBeTested)
        }
    }

    test("plus-clause-complains-on-bad-clause") {
        withFreshTheories {
            notWellFormedClauses.forEach {
                assertFailsWith<IllegalArgumentException> { filledTheory + it }
            }
        }
    }

    test("contains-clause-returns-true-with-present-clauses") {
        withFreshTheories {
            wellFormedClauses.forEach {
                assertTrue { it in filledTheory }
            }

            clausesQueryResultsMap.forEach { (query, result) ->
                if (result.isNotEmpty()) assertTrue { query in filledTheory }
            }
        }
    }

    test("contains-clause-returns-false-with-non-present-clauses") {
        withFreshTheories {
            assertFalse(anIndependentFact in filledTheory)

            clausesQueryResultsMap.forEach { (query, result) ->
                if (result.isEmpty()) assertFalse { query in filledTheory }
            }
        }
    }

    test("contains-struct-returns-true-if-matching-head-is-found") {
        withFreshTheories {
            wellFormedClauses.filterIsInstance<Rule>().forEach {
                assertTrue { it.head in filledTheory }
            }
        }
    }

    test("contains-struct-returns-false-if-no-matching-head-is-found") {
        withFreshTheories {
            assertFalse(anIndependentFact.head in filledTheory)
        }
    }

    test("contains-indicator-returns-true-if-matching-clause-is-found") {
        withFreshTheories {
            wellFormedClauses.filterIsInstance<Rule>().forEach {
                assertTrue { it.head.indicator in filledTheory }
            }

            clausesQueryResultsMap
                .filterKeys { it is Rule }
                .mapKeys { it.key as Rule }
                .forEach { (query, result) ->
                    if (result.isNotEmpty()) assertTrue { query.head.indicator in filledTheory }
                }
        }
    }

    test("contains-indicator-returns-false-if-no-matching-clause-is-found") {
        withFreshTheories {
            assertFalse(filledTheory.contains(anIndependentFact.head.indicator))

            clausesQueryResultsMap
                .filterKeys { it is Rule }
                .mapKeys { it.key as Rule }
                .forEach { (query, result) ->
                    if (result.isEmpty()) assertFalse { query.head.indicator in filledTheory }
                }
        }
    }

    test("contains-indicator-complains-if-indicator-non-well-formed") {
        withFreshTheories {
            assertFailsWith<IllegalArgumentException> {
                Indicator.of(Var.anonymous(), Var.anonymous()) in filledTheory
            }
        }
    }

    test("get-clause") {
        withFreshTheories {
            clausesQueryResultsMap.forEach { (query, result) ->
                assertEquals(result, filledTheory[query].toList())
            }
        }
    }

    test("get-struct") {
        withFreshTheories {
            rulesQueryWithVarBodyResultsMap
                .forEach { (query, result) ->
                    val a = filledTheory[query.head].toList()
                    assertEquals(result, a)
                }
        }
    }

    test("get-indicator") {
        withFreshTheories {
            rulesQueryResultByFunctorAndArity
                .forEach { (query, result) ->
                    val a = filledTheory[query.head.indicator].toList()
                    assertEquals(result, a)
                }
        }
    }

    test("get-indicator-complains-if-indicator-non-well-formed") {
        withFreshTheories {
            assertFailsWith<IllegalArgumentException> {
                filledTheory.get(Indicator.of(Var.anonymous(), Var.anonymous()))
            }
        }
    }

    test("assert-a-clause") {
        withFreshTheories {
            val correctPartiallyOrderedClauses = wellFormedClauses.toMutableList().apply { add(0, aRule) }
            val toBeTested = filledTheory.assertA(aRule)

            assertClauseHeadPartialOrderingRespected(correctPartiallyOrderedClauses, toBeTested.clauses)
        }
    }

    test("assert-a-clause-complains-on-bad-clause") {
        withFreshTheories {
            notWellFormedClauses.forEach {
                assertFailsWith<IllegalArgumentException> { filledTheory.assertA(it) }
            }
        }
    }

    test("assert-a-struct") {
        withFreshTheories {
            val correctPartiallyOrderedClauses =
                wellFormedClauses
                    .toMutableList()
                    .apply { add(0, Fact.of(aRule.head)) }
            val toBeTested = filledTheory.assertA(aRule.head)

            assertClauseHeadPartialOrderingRespected(correctPartiallyOrderedClauses, toBeTested.clauses)
        }
    }

    test("assert-a-creates-new-unlinked-instance") {
        withFreshTheories {
            val toBeTested = filledTheory.assertA(anIndependentFact)

            assertTrueIfMutableFalseOtherwise(anIndependentFact in filledTheory)
            assertTrue(anIndependentFact in toBeTested)
        }
    }

    test("assert-z-clause") {
        withFreshTheories {
            val toBeTested = filledTheory.assertZ(aRule)

            assertClauseHeadPartialOrderingRespected(wellFormedClauses + aRule, toBeTested.clauses)
        }
    }

    test("assert-z-clause-complains-on-bad-clause") {
        withFreshTheories {
            notWellFormedClauses.forEach {
                assertFailsWith<IllegalArgumentException> { filledTheory.assertZ(it) }
            }
        }
    }

    test("assert-z-struct") {
        withFreshTheories {
            val toBeTested = filledTheory.assertZ(aRule.head)

            assertClauseHeadPartialOrderingRespected(
                wellFormedClauses + Fact.of(aRule.head),
                toBeTested.clauses,
            )
        }
    }

    test("assert-z-creates-new-unlinked-instance") {
        withFreshTheories {
            val toBeTested = filledTheory.assertZ(anIndependentFact)

            assertTrueIfMutableFalseOtherwise(anIndependentFact in filledTheory)
            assertTrue(anIndependentFact in toBeTested)
        }
    }

    test("retract-clause-returns-failure-if-empty-retracted-collection-success-otherwise") {
        withFreshTheories {
            clausesQueryResultsMap.forEach { (query, result) ->
                val retractResult = filledTheory.retract(query)

                if (result.isEmpty()) {
                    assertTrue { retractResult is RetractResult.Failure }
                } else {
                    assertTrue { retractResult is RetractResult.Success }
                }
            }
        }
    }

    test("retract-clause-removes-only-first-matching-clause") {
        successfulRetractQueryResultMap.forEach { (query, result) ->
            withFreshTheories {
                val retractResult = filledTheory.retract(query) as RetractResult.Success

                assertEquals(listOf(result.first()), retractResult.clauses.toList())
            }
        }
    }

    test("retract-struct-returns-failure-if-empty-retracted-collection-success-otherwise") {
        withFreshTheories {
            rulesQueryWithVarBodyResultsMap.forEach { (query, result) ->
                val retractResult = filledTheory.retract(query)

                if (result.isEmpty()) {
                    assertTrue { retractResult is RetractResult.Failure }
                } else {
                    assertTrue { retractResult is RetractResult.Success }
                }
            }
        }
    }

    test("retract-struct-removes-only-first-matching-clause") {
        successfulRetractQueryWithBodyVarResultsMap.forEach { (query, result) ->
            withFreshTheories {
                val retractResult = filledTheory.retract(query.head) as RetractResult.Success

                assertEquals(listOf(result.first()), retractResult.clauses.toList())
            }
        }
    }

    test("retract-creates-new-unlinked-instance-if-successful") {
        withFreshTheories {
            val aDatabaseClause = filledTheory.clauses.first()
            val toBeTested = filledTheory.retract(aDatabaseClause).theory

            assertSameIfMutableNotSameOtherwise(filledTheory, toBeTested)
            assertFalseIfMutableTrueOtherwise(aDatabaseClause in filledTheory)
            assertFalse(aDatabaseClause in toBeTested)
        }
    }

    test("retract-returns-same-theory-on-failure") {
        withFreshTheories {
            val toBeTested = filledTheory.retract(anIndependentFact).theory

            assertSame(filledTheory, toBeTested) // TODO take care of mutable theories here
        }
    }

    test("retract-all-clause-returns-failure-if-empty-retracted-collection-success-otherwise") {
        clausesQueryResultsMap.forEach { (query, result) ->
            withFreshTheories {
                val retractResult = filledTheory.retractAll(query)

                if (result.isEmpty()) {
                    assertTrue { retractResult is RetractResult.Failure }
                } else {
                    assertTrue { retractResult is RetractResult.Success }
                }
            }
        }
    }

    test("retract-all-clause-removes-only-first-matching-clause") {
        successfulRetractQueryResultMap.forEach { (query, result) ->
            withFreshTheories {
                val retractResult = filledTheory.retractAll(query) as RetractResult.Success

                assertEquals(result, retractResult.clauses.toList())
            }
        }
    }

    test("retract-all-struct-returns-failure-if-empty-retracted-collection-success-otherwise") {
        rulesQueryWithVarBodyResultsMap.forEach { (query, result) ->
            withFreshTheories {
                val retractResult = filledTheory.retractAll(query)

                if (result.isEmpty()) {
                    assertTrue { retractResult is RetractResult.Failure }
                } else {
                    assertTrue { retractResult is RetractResult.Success }
                }
            }
        }
    }

    test("retract-all-struct-removes-only-first-matching-clause") {
        successfulRetractQueryWithBodyVarResultsMap.forEach { (query, result) ->
            withFreshTheories {
                val retractResult = filledTheory.retractAll(query.head) as RetractResult.Success

                assertEquals(result, retractResult.clauses.toList())
            }
        }
    }

    test("retract-all-creates-new-unlinked-instance-if-successful") {
        withFreshTheories {
            val aDatabaseClause = filledTheory.clauses.first()
            val toBeTested = filledTheory.retractAll(aDatabaseClause).theory

            assertSameIfMutableNotSameOtherwise(filledTheory, toBeTested)
            assertFalseIfMutableTrueOtherwise(aDatabaseClause in filledTheory)
            assertFalse(aDatabaseClause in toBeTested)
        }
    }

    test("retract-all-returns-same-theory-on-failure") {
        withFreshTheories {
            val toBeTested = filledTheory.retractAll(anIndependentFact).theory

            assertSame(filledTheory, toBeTested)
        }
    }

    test("iterator-returns-correct-instance") {
        withFreshTheories {
            assertClausesHaveSameLengthAndContent(
                filledTheory.clauses.iterator().asSequence(),
                filledTheory.iterator().asSequence(),
            )
        }
    }

    test("get-takes-unification-into-account") {
        val theory = theoryGenerator(memberClause)

        for (query in positiveMemberQueries) {
            assertClausesHaveSameLengthAndContent(
                memberClause.asSequence(),
                theory[query],
            )
            assertClausesHaveSameLengthAndContent(
                memberClause.asSequence(),
                theory[query.head],
            )
        }

        for (query in negativeMemberQueries) {
            assertClausesHaveSameLengthAndContent(
                emptySequence(),
                theory[query],
            )
            assertClausesHaveSameLengthAndContent(
                emptySequence(),
                theory[query.head],
            )
        }
    }

    test("retract-takes-unification-into-account") {
        for (query in positiveMemberQueries) {
            var theory = theoryGenerator(memberClause)
            assertClausesHaveSameLengthAndContent(
                memberClause,
                (theory.retract(query) as RetractResult.Success).clauses,
            )
            theory = theoryGenerator(memberClause)
            assertClausesHaveSameLengthAndContent(
                memberClause,
                (theory.retract(query.head) as RetractResult.Success).clauses,
            )
        }

        for (query in negativeMemberQueries) {
            val theory = theoryGenerator(memberClause)
            assertEquals(
                RetractResult.Failure(theory),
                theory.retract(query),
            )
            assertEquals(
                RetractResult.Failure(theory),
                theory.retract(query.head),
            )
        }
    }

    test("nested-get-works-at-several-depth-levels") {
        val theory = theoryGenerator(deepClause)

        for (query in deepQueries) {
            assertClausesHaveSameLengthAndContent(
                deepClause.asSequence(),
                theory[query],
            )
        }
    }

    test("nested-retract-works-at-several-depth-levels") {
        for (query in deepQueries) {
            val theory = theoryGenerator(deepClause)
            assertClausesHaveSameLengthAndContent(
                deepClause,
                (theory.retract(query) as RetractResult.Success).clauses,
            )
        }
    }
}

private data class FreshTheoriesScope(
    var emptyTheory: Theory,
    var filledTheory: Theory,
)
