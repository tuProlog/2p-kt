package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.*
import it.unibo.tuprolog.theory.testutils.ClauseDatabaseUtils
import it.unibo.tuprolog.theory.testutils.ReteTreeUtils.assertClauseHeadPartialOrderingRespected
import kotlin.test.*

/**
 * Test class for [ClauseDatabaseImpl] and [ClauseDatabase]
 *
 * @author Enrico
 */
internal class ClauseDatabaseImplTest {

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

    @BeforeTest
    fun init() {
        emptyClauseDatabase = ClauseDatabaseImpl(emptyList())
        filledClauseDatabase = ClauseDatabaseImpl(ClauseDatabaseUtils.wellFormedClauses)
    }

    @Test
    fun clauseDatabaseComplainsIFProvidingNotWellFormedClausesUponConstruction() {
        assertFailsWith<IllegalArgumentException> { ClauseDatabaseImpl(ClauseDatabaseUtils.notWellFormedClauses) }
    }

    @Test
    fun clausesCorrect() {
        assertTrue(emptyClauseDatabase.clauses.none())
        assertClauseHeadPartialOrderingRespected(ClauseDatabaseUtils.wellFormedClauses, filledClauseDatabase.clauses)
    }

    @Test
    fun rulesCorrect() {
        val rules = ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Rule>()

        assertClauseHeadPartialOrderingRespected(rules, filledClauseDatabase.rules)
        assertTrue(emptyClauseDatabase.rules.none())
    }

    @Test
    fun directivesCorrect() {
        val directives = ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Directive>()

        assertClauseHeadPartialOrderingRespected(directives, filledClauseDatabase.directives)
        assertTrue(emptyClauseDatabase.directives.none())
    }

    @Test
    fun plusClauseDatabasePreservesOrder() {
        val (firstHalfClauses, secondHalfClauses) = ClauseDatabaseUtils.wellFormedClausesHelves
        val toBeTested = ClauseDatabaseImpl(firstHalfClauses) + ClauseDatabaseImpl(secondHalfClauses)

        assertEquals(filledClauseDatabase.clauses, toBeTested.clauses)
    }

    @Test
    fun plusClauseDatabaseFailsOnBadDatabase() {
        val badClauseDatabase = object : ClauseDatabase {
            override val clauses: Iterable<Clause> = ClauseDatabaseUtils.notWellFormedClauses
            override fun plus(clauseDatabase: ClauseDatabase) = throw NotImplementedError()
            override fun contains(clause: Clause) = throw NotImplementedError()
            override fun contains(head: Struct) = throw NotImplementedError()
            override fun get(clause: Clause) = throw NotImplementedError()
            override fun get(head: Struct) = throw NotImplementedError()
            override fun assertA(clause: Clause) = throw NotImplementedError()
            override fun assertZ(clause: Clause) = throw NotImplementedError()
            override fun retract(clause: Clause) = throw NotImplementedError()
            override fun retractAll(clause: Clause) = throw NotImplementedError()
            override fun iterator() = throw NotImplementedError()
        }
        assertFailsWith<IllegalArgumentException> { filledClauseDatabase + badClauseDatabase }
    }

    @Test
    fun plusClause() {
        val (firstHalfClauses, secondHalfClauses) = ClauseDatabaseUtils.wellFormedClausesHelves
        var toBeTested: ClauseDatabase = ClauseDatabaseImpl(firstHalfClauses)
        secondHalfClauses.forEach { toBeTested += it }

        assertEquals(filledClauseDatabase.clauses, toBeTested.clauses)
    }

    @Test
    fun plusClauseRespectsPartialOrdering() {
        val toBeTested = filledClauseDatabase + aRule

        assertClauseHeadPartialOrderingRespected(ClauseDatabaseUtils.wellFormedClauses + aRule, toBeTested.clauses)
    }

    @Test
    fun plusClauseReturnsNewUnlinkedClauseDatabaseInstance() {
        val toBeTested = filledClauseDatabase + anIndependentFact

        assertFalse(anIndependentFact in filledClauseDatabase)
        assertTrue(anIndependentFact in toBeTested)
    }

