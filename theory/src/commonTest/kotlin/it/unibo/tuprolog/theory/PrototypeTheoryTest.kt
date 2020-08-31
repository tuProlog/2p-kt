package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.testutils.ClauseAssertionUtils.assertClausesHaveSameLengthAndContent
import it.unibo.tuprolog.testutils.ReteNodeUtils.assertClauseHeadPartialOrderingRespected
import it.unibo.tuprolog.testutils.TheoryUtils
import it.unibo.tuprolog.testutils.TheoryUtils.deepClause
import it.unibo.tuprolog.testutils.TheoryUtils.deepQueries
import it.unibo.tuprolog.testutils.TheoryUtils.memberClause
import it.unibo.tuprolog.testutils.TheoryUtils.negativeMemberQueries
import it.unibo.tuprolog.testutils.TheoryUtils.positiveMemberQueries
import kotlin.test.*

/**
 * Test class for all [Theory] subtypes
 *
 * @author Enrico
 */
class PrototypeTheoryTest(
    private val emptyTheoryGenerator: () -> Theory,
    private val theoryGenerator: (Iterable<Clause>) -> Theory
) {

    private lateinit var emptyTheory: Theory
    private lateinit var filledTheory: Theory

    private val anIndependentFact: Fact = Fact.of(Atom.of("myTestingFact"))
    private val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))

    private val successfulRetractQueryResultMap by lazy {
        TheoryUtils.clausesQueryResultsMap.filterValues { it.isNotEmpty() }
    }
    private val successfulRetractQueryWithBodyVarResultsMap by lazy {
        TheoryUtils.rulesQueryWithVarBodyResultsMap.filterValues { it.isNotEmpty() }
    }

    fun init() {
        emptyTheory = emptyTheoryGenerator()
        filledTheory = theoryGenerator(TheoryUtils.wellFormedClauses)
    }

    fun theoryComplainsIFProvidingNotWellFormedClausesUponConstruction() {
        assertFailsWith<IllegalArgumentException> { theoryGenerator(TheoryUtils.notWellFormedClauses) }
    }

    fun clausesCorrect() {
        assertTrue(emptyTheory.clauses.none())
        assertClauseHeadPartialOrderingRespected(TheoryUtils.wellFormedClauses, filledTheory.clauses)
    }

    fun rulesCorrect() {
        val rules = TheoryUtils.wellFormedClauses.filterIsInstance<Rule>()

        assertClauseHeadPartialOrderingRespected(rules, filledTheory.rules)
        assertTrue(emptyTheory.rules.none())
    }

    fun directivesCorrect() {
        val directives = TheoryUtils.wellFormedClauses.filterIsInstance<Directive>()

        assertClauseHeadPartialOrderingRespected(directives, filledTheory.directives)
        assertTrue(emptyTheory.directives.none())
    }

    fun plusTheoryPreservesOrder() {
        val (firstHalfClauses, secondHalfClauses) = TheoryUtils.wellFormedClausesHelves
        val toBeTested = theoryGenerator(firstHalfClauses) + theoryGenerator(secondHalfClauses)

        assertEquals(filledTheory.clauses, toBeTested.clauses)
    }

    fun plusTheoryFailsOnBadTheory() {
        val badTheory = object : Theory by emptyTheoryGenerator() {
            override val clauses: Iterable<Clause> = TheoryUtils.notWellFormedClauses
        }
        assertFailsWith<IllegalArgumentException> { filledTheory + badTheory }
    }

    fun plusClause() {
        val (firstHalfClauses, secondHalfClauses) = TheoryUtils.wellFormedClausesHelves
        var toBeTested: Theory = theoryGenerator(firstHalfClauses)
        secondHalfClauses.forEach { toBeTested += it }

        assertEquals(filledTheory.clauses, toBeTested.clauses)
    }

    fun plusClauseRespectsPartialOrdering() {
        val toBeTested = filledTheory + aRule

        assertClauseHeadPartialOrderingRespected(TheoryUtils.wellFormedClauses + aRule, toBeTested.clauses)
    }

    fun plusClauseReturnsNewUnlinkedTheoryInstance() {
        val toBeTested = filledTheory + anIndependentFact

        assertFalse(anIndependentFact in filledTheory)
        assertTrue(anIndependentFact in toBeTested)
    }

    fun plusClauseComplainsOnBadClause() {
        TheoryUtils.notWellFormedClauses.forEach {
            assertFailsWith<IllegalArgumentException> { filledTheory + it }
        }
    }

    fun containsClauseReturnsTrueWithPresentClauses() {
        TheoryUtils.wellFormedClauses.forEach {
            assertTrue { it in filledTheory }
        }

        TheoryUtils.clausesQueryResultsMap.forEach { (query, result) ->
            if (result.isNotEmpty()) assertTrue { query in filledTheory }
        }
    }

    fun containsClauseReturnsFalseWithNonPresentClauses() {
        assertFalse(anIndependentFact in filledTheory)

        TheoryUtils.clausesQueryResultsMap.forEach { (query, result) ->
            if (result.isEmpty()) assertFalse { query in filledTheory }
        }
    }

    fun containsStructReturnsTrueIfMatchingHeadIsFound() {
        TheoryUtils.wellFormedClauses.filterIsInstance<Rule>().forEach {
            assertTrue { it.head in filledTheory }
        }
    }

    fun containsStructReturnsFalseIfNoMatchingHeadIsFound() {
        assertFalse(anIndependentFact.head in filledTheory)
    }

    fun containsIndicatorReturnsTrueIfMatchingClauseIsFound() {
        TheoryUtils.wellFormedClauses.filterIsInstance<Rule>().forEach {
            assertTrue { it.head.indicator in filledTheory }
        }

        TheoryUtils.clausesQueryResultsMap.filterKeys { it is Rule }.mapKeys { it.key as Rule }
            .forEach { (query, result) ->
                if (result.isNotEmpty()) assertTrue { query.head.indicator in filledTheory }
            }
    }

    fun containsIndicatorReturnsFalseIfNoMatchingClauseIsFound() {
        assertFalse(filledTheory.contains(anIndependentFact.head.indicator))

        TheoryUtils.clausesQueryResultsMap.filterKeys { it is Rule }.mapKeys { it.key as Rule }
            .forEach { (query, result) ->
                if (result.isEmpty()) assertFalse { query.head.indicator in filledTheory }
            }
    }

    fun containsIndicatorComplainsIfIndicatorNonWellFormed() {
        assertFailsWith<IllegalArgumentException> {
            Indicator.of(Var.anonymous(), Var.anonymous()) in filledTheory
        }
    }

    fun getClause() {
        TheoryUtils.clausesQueryResultsMap.forEach { (query, result) ->
            assertEquals(result, filledTheory[query].toList())
        }
    }

    fun getStruct() {
        TheoryUtils.rulesQueryWithVarBodyResultsMap
            .forEach { (query, result) ->
                val a = filledTheory[query.head].toList()
                assertEquals(result, a)
            }
    }

    fun getIndicator() {
        TheoryUtils.rulesQueryResultByFunctorAndArity
            .forEach { (query, result) ->
                val a = filledTheory[query.head.indicator].toList()
                assertEquals(result, a)
            }
    }

    fun getIndicatorComplainsIfIndicatorNonWellFormed() {
        assertFailsWith<IllegalArgumentException> {
            filledTheory.get(Indicator.of(Var.anonymous(), Var.anonymous()))
        }
    }

    fun assertAClause() {
        val correctPartiallyOrderedClauses = TheoryUtils.wellFormedClauses.toMutableList()
            .apply { add(0, aRule) }
        val toBeTested = filledTheory.assertA(aRule)

        assertClauseHeadPartialOrderingRespected(correctPartiallyOrderedClauses, toBeTested.clauses)
    }

    fun assertAClauseComplainsOnBadClause() {
        TheoryUtils.notWellFormedClauses.forEach {
            assertFailsWith<IllegalArgumentException> { filledTheory.assertA(it) }
        }
    }

    fun assertAStruct() {
        val correctPartiallyOrderedClauses = TheoryUtils.wellFormedClauses.toMutableList()
            .apply { add(0, Fact.of(aRule.head)) }
        val toBeTested = filledTheory.assertA(aRule.head)

        assertClauseHeadPartialOrderingRespected(correctPartiallyOrderedClauses, toBeTested.clauses)
    }

    fun assertACreatesNewUnlinkedInstance() {
        val toBeTested = filledTheory.assertA(anIndependentFact)

        assertFalse(anIndependentFact in filledTheory)
        assertTrue(anIndependentFact in toBeTested)
    }

    fun assertZClause() {
        val toBeTested = filledTheory.assertZ(aRule)

        assertClauseHeadPartialOrderingRespected(TheoryUtils.wellFormedClauses + aRule, toBeTested.clauses)
    }

    fun assertZClauseComplainsOnBadClause() {
        TheoryUtils.notWellFormedClauses.forEach {
            assertFailsWith<IllegalArgumentException> { filledTheory.assertZ(it) }
        }
    }

    fun assertZStruct() {
        val toBeTested = filledTheory.assertZ(aRule.head)

        assertClauseHeadPartialOrderingRespected(
            TheoryUtils.wellFormedClauses + Fact.of(aRule.head),
            toBeTested.clauses
        )
    }

    fun assertZCreatesNewUnlinkedInstance() {
        val toBeTested = filledTheory.assertZ(anIndependentFact)

        assertFalse(anIndependentFact in filledTheory)
        assertTrue(anIndependentFact in toBeTested)
    }

    fun retractClauseReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        TheoryUtils.clausesQueryResultsMap.forEach { (query, result) ->
            val retractResult = filledTheory.retract(query)

            if (result.isEmpty()) assertTrue { retractResult is RetractResult.Failure }
            else assertTrue { retractResult is RetractResult.Success }
        }
    }

    fun retractClauseRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryResultMap.forEach { (query, result) ->
            val retractResult = filledTheory.retract(query) as RetractResult.Success

            assertEquals(listOf(result.first()), retractResult.clauses.toList())
        }
    }

    fun retractStructReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        TheoryUtils.rulesQueryWithVarBodyResultsMap.forEach { (query, result) ->
            val retractResult = filledTheory.retract(query)

            if (result.isEmpty()) assertTrue { retractResult is RetractResult.Failure }
            else assertTrue { retractResult is RetractResult.Success }
        }
    }

    fun retractStructRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryWithBodyVarResultsMap.forEach { (query, result) ->
            val retractResult = filledTheory.retract(query.head) as RetractResult.Success

            assertEquals(listOf(result.first()), retractResult.clauses.toList())
        }
    }

    fun retractCreatesNewUnlinkedInstanceIfSuccessful() {
        val aDatabaseClause = filledTheory.clauses.first()
        val toBeTested = filledTheory.retract(aDatabaseClause).theory

        assertNotSame(filledTheory, toBeTested)
        assertTrue(aDatabaseClause in filledTheory)
        assertFalse(aDatabaseClause in toBeTested)
    }

    fun retractReturnsSameTheoryOnFailure() {
        val toBeTested = filledTheory.retract(anIndependentFact).theory

        assertSame(filledTheory, toBeTested)
    }

    fun retractAllClauseReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        TheoryUtils.clausesQueryResultsMap.forEach { (query, result) ->
            val retractResult = filledTheory.retractAll(query)

            if (result.isEmpty()) assertTrue { retractResult is RetractResult.Failure }
            else assertTrue { retractResult is RetractResult.Success }
        }
    }

    fun retractAllClauseRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryResultMap.forEach { (query, result) ->
            val retractResult = filledTheory.retractAll(query) as RetractResult.Success

            assertEquals(result, retractResult.clauses.toList())
        }
    }

    fun retractAllStructReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        TheoryUtils.rulesQueryWithVarBodyResultsMap.forEach { (query, result) ->
            val retractResult = filledTheory.retractAll(query)

            if (result.isEmpty()) assertTrue { retractResult is RetractResult.Failure }
            else assertTrue { retractResult is RetractResult.Success }
        }
    }

    fun retractAllStructRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryWithBodyVarResultsMap.forEach { (query, result) ->
            val retractResult = filledTheory.retractAll(query.head) as RetractResult.Success

            assertEquals(result, retractResult.clauses.toList())
        }
    }

    fun retractAllCreatesNewUnlinkedInstanceIfSuccessful() {
        val aDatabaseClause = filledTheory.clauses.first()
        val toBeTested = filledTheory.retractAll(aDatabaseClause).theory

        assertNotSame(filledTheory, toBeTested)
        assertTrue(aDatabaseClause in filledTheory)
        assertFalse(aDatabaseClause in toBeTested)
    }

    fun retractAllReturnsSameTheoryOnFailure() {
        val toBeTested = filledTheory.retractAll(anIndependentFact).theory

        assertSame(filledTheory, toBeTested)
    }

    fun iteratorReturnsCorrectInstance() {
        assertEquals(
            filledTheory.clauses.iterator().asSequence().toList(),
            filledTheory.iterator().asSequence().toList()
        )
    }

    fun getTakesUnificationIntoAccount() {
        val theory = theoryGenerator(memberClause)

        for (query in positiveMemberQueries) {
            assertClausesHaveSameLengthAndContent(
                memberClause.asSequence(),
                theory[query]
            )
            assertClausesHaveSameLengthAndContent(
                memberClause.asSequence(),
                theory[query.head]
            )
        }

        for (query in negativeMemberQueries) {
            assertClausesHaveSameLengthAndContent(
                emptySequence(),
                theory[query]
            )
            assertClausesHaveSameLengthAndContent(
                emptySequence(),
                theory[query.head]
            )
        }
    }

    fun retractTakesUnificationIntoAccount() {

        for (query in positiveMemberQueries) {
            var theory = theoryGenerator(memberClause)
            assertClausesHaveSameLengthAndContent(
                memberClause,
                (theory.retract(query) as RetractResult.Success).clauses
            )
            theory = theoryGenerator(memberClause)
            assertClausesHaveSameLengthAndContent(
                memberClause,
                (theory.retract(query.head) as RetractResult.Success).clauses
            )
        }

        for (query in negativeMemberQueries) {
            val theory = theoryGenerator(memberClause)
            assertEquals(
                RetractResult.Failure(theory),
                theory.retract(query)
            )
            assertEquals(
                RetractResult.Failure(theory),
                theory.retract(query.head)
            )
        }
    }

    fun nestedGetWorksAtSeveralDepthLevels() {
        val theory = theoryGenerator(deepClause)

        for (query in deepQueries) {
            assertClausesHaveSameLengthAndContent(
                deepClause.asSequence(),
                theory[query]
            )
        }
    }

    fun nestedRetractWorksAtSeveralDepthLevels() {
        for (query in deepQueries) {
            val theory = theoryGenerator(deepClause)
            assertClausesHaveSameLengthAndContent(
                deepClause,
                (theory.retract(query) as RetractResult.Success).clauses
            )
        }
    }
}
