package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Directive
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.theory.testutils.ClauseDatabaseUtils
import it.unibo.tuprolog.theory.testutils.ClauseDatabaseUtils.assertContentsEquals
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Test class for [ClauseDatabaseImpl] and [ClauseDatabase]
 *
 * @author Enrico
 */
internal class ClauseDatabaseImplTest {

    private lateinit var clauseDatabase: ClauseDatabase

    @BeforeTest
    fun init() {
        clauseDatabase = ClauseDatabaseImpl(ClauseDatabaseUtils.wellFormedClauses)
    }

    @Test
    fun clausesCorrect() {
        assertEquals(ClauseDatabaseUtils.wellFormedClauses.count(), clauseDatabase.clauses.count())
        assertContentsEquals(ClauseDatabaseUtils.wellFormedClauses, clauseDatabase.clauses.toList())
    }

    @Test
    fun rulesCorrect() {
        val rules = ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Rule>()

        assertEquals(rules.count(), clauseDatabase.rules.count())
        assertContentsEquals(rules, clauseDatabase.rules.toList())
    }

    @Test
    fun directivesCorrect() {
        val directives = ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Directive>()

        assertEquals(directives.count(), clauseDatabase.directives.count())
        assertContentsEquals(directives, clauseDatabase.directives.toList())
    }

    @Test
    fun plusClauseDatabase() {
        val (firstHalfClauses, secondHalfClauses) = ClauseDatabaseUtils.wellFormedClausesHelves

        val toBeTested = ClauseDatabaseImpl(firstHalfClauses) + ClauseDatabaseImpl(secondHalfClauses)

        assertEquals(clauseDatabase.clauses.count(), toBeTested.count())
        assertContentsEquals(clauseDatabase.clauses.toList(), toBeTested.clauses.toList())
    }

    @Test
    fun plusClause() {
        val (firstHalfClauses, secondHalfClauses) = ClauseDatabaseUtils.wellFormedClausesHelves

        var toBeTested: ClauseDatabase = ClauseDatabaseImpl(firstHalfClauses)
        secondHalfClauses.forEach { toBeTested += it }

        assertEquals(clauseDatabase.clauses.count(), toBeTested.count())
        assertContentsEquals(clauseDatabase.clauses.toList(), toBeTested.clauses.toList())
    }

    @Test
    fun containsClause() {
        ClauseDatabaseUtils.wellFormedClauses.forEach {
            assertTrue { it in clauseDatabase }
        }

        // TODO Issue #32 needs to be solved to get this working
//        ClauseDatabaseUtils.notWellFormedClauses.forEach {
//            assertFalse { it in clauseDatabase }
//        }
    }

    @Test
    fun containsStruct() {

        // TODO Issue #34 need to be solved to get this working
//        ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Rule>().forEach {
//            assertTrue { it.head in clauseDatabase }
//        }

        // TODO Issue #32 needs to be solved to get this working
//        ClauseDatabaseUtils.notWellFormedClauses.filterIsInstance<Rule>().forEach {
//            assertFalse { it.head in clauseDatabase }
//        }
    }
}