    @Test
    fun plusClauseComplainsOnBadClause() {
        ClauseDatabaseUtils.notWellFormedClauses.forEach {
            assertFailsWith<IllegalArgumentException> { filledClauseDatabase + it }
        }
    }

    @Test
    fun containsClauseReturnsTrueWithPresentClauses() {
        ClauseDatabaseUtils.wellFormedClauses.forEach {
            assertTrue { it in filledClauseDatabase }
        }

        ClauseDatabaseUtils.clausesQueryResultsMap.forEach { (query, result) ->
            if (result.isNotEmpty()) assertTrue { query in filledClauseDatabase }
        }
    }

    @Test
    fun containsClauseReturnsFalseWithNonPresentClauses() {
        assertFalse(anIndependentFact in filledClauseDatabase)

        ClauseDatabaseUtils.clausesQueryResultsMap.forEach { (query, result) ->
            if (result.isEmpty()) assertFalse { query in filledClauseDatabase }
        }
    }

    @Test
    fun containsStructReturnsTrueIfMatchingHeadIsFound() {
        ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Rule>().forEach {
            assertTrue { it.head in filledClauseDatabase }
        }
    }

    @Test
    fun containsStructReturnsFalseIfNoMatchingHeadIsFound() {
        assertFalse(anIndependentFact in filledClauseDatabase)
    }

    @Test
    fun getClause() {
        ClauseDatabaseUtils.clausesQueryResultsMap.forEach { (query, result) ->
            assertEquals(result, filledClauseDatabase[query].toList())
        }
    }

    @Test
    fun getStruct() {
        ClauseDatabaseUtils.rulesQueryWithVarBodyResultsMap
                .forEach { (query, result) ->
                    val a = filledClauseDatabase[query.head].toList()
                    assertEquals(result, a)
                }
    }

    @Test
    fun assertAClause() {
        val correctPartiallyOrderedClauses = ClauseDatabaseUtils.wellFormedClauses.toMutableList()
                .apply { add(0, aRule) }
        val toBeTested = filledClauseDatabase.assertA(aRule)

        assertClauseHeadPartialOrderingRespected(correctPartiallyOrderedClauses, toBeTested.clauses)
    }

    @Test
    fun assertAClauseComplainsOnBadClause() {
        ClauseDatabaseUtils.notWellFormedClauses.forEach {
            assertFailsWith<IllegalArgumentException> { filledClauseDatabase.assertA(it) }
        }
    }

    @Test
    fun assertAStruct() {
        val correctPartiallyOrderedClauses = ClauseDatabaseUtils.wellFormedClauses.toMutableList()
                .apply { add(0, Fact.of(aRule.head)) }
        val toBeTested = filledClauseDatabase.assertA(aRule.head)

        assertClauseHeadPartialOrderingRespected(correctPartiallyOrderedClauses, toBeTested.clauses)
    }

    @Test
    fun assertACreatesNewUnlinkedInstance() {
        val toBeTested = filledClauseDatabase.assertA(anIndependentFact)

        assertFalse(anIndependentFact in filledClauseDatabase)
        assertTrue(anIndependentFact in toBeTested)
    }

    @Test
    fun assertZClause() {
        val toBeTested = filledClauseDatabase.assertZ(aRule)

        assertClauseHeadPartialOrderingRespected(ClauseDatabaseUtils.wellFormedClauses + aRule, toBeTested.clauses)
    }

    @Test
    fun assertZClauseComplainsOnBadClause() {
        ClauseDatabaseUtils.notWellFormedClauses.forEach {
            assertFailsWith<IllegalArgumentException> { filledClauseDatabase.assertZ(it) }
        }
    }

    @Test
    fun assertZStruct() {
        val toBeTested = filledClauseDatabase.assertZ(aRule.head)

        assertClauseHeadPartialOrderingRespected(ClauseDatabaseUtils.wellFormedClauses + Fact.of(aRule.head), toBeTested.clauses)
    }

