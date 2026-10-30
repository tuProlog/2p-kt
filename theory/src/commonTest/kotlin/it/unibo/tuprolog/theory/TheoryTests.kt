package it.unibo.tuprolog.theory

import io.kotest.core.spec.style.funSpec
import io.kotest.datatest.withTests
import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Fact
import it.unibo.tuprolog.core.Indicator
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import it.unibo.tuprolog.testutils.ReteNodeUtils
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertClauseHeadPartialOrderingRespected
import it.unibo.tuprolog.testutils.TheoryUtils
import it.unibo.tuprolog.testutils.TheoryUtils.clauseQueryCases
import it.unibo.tuprolog.testutils.TheoryUtils.deepClause
import it.unibo.tuprolog.testutils.TheoryUtils.deepQueryCases
import it.unibo.tuprolog.testutils.TheoryUtils.memberClause
import it.unibo.tuprolog.testutils.TheoryUtils.negativeMemberQueryCases
import it.unibo.tuprolog.testutils.TheoryUtils.notWellFormedClauseCases
import it.unibo.tuprolog.testutils.TheoryUtils.notWellFormedClauses
import it.unibo.tuprolog.testutils.TheoryUtils.positiveMemberQueryCases
import it.unibo.tuprolog.testutils.TheoryUtils.ruleQueryByFunctorAndArityCases
import it.unibo.tuprolog.testutils.TheoryUtils.ruleQueryWithVarBodyCases
import it.unibo.tuprolog.testutils.TheoryUtils.wellFormedClauseCases
import it.unibo.tuprolog.testutils.TheoryUtils.wellFormedClauses
import it.unibo.tuprolog.testutils.TheoryUtils.wellFormedRuleCases
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

    val successfulRetractQueryCases = clauseQueryCases.filter { it.expected.isNotEmpty() }

    val failingRetractQueryCases = clauseQueryCases.filter { it.expected.isEmpty() }

    val successfulRetractQueryWithBodyVarCases = ruleQueryWithVarBodyCases.filter { it.expected.isNotEmpty() }

    val successfulRuleQueryCases = ReteNodeUtils.ruleQueryCases.filter { it.expected.isNotEmpty() }

    val failingRuleQueryCases = ReteNodeUtils.ruleQueryCases.filter { it.expected.isEmpty() }

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

    withTests(nameFn = { "plus-clause-complains-on-bad-clause/${it.id}" }, notWellFormedClauseCases) { case ->
        withFreshTheories {
            assertFailsWith<IllegalArgumentException> { filledTheory + case.clause }
        }
    }
    withTests(
        nameFn = { "contains-clause-returns-true-with-present-clauses/clause/${it.id}" },
        wellFormedClauseCases,
    ) { case ->
        withFreshTheories {
            assertTrue { case.clause in filledTheory }
        }
    }

    withTests(
        nameFn = { "contains-clause-returns-true-with-present-clauses/query/${it.id}" },
        successfulRetractQueryCases,
    ) { case ->
        withFreshTheories {
            assertTrue { case.query in filledTheory }
        }
    }
    test("contains-clause-returns-false-with-non-present-clauses") {
        withFreshTheories {
            assertFalse(anIndependentFact in filledTheory)
        }
    }

    withTests(
        nameFn = { "contains-clause-returns-false-with-non-present-clauses/query/${it.id}" },
        failingRetractQueryCases,
    ) { case ->
        withFreshTheories {
            assertFalse { case.query in filledTheory }
        }
    }
    withTests(
        nameFn = { "contains-struct-returns-true-if-matching-head-is-found/${it.id}" },
        wellFormedRuleCases,
    ) { case ->
        withFreshTheories {
            assertTrue { case.clause.head in filledTheory }
        }
    }
    test("contains-struct-returns-false-if-no-matching-head-is-found") {
        withFreshTheories {
            assertFalse(anIndependentFact.head in filledTheory)
        }
    }

    withTests(
        nameFn = { "contains-indicator-returns-true-if-matching-clause-is-found/clause/${it.id}" },
        wellFormedRuleCases,
    ) { case ->
        withFreshTheories {
            assertTrue { case.clause.head.indicator in filledTheory }
        }
    }

    withTests(
        nameFn = { "contains-indicator-returns-true-if-matching-clause-is-found/query/${it.id}" },
        successfulRuleQueryCases,
    ) { case ->
        withFreshTheories {
            assertTrue { case.query.head.indicator in filledTheory }
        }
    }
    test("contains-indicator-returns-false-if-no-matching-clause-is-found") {
        withFreshTheories {
            assertFalse(filledTheory.contains(anIndependentFact.head.indicator))
        }
    }

    withTests(
        nameFn = { "contains-indicator-returns-false-if-no-matching-clause-is-found/query/${it.id}" },
        failingRuleQueryCases,
    ) { case ->
        withFreshTheories {
            assertFalse { case.query.head.indicator in filledTheory }
        }
    }
    test("contains-indicator-complains-if-indicator-non-well-formed") {
        withFreshTheories {
            assertFailsWith<IllegalArgumentException> {
                Indicator.of(Var.anonymous(), Var.anonymous()) in filledTheory
            }
        }
    }

    withTests(nameFn = { "get-clause/${it.id}" }, clauseQueryCases) { case ->
        withFreshTheories {
            assertEquals(case.expected, filledTheory[case.query].toList())
        }
    }
    withTests(nameFn = { "get-struct/${it.id}" }, ruleQueryWithVarBodyCases) { case ->
        withFreshTheories {
            assertEquals(case.expected, filledTheory[case.query.head].toList())
        }
    }
    withTests(nameFn = { "get-indicator/${it.id}" }, ruleQueryByFunctorAndArityCases) { case ->
        withFreshTheories {
            assertEquals(case.expected, filledTheory[case.query.head.indicator].toList())
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

    withTests(nameFn = { "assert-a-clause-complains-on-bad-clause/${it.id}" }, notWellFormedClauseCases) { case ->
        withFreshTheories {
            assertFailsWith<IllegalArgumentException> { filledTheory.assertA(case.clause) }
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

    withTests(nameFn = { "assert-z-clause-complains-on-bad-clause/${it.id}" }, notWellFormedClauseCases) { case ->
        withFreshTheories {
            assertFailsWith<IllegalArgumentException> { filledTheory.assertZ(case.clause) }
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

    withTests(
        nameFn = { "retract-clause-returns-failure-if-empty-retracted-collection-success-otherwise/${it.id}" },
        clauseQueryCases,
    ) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retract(case.query)

            if (case.expected.isEmpty()) {
                assertTrue { retractResult is RetractResult.Failure }
            } else {
                assertTrue { retractResult is RetractResult.Success }
            }
        }
    }
    withTests(nameFn = {
        "retract-clause-removes-only-first-matching-clause/${it.id}"
    }, successfulRetractQueryCases) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retract(case.query) as RetractResult.Success

            assertEquals(listOf(case.expected.first()), retractResult.clauses.toList())
        }
    }
    withTests(
        nameFn = { "retract-struct-returns-failure-if-empty-retracted-collection-success-otherwise/${it.id}" },
        ruleQueryWithVarBodyCases,
    ) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retract(case.query)

            if (case.expected.isEmpty()) {
                assertTrue { retractResult is RetractResult.Failure }
            } else {
                assertTrue { retractResult is RetractResult.Success }
            }
        }
    }
    withTests(nameFn = {
        "retract-struct-removes-only-first-matching-clause/${it.id}"
    }, successfulRetractQueryWithBodyVarCases) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retract(case.query.head) as RetractResult.Success

            assertEquals(listOf(case.expected.first()), retractResult.clauses.toList())
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

    withTests(
        nameFn = { "retract-all-clause-returns-failure-if-empty-retracted-collection-success-otherwise/${it.id}" },
        clauseQueryCases,
    ) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retractAll(case.query)

            if (case.expected.isEmpty()) {
                assertTrue { retractResult is RetractResult.Failure }
            } else {
                assertTrue { retractResult is RetractResult.Success }
            }
        }
    }
    withTests(nameFn = {
        "retract-all-clause-removes-only-first-matching-clause/${it.id}"
    }, successfulRetractQueryCases) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retractAll(case.query) as RetractResult.Success

            assertEquals(case.expected, retractResult.clauses.toList())
        }
    }
    withTests(
        nameFn = { "retract-all-struct-returns-failure-if-empty-retracted-collection-success-otherwise/${it.id}" },
        ruleQueryWithVarBodyCases,
    ) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retractAll(case.query)

            if (case.expected.isEmpty()) {
                assertTrue { retractResult is RetractResult.Failure }
            } else {
                assertTrue { retractResult is RetractResult.Success }
            }
        }
    }
    withTests(nameFn = {
        "retract-all-struct-removes-only-first-matching-clause/${it.id}"
    }, successfulRetractQueryWithBodyVarCases) { case ->
        withFreshTheories {
            val retractResult = filledTheory.retractAll(case.query.head) as RetractResult.Success

            assertEquals(case.expected, retractResult.clauses.toList())
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

    withTests(nameFn = { "get-takes-unification-into-account/${it.id}" }, positiveMemberQueryCases) { case ->
        val theory = theoryGenerator(memberClause)

        assertClausesHaveSameLengthAndContent(
            memberClause.asSequence(),
            theory[case.clause],
        )
        assertClausesHaveSameLengthAndContent(
            memberClause.asSequence(),
            theory[case.clause.head],
        )
    }

    withTests(nameFn = { "get-takes-unification-into-account/${it.id}" }, negativeMemberQueryCases) { case ->
        val theory = theoryGenerator(memberClause)

        assertClausesHaveSameLengthAndContent(
            emptySequence(),
            theory[case.clause],
        )
        assertClausesHaveSameLengthAndContent(
            emptySequence(),
            theory[case.clause.head],
        )
    }
    withTests(nameFn = { "retract-takes-unification-into-account/${it.id}" }, positiveMemberQueryCases) { case ->
        var theory = theoryGenerator(memberClause)
        assertClausesHaveSameLengthAndContent(
            memberClause,
            (theory.retract(case.clause) as RetractResult.Success).clauses,
        )
        theory = theoryGenerator(memberClause)
        assertClausesHaveSameLengthAndContent(
            memberClause,
            (theory.retract(case.clause.head) as RetractResult.Success).clauses,
        )
    }

    withTests(nameFn = { "retract-takes-unification-into-account/${it.id}" }, negativeMemberQueryCases) { case ->
        val theory = theoryGenerator(memberClause)
        assertEquals(
            RetractResult.Failure(theory),
            theory.retract(case.clause),
        )
        assertEquals(
            RetractResult.Failure(theory),
            theory.retract(case.clause.head),
        )
    }
    withTests(nameFn = { "nested-get-works-at-several-depth-levels/${it.id}" }, deepQueryCases) { case ->
        val theory = theoryGenerator(deepClause)

        assertClausesHaveSameLengthAndContent(
            deepClause.asSequence(),
            theory[case.clause],
        )
    }
    withTests(nameFn = { "nested-retract-works-at-several-depth-levels/${it.id}" }, deepQueryCases) { case ->
        val theory = theoryGenerator(deepClause)
        assertClausesHaveSameLengthAndContent(
            deepClause,
            (theory.retract(case.clause) as RetractResult.Success).clauses,
        )
    }
}

private data class FreshTheoriesScope(
    var emptyTheory: Theory,
    var filledTheory: Theory,
)
