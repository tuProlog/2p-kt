package it.unibo.tuprolog.theory

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
class PrototypeTheoryTest(
    private val emptyTheoryGenerator: () -> Theory,
    private val theoryGenerator: (Iterable<Clause>) -> Theory,
) {
    private data class FreshTheoriesScope(
        var emptyTheory: Theory,
        var filledTheory: Theory,
    )

    private fun <R> withFreshTheories(
        emptyTheory: Theory = emptyTheoryGenerator(),
        filledTheory: Theory = theoryGenerator(wellFormedClauses),
        action: FreshTheoriesScope.() -> R,
    ): R = FreshTheoriesScope(emptyTheory, filledTheory).action()

    private val anIndependentFact: Fact = Fact.of(Atom.of("myTestingFact"))

    private val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))

    private val successfulRetractQueryResultMap by lazy {
        clausesQueryResultsMap.filterValues { it.isNotEmpty() }
    }

    private val successfulRetractQueryWithBodyVarResultsMap by lazy {
        rulesQueryWithVarBodyResultsMap.filterValues { it.isNotEmpty() }
    }

    fun theoryComplainsIFProvidingNotWellFormedClausesUponConstruction() {
        assertFailsWith<IllegalArgumentException> { theoryGenerator(notWellFormedClauses) }
    }

    fun clausesCorrect() {
        withFreshTheories {
            assertTrue(emptyTheory.clauses.none())
            assertClauseHeadPartialOrderingRespected(wellFormedClauses, filledTheory.clauses)
        }
    }

    fun rulesCorrect() {
        withFreshTheories {
            val rules = wellFormedClauses.filterIsInstance<Rule>()

            assertClauseHeadPartialOrderingRespected(rules, filledTheory.rules)
            assertTrue(emptyTheory.rules.none())
        }
    }

    fun directivesCorrect() {
        withFreshTheories {
            val directives = wellFormedClauses.filterIsInstance<Directive>()

            assertClauseHeadPartialOrderingRespected(directives, filledTheory.directives)
            assertTrue(emptyTheory.directives.none())
        }
    }

    fun plusTheoryPreservesOrder() {
        withFreshTheories {
            val (firstHalfClauses, secondHalfClauses) = TheoryUtils.wellFormedClausesHelves
            val toBeTested = theoryGenerator(firstHalfClauses) + theoryGenerator(secondHalfClauses)

            assertClausesHaveSameLengthAndContent(filledTheory.clauses, toBeTested.clauses)
        }
    }

    fun plusTheoryFailsOnBadTheory() {
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

    fun plusClause() {
        withFreshTheories {
            val (firstHalfClauses, secondHalfClauses) = TheoryUtils.wellFormedClausesHelves
            var toBeTested: Theory = theoryGenerator(firstHalfClauses)
            secondHalfClauses.forEach { toBeTested += it }

            assertClausesHaveSameLengthAndContent(filledTheory.clauses, toBeTested.clauses)
        }
    }

    fun plusClauseRespectsPartialOrdering() {
        withFreshTheories {
            val toBeTested = filledTheory + aRule

            assertClauseHeadPartialOrderingRespected(wellFormedClauses + aRule, toBeTested.clauses)
        }
    }

    fun plusClauseReturnsNewUnlinkedTheoryInstance() {
        withFreshTheories {
            val toBeTested = filledTheory + anIndependentFact

            assertTrueIfMutableFalseOtherwise(anIndependentFact in filledTheory)
            assertTrue(anIndependentFact in toBeTested)
        }
    }

    fun plusClauseComplainsOnBadClause() {
        withFreshTheories {
            notWellFormedClauses.forEach {
                assertFailsWith<IllegalArgumentException> { filledTheory + it }
            }
        }
    }

    fun containsClauseReturnsTrueWithPresentClauses() {
        withFreshTheories {
            wellFormedClauses.forEach {
                assertTrue { it in filledTheory }
            }

            clausesQueryResultsMap.forEach { (query, result) ->
                if (result.isNotEmpty()) assertTrue { query in filledTheory }
            }
        }
    }

    fun containsClauseReturnsFalseWithNonPresentClauses() {
        withFreshTheories {
            assertFalse(anIndependentFact in filledTheory)

            clausesQueryResultsMap.forEach { (query, result) ->
                if (result.isEmpty()) assertFalse { query in filledTheory }
            }
        }
    }

    fun containsStructReturnsTrueIfMatchingHeadIsFound() {
        withFreshTheories {
            wellFormedClauses.filterIsInstance<Rule>().forEach {
                assertTrue { it.head in filledTheory }
            }
        }
    }

    fun containsStructReturnsFalseIfNoMatchingHeadIsFound() {
        withFreshTheories {
            assertFalse(anIndependentFact.head in filledTheory)
        }
    }

    fun containsIndicatorReturnsTrueIfMatchingClauseIsFound() {
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

    fun containsIndicatorReturnsFalseIfNoMatchingClauseIsFound() {
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

    fun containsIndicatorComplainsIfIndicatorNonWellFormed() {
        withFreshTheories {
            assertFailsWith<IllegalArgumentException> {
                Indicator.of(Var.anonymous(), Var.anonymous()) in filledTheory
            }
        }
    }

    fun getClause() {
        withFreshTheories {
            clausesQueryResultsMap.forEach { (query, result) ->
                assertEquals(result, filledTheory[query].toList())
            }
        }
    }

    fun getStruct() {
        withFreshTheories {
            rulesQueryWithVarBodyResultsMap
                .forEach { (query, result) ->
                    val a = filledTheory[query.head].toList()
                    assertEquals(result, a)
                }
        }
    }

    fun getIndicator() {
        withFreshTheories {
            rulesQueryResultByFunctorAndArity
                .forEach { (query, result) ->
                    val a = filledTheory[query.head.indicator].toList()
                    assertEquals(result, a)
                }
        }
    }

    fun getIndicatorComplainsIfIndicatorNonWellFormed() {
        withFreshTheories {
            assertFailsWith<IllegalArgumentException> {
                filledTheory.get(Indicator.of(Var.anonymous(), Var.anonymous()))
            }
        }
    }

    fun assertAClause() {
        withFreshTheories {
            val correctPartiallyOrderedClauses = wellFormedClauses.toMutableList().apply { add(0, aRule) }
            val toBeTested = filledTheory.assertA(aRule)

            assertClauseHeadPartialOrderingRespected(correctPartiallyOrderedClauses, toBeTested.clauses)
        }
    }

    fun assertAClauseComplainsOnBadClause() {
        withFreshTheories {
            notWellFormedClauses.forEach {
                assertFailsWith<IllegalArgumentException> { filledTheory.assertA(it) }
            }
        }
    }

    fun assertAStruct() {
        withFreshTheories {
            val correctPartiallyOrderedClauses =
                wellFormedClauses
                    .toMutableList()
                    .apply { add(0, Fact.of(aRule.head)) }
            val toBeTested = filledTheory.assertA(aRule.head)

            assertClauseHeadPartialOrderingRespected(correctPartiallyOrderedClauses, toBeTested.clauses)
        }
    }

    private fun FreshTheoriesScope.assertTrueIfMutableFalseOtherwise(boolean: Boolean) {
        if (filledTheory is MutableTheory) {
            assertTrue(boolean)
        } else {
            assertFalse(boolean)
        }
    }

    private fun FreshTheoriesScope.assertFalseIfMutableTrueOtherwise(boolean: Boolean) {
        if (filledTheory is MutableTheory) {
            assertFalse(boolean)
        } else {
            assertTrue(boolean)
        }
    }

    fun assertACreatesNewUnlinkedInstance() {
        withFreshTheories {
            val toBeTested = filledTheory.assertA(anIndependentFact)

            assertTrueIfMutableFalseOtherwise(anIndependentFact in filledTheory)
            assertTrue(anIndependentFact in toBeTested)
        }
    }

    fun assertZClause() {
        withFreshTheories {
            val toBeTested = filledTheory.assertZ(aRule)

            assertClauseHeadPartialOrderingRespected(wellFormedClauses + aRule, toBeTested.clauses)
        }
    }

    fun assertZClauseComplainsOnBadClause() {
        withFreshTheories {
            notWellFormedClauses.forEach {
                assertFailsWith<IllegalArgumentException> { filledTheory.assertZ(it) }
            }
        }
    }

    fun assertZStruct() {
        withFreshTheories {
            val toBeTested = filledTheory.assertZ(aRule.head)

            assertClauseHeadPartialOrderingRespected(
                wellFormedClauses + Fact.of(aRule.head),
                toBeTested.clauses,
            )
        }
    }

    fun assertZCreatesNewUnlinkedInstance() {
        withFreshTheories {
            val toBeTested = filledTheory.assertZ(anIndependentFact)

            assertTrueIfMutableFalseOtherwise(anIndependentFact in filledTheory)
            assertTrue(anIndependentFact in toBeTested)
        }
    }

    fun retractClauseReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
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

    fun retractClauseRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryResultMap.forEach { (query, result) ->
            withFreshTheories {
                val retractResult = filledTheory.retract(query) as RetractResult.Success

                assertEquals(listOf(result.first()), retractResult.clauses.toList())
            }
        }
    }

    fun retractStructReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
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

    fun retractStructRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryWithBodyVarResultsMap.forEach { (query, result) ->
            withFreshTheories {
                val retractResult = filledTheory.retract(query.head) as RetractResult.Success

                assertEquals(listOf(result.first()), retractResult.clauses.toList())
            }
        }
    }

    fun retractCreatesNewUnlinkedInstanceIfSuccessful() {
        withFreshTheories {
            val aDatabaseClause = filledTheory.clauses.first()
            val toBeTested = filledTheory.retract(aDatabaseClause).theory

            assertSameIfMutableNotSameOtherwise(filledTheory, toBeTested)
            assertFalseIfMutableTrueOtherwise(aDatabaseClause in filledTheory)
            assertFalse(aDatabaseClause in toBeTested)
        }
    }

    fun retractReturnsSameTheoryOnFailure() {
        withFreshTheories {
            val toBeTested = filledTheory.retract(anIndependentFact).theory

            assertSame(filledTheory, toBeTested) // TODO take care of mutable theories here
        }
    }

    fun retractAllClauseReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
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

    fun retractAllClauseRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryResultMap.forEach { (query, result) ->
            withFreshTheories {
                val retractResult = filledTheory.retractAll(query) as RetractResult.Success

                assertEquals(result, retractResult.clauses.toList())
            }
        }
    }

    fun retractAllStructReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
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

    fun retractAllStructRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryWithBodyVarResultsMap.forEach { (query, result) ->
            withFreshTheories {
                val retractResult = filledTheory.retractAll(query.head) as RetractResult.Success

                assertEquals(result, retractResult.clauses.toList())
            }
        }
    }

    private fun assertSameIfMutableNotSameOtherwise(
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

    fun retractAllCreatesNewUnlinkedInstanceIfSuccessful() {
        withFreshTheories {
            val aDatabaseClause = filledTheory.clauses.first()
            val toBeTested = filledTheory.retractAll(aDatabaseClause).theory

            assertSameIfMutableNotSameOtherwise(filledTheory, toBeTested)
            assertFalseIfMutableTrueOtherwise(aDatabaseClause in filledTheory)
            assertFalse(aDatabaseClause in toBeTested)
        }
    }

    fun retractAllReturnsSameTheoryOnFailure() {
        withFreshTheories {
            val toBeTested = filledTheory.retractAll(anIndependentFact).theory

            assertSame(filledTheory, toBeTested)
        }
    }

    fun iteratorReturnsCorrectInstance() {
        withFreshTheories {
            assertClausesHaveSameLengthAndContent(
                filledTheory.clauses.iterator().asSequence(),
                filledTheory.iterator().asSequence(),
            )
        }
    }

    fun getTakesUnificationIntoAccount() {
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

    fun retractTakesUnificationIntoAccount() {
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

    fun nestedGetWorksAtSeveralDepthLevels() {
        val theory = theoryGenerator(deepClause)

        for (query in deepQueries) {
            assertClausesHaveSameLengthAndContent(
                deepClause.asSequence(),
                theory[query],
            )
        }
    }

    fun nestedRetractWorksAtSeveralDepthLevels() {
        for (query in deepQueries) {
            val theory = theoryGenerator(deepClause)
            assertClausesHaveSameLengthAndContent(
                deepClause,
                (theory.retract(query) as RetractResult.Success).clauses,
            )
        }
    }
}
