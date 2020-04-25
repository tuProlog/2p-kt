package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.testutils.ClauseDatabaseUtils
import it.unibo.tuprolog.theory.testutils.ReteNodeUtils.assertClauseHeadPartialOrderingRespected
import kotlin.test.*

/**
 * Test class for all [ClauseDatabase] subtypes
 *
 * @author Enrico
 */
class ClauseDatabaseTestPrototype(
    private val emptyClauseDbGenerator: () -> ClauseDatabase,
    private val clauseDbGenerator: (Iterable<Clause>) -> ClauseDatabase
) {

    private lateinit var emptyClauseDatabase: ClauseDatabase
    private lateinit var filledClauseDatabase: ClauseDatabase

    private val anIndependentFact: Fact = Fact.of(Atom.of("myTestingFact"))
    private val aRule: Rule = Rule.of(Atom.of("a"), Var.of("A"))

    private val successfulRetractQueryResultMap by lazy {
        ClauseDatabaseUtils.clausesQueryResultsMap.filterValues { it.isNotEmpty() }
    }
    private val successfulRetractQueryWithBodyVarResultsMap by lazy {
        ClauseDatabaseUtils.rulesQueryWithVarBodyResultsMap.filterValues { it.isNotEmpty() }
    }

    fun init() {
        emptyClauseDatabase = emptyClauseDbGenerator()
        filledClauseDatabase = clauseDbGenerator(ClauseDatabaseUtils.wellFormedClauses)
    }

    fun clauseDatabaseComplainsIFProvidingNotWellFormedClausesUponConstruction() {
        assertFailsWith<IllegalArgumentException> { clauseDbGenerator(ClauseDatabaseUtils.notWellFormedClauses) }
    }

    fun clausesCorrect() {
        assertTrue(emptyClauseDatabase.clauses.none())
        assertClauseHeadPartialOrderingRespected(ClauseDatabaseUtils.wellFormedClauses, filledClauseDatabase.clauses)
    }

    fun rulesCorrect() {
        val rules = ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Rule>()

        assertClauseHeadPartialOrderingRespected(rules, filledClauseDatabase.rules)
        assertTrue(emptyClauseDatabase.rules.none())
    }

    fun directivesCorrect() {
        val directives = ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Directive>()

        assertClauseHeadPartialOrderingRespected(directives, filledClauseDatabase.directives)
        assertTrue(emptyClauseDatabase.directives.none())
    }

    fun plusClauseDatabasePreservesOrder() {
        val (firstHalfClauses, secondHalfClauses) = ClauseDatabaseUtils.wellFormedClausesHelves
        val toBeTested = clauseDbGenerator(firstHalfClauses) + clauseDbGenerator(secondHalfClauses)

        assertEquals(filledClauseDatabase.clauses, toBeTested.clauses)
    }

    fun plusClauseDatabaseFailsOnBadDatabase() {
        val badClauseDatabase = object : ClauseDatabase by emptyClauseDbGenerator() {
            override val clauses: Iterable<Clause> = ClauseDatabaseUtils.notWellFormedClauses
        }
        assertFailsWith<IllegalArgumentException> { filledClauseDatabase + badClauseDatabase }
    }

    fun plusClause() {
        val (firstHalfClauses, secondHalfClauses) = ClauseDatabaseUtils.wellFormedClausesHelves
        var toBeTested: ClauseDatabase = clauseDbGenerator(firstHalfClauses)
        secondHalfClauses.forEach { toBeTested += it }

        assertEquals(filledClauseDatabase.clauses, toBeTested.clauses)
    }

    fun plusClauseRespectsPartialOrdering() {
        val toBeTested = filledClauseDatabase + aRule

        assertClauseHeadPartialOrderingRespected(ClauseDatabaseUtils.wellFormedClauses + aRule, toBeTested.clauses)
    }

    fun plusClauseReturnsNewUnlinkedClauseDatabaseInstance() {
        val toBeTested = filledClauseDatabase + anIndependentFact

        assertFalse(anIndependentFact in filledClauseDatabase)
        assertTrue(anIndependentFact in toBeTested)
    }

    fun plusClauseComplainsOnBadClause() {
        ClauseDatabaseUtils.notWellFormedClauses.forEach {
            assertFailsWith<IllegalArgumentException> { filledClauseDatabase + it }
        }
    }

    fun containsClauseReturnsTrueWithPresentClauses() {
        ClauseDatabaseUtils.wellFormedClauses.forEach {
            assertTrue { it in filledClauseDatabase }
        }

        ClauseDatabaseUtils.clausesQueryResultsMap.forEach { (query, result) ->
            if (result.isNotEmpty()) assertTrue { query in filledClauseDatabase }
        }
    }

    fun containsClauseReturnsFalseWithNonPresentClauses() {
        assertFalse(anIndependentFact in filledClauseDatabase)

        ClauseDatabaseUtils.clausesQueryResultsMap.forEach { (query, result) ->
            if (result.isEmpty()) assertFalse { query in filledClauseDatabase }
        }
    }

    fun containsStructReturnsTrueIfMatchingHeadIsFound() {
        ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Rule>().forEach {
            assertTrue { it.head in filledClauseDatabase }
        }
    }

    fun containsStructReturnsFalseIfNoMatchingHeadIsFound() {
        assertFalse(anIndependentFact.head in filledClauseDatabase)
    }

    fun containsIndicatorReturnsTrueIfMatchingClauseIsFound() {
        ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Rule>().forEach {
            assertTrue { it.head.indicator in filledClauseDatabase }
        }

        ClauseDatabaseUtils.clausesQueryResultsMap.filterKeys { it is Rule }.mapKeys { it.key as Rule }
            .forEach { (query, result) ->
                if (result.isNotEmpty()) assertTrue { query.head.indicator in filledClauseDatabase }
            }
    }

    fun containsIndicatorReturnsFalseIfNoMatchingClauseIsFound() {
        assertFalse(filledClauseDatabase.contains(anIndependentFact.head.indicator))

        ClauseDatabaseUtils.clausesQueryResultsMap.filterKeys { it is Rule }.mapKeys { it.key as Rule }
            .forEach { (query, result) ->
                if (result.isEmpty()) assertFalse { query.head.indicator in filledClauseDatabase }
            }
    }

    fun containsIndicatorComplainsIfIndicatorNonWellFormed() {
        assertFailsWith<IllegalArgumentException> {
            Indicator.of(Var.anonymous(), Var.anonymous()) in filledClauseDatabase
        }
    }

    fun getClause() {
        ClauseDatabaseUtils.clausesQueryResultsMap.forEach { (query, result) ->
            assertEquals(result, filledClauseDatabase[query].toList())
        }
    }

    fun getStruct() {
        ClauseDatabaseUtils.rulesQueryWithVarBodyResultsMap
            .forEach { (query, result) ->
                val a = filledClauseDatabase[query.head].toList()
                assertEquals(result, a)
            }
    }

    fun getIndicator() {
        ClauseDatabaseUtils.rulesQueryResultByFunctorAndArity
            .forEach { (query, result) ->
                val a = filledClauseDatabase[query.head.indicator].toList()
                assertEquals(result, a)
            }
    }

    fun getIndicatorComplainsIfIndicatorNonWellFormed() {
        assertFailsWith<IllegalArgumentException> {
            filledClauseDatabase.get(Indicator.of(Var.anonymous(), Var.anonymous()))
        }
    }

    fun assertAClause() {
        val correctPartiallyOrderedClauses = ClauseDatabaseUtils.wellFormedClauses.toMutableList()
            .apply { add(0, aRule) }
        val toBeTested = filledClauseDatabase.assertA(aRule)

        assertClauseHeadPartialOrderingRespected(correctPartiallyOrderedClauses, toBeTested.clauses)
    }

    fun assertAClauseComplainsOnBadClause() {
        ClauseDatabaseUtils.notWellFormedClauses.forEach {
            assertFailsWith<IllegalArgumentException> { filledClauseDatabase.assertA(it) }
        }
    }

    fun assertAStruct() {
        val correctPartiallyOrderedClauses = ClauseDatabaseUtils.wellFormedClauses.toMutableList()
            .apply { add(0, Fact.of(aRule.head)) }
        val toBeTested = filledClauseDatabase.assertA(aRule.head)

        assertClauseHeadPartialOrderingRespected(correctPartiallyOrderedClauses, toBeTested.clauses)
    }

    fun assertACreatesNewUnlinkedInstance() {
        val toBeTested = filledClauseDatabase.assertA(anIndependentFact)

        assertFalse(anIndependentFact in filledClauseDatabase)
        assertTrue(anIndependentFact in toBeTested)
    }

    fun assertZClause() {
        val toBeTested = filledClauseDatabase.assertZ(aRule)

        assertClauseHeadPartialOrderingRespected(ClauseDatabaseUtils.wellFormedClauses + aRule, toBeTested.clauses)
    }

    fun assertZClauseComplainsOnBadClause() {
        ClauseDatabaseUtils.notWellFormedClauses.forEach {
            assertFailsWith<IllegalArgumentException> { filledClauseDatabase.assertZ(it) }
        }
    }

    fun assertZStruct() {
        val toBeTested = filledClauseDatabase.assertZ(aRule.head)

        assertClauseHeadPartialOrderingRespected(
            ClauseDatabaseUtils.wellFormedClauses + Fact.of(aRule.head),
            toBeTested.clauses
        )
    }

    fun assertZCreatesNewUnlinkedInstance() {
        val toBeTested = filledClauseDatabase.assertZ(anIndependentFact)

        assertFalse(anIndependentFact in filledClauseDatabase)
        assertTrue(anIndependentFact in toBeTested)
    }

    fun retractClauseReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        ClauseDatabaseUtils.clausesQueryResultsMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retract(query)

            if (result.isEmpty()) assertTrue { retractResult is RetractResult.Failure }
            else assertTrue { retractResult is RetractResult.Success }
        }
    }

    fun retractClauseRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryResultMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retract(query) as RetractResult.Success

            assertEquals(listOf(result.first()), retractResult.clauses.toList())
        }
    }

    fun retractStructReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        ClauseDatabaseUtils.rulesQueryWithVarBodyResultsMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retract(query)

            if (result.isEmpty()) assertTrue { retractResult is RetractResult.Failure }
            else assertTrue { retractResult is RetractResult.Success }
        }
    }

    fun retractStructRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryWithBodyVarResultsMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retract(query.head) as RetractResult.Success

            assertEquals(listOf(result.first()), retractResult.clauses.toList())
        }
    }

    fun retractCreatesNewUnlinkedInstanceIfSuccessful() {
        val aDatabaseClause = filledClauseDatabase.clauses.first()
        val toBeTested = filledClauseDatabase.retract(aDatabaseClause).clauseDatabase

        assertNotSame(filledClauseDatabase, toBeTested)
        assertTrue(aDatabaseClause in filledClauseDatabase)
        assertFalse(aDatabaseClause in toBeTested)
    }

    fun retractReturnsSameClauseDatabaseOnFailure() {
        val toBeTested = filledClauseDatabase.retract(anIndependentFact).clauseDatabase

        assertSame(filledClauseDatabase, toBeTested)
    }

    fun retractAllClauseReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        ClauseDatabaseUtils.clausesQueryResultsMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retractAll(query)

            if (result.isEmpty()) assertTrue { retractResult is RetractResult.Failure }
            else assertTrue { retractResult is RetractResult.Success }
        }
    }

    fun retractAllClauseRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryResultMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retractAll(query) as RetractResult.Success

            assertEquals(result, retractResult.clauses.toList())
        }
    }

    fun retractAllStructReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        ClauseDatabaseUtils.rulesQueryWithVarBodyResultsMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retractAll(query)

            if (result.isEmpty()) assertTrue { retractResult is RetractResult.Failure }
            else assertTrue { retractResult is RetractResult.Success }
        }
    }

    fun retractAllStructRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryWithBodyVarResultsMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retractAll(query.head) as RetractResult.Success

            assertEquals(result, retractResult.clauses.toList())
        }
    }

    fun retractAllCreatesNewUnlinkedInstanceIfSuccessful() {
        val aDatabaseClause = filledClauseDatabase.clauses.first()
        val toBeTested = filledClauseDatabase.retractAll(aDatabaseClause).clauseDatabase

        assertNotSame(filledClauseDatabase, toBeTested)
        assertTrue(aDatabaseClause in filledClauseDatabase)
        assertFalse(aDatabaseClause in toBeTested)
    }

    fun retractAllReturnsSameClauseDatabaseOnFailure() {
        val toBeTested = filledClauseDatabase.retractAll(anIndependentFact).clauseDatabase

        assertSame(filledClauseDatabase, toBeTested)
    }

    fun iteratorReturnsCorrectInstance() {
        assertEquals(
            filledClauseDatabase.clauses.iterator().asSequence().toList(),
            filledClauseDatabase.iterator().asSequence().toList()
        )
    }

}