    @Test
    fun assertZCreatesNewUnlinkedInstance() {
        val toBeTested = filledClauseDatabase.assertZ(anIndependentFact)

        assertFalse(anIndependentFact in filledClauseDatabase)
        assertTrue(anIndependentFact in toBeTested)
    }

    @Test
    fun retractClauseReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        ClauseDatabaseUtils.clausesQueryResultsMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retract(query)

            if (result.isEmpty()) assertTrue { retractResult is RetractResult.Failure }
            else assertTrue { retractResult is RetractResult.Success }
        }
    }

    @Test
    fun retractClauseRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryResultMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retract(query) as RetractResult.Success

            assertEquals(listOf(result.first()), retractResult.clauses.toList())
        }
    }

    @Test
    fun retractStructReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        ClauseDatabaseUtils.rulesQueryWithVarBodyResultsMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retract(query)

            if (result.isEmpty()) assertTrue { retractResult is RetractResult.Failure }
            else assertTrue { retractResult is RetractResult.Success }
        }
    }

    @Test
    fun retractStructRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryWithBodyVarResultsMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retract(query.head) as RetractResult.Success

            assertEquals(listOf(result.first()), retractResult.clauses.toList())
        }
    }

    @Test
    fun retractCreatesNewUnlinkedInstanceIfSuccessful() {
        val aDatabaseClause = filledClauseDatabase.clauses.first()
        val toBeTested = filledClauseDatabase.retract(aDatabaseClause).clauseDatabase

        assertNotSame(filledClauseDatabase, toBeTested)
        assertTrue(aDatabaseClause in filledClauseDatabase)
        assertFalse(aDatabaseClause in toBeTested)
    }

    @Test
    fun retractReturnsSameClauseDatabaseOnFailure() {
        val toBeTested = filledClauseDatabase.retract(anIndependentFact).clauseDatabase

        assertSame(filledClauseDatabase, toBeTested)
    }

    @Test
    fun retractAllClauseReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        ClauseDatabaseUtils.clausesQueryResultsMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retractAll(query)

            if (result.isEmpty()) assertTrue { retractResult is RetractResult.Failure }
            else assertTrue { retractResult is RetractResult.Success }
        }
    }

    @Test
    fun retractAllClauseRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryResultMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retractAll(query) as RetractResult.Success

            assertEquals(result, retractResult.clauses.toList())
        }
    }

    @Test
    fun retractAllStructReturnsFailureIfEmptyRetractedCollectionSuccessOtherwise() {
        ClauseDatabaseUtils.rulesQueryWithVarBodyResultsMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retractAll(query)

            if (result.isEmpty()) assertTrue { retractResult is RetractResult.Failure }
            else assertTrue { retractResult is RetractResult.Success }
        }
    }

    @Test
    fun retractAllStructRemovesOnlyFirstMatchingClause() {
        successfulRetractQueryWithBodyVarResultsMap.forEach { (query, result) ->
            val retractResult = filledClauseDatabase.retractAll(query.head) as RetractResult.Success

            assertEquals(result, retractResult.clauses.toList())
        }
    }

    @Test
    fun retractAllCreatesNewUnlinkedInstanceIfSuccessful() {
        val aDatabaseClause = filledClauseDatabase.clauses.first()
        val toBeTested = filledClauseDatabase.retractAll(aDatabaseClause).clauseDatabase

        assertNotSame(filledClauseDatabase, toBeTested)
        assertTrue(aDatabaseClause in filledClauseDatabase)
        assertFalse(aDatabaseClause in toBeTested)
    }

    @Test
    fun retractAllReturnsSameClauseDatabaseOnFailure() {
        val toBeTested = filledClauseDatabase.retractAll(anIndependentFact).clauseDatabase

        assertSame(filledClauseDatabase, toBeTested)
    }

    @Test
    fun iteratorReturnsCorrectInstance() {
        assertEquals(
                filledClauseDatabase.clauses.iterator().asSequence().toList(),
                filledClauseDatabase.iterator().asSequence().toList()
        )
    }

}
