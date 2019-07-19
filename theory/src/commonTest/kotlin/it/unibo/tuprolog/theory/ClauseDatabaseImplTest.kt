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

    private lateinit var wellFormedClauseDatabase: ClauseDatabase

    @BeforeTest
    fun init() {
        wellFormedClauseDatabase = ClauseDatabaseImpl(ClauseDatabaseUtils.wellFormedClauses)
    }

    @Test
    fun clausesCorrect() {
        assertEquals(ClauseDatabaseUtils.wellFormedClauses.count(), wellFormedClauseDatabase.clauses.count())
        assertContentsEquals(ClauseDatabaseUtils.wellFormedClauses, wellFormedClauseDatabase.clauses.toList())
    }

    @Test
    fun rulesCorrect() {
        val rules = ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Rule>()

        assertEquals(rules.count(), wellFormedClauseDatabase.rules.count())
        assertContentsEquals(rules, wellFormedClauseDatabase.rules.toList())
    }

    @Test
    fun directivesCorrect() {
        val directives = ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Directive>()

        assertEquals(directives.count(), wellFormedClauseDatabase.directives.count())
        assertContentsEquals(directives, wellFormedClauseDatabase.directives.toList())
    }

    @Test
    fun plusClauseDatabase() {
        val (firstHalfClauses, secondHalfClauses) = ClauseDatabaseUtils.wellFormedClausesHelves

        val toBeTested = ClauseDatabaseImpl(firstHalfClauses) + ClauseDatabaseImpl(secondHalfClauses)

        assertEquals(wellFormedClauseDatabase.clauses.count(), toBeTested.count())
        assertContentsEquals(wellFormedClauseDatabase.clauses.toList(), toBeTested.clauses.toList())
    }

    @Test
    fun plusClause() {
        val (firstHalfClauses, secondHalfClauses) = ClauseDatabaseUtils.wellFormedClausesHelves

        var toBeTested: ClauseDatabase = ClauseDatabaseImpl(firstHalfClauses)
        secondHalfClauses.forEach { toBeTested += it }

        assertEquals(wellFormedClauseDatabase.clauses.count(), toBeTested.count())
        assertContentsEquals(wellFormedClauseDatabase.clauses.toList(), toBeTested.clauses.toList())
    }

    @Test
    fun containsClause() {
        ClauseDatabaseUtils.wellFormedClauses.forEach {
            assertTrue { it in wellFormedClauseDatabase }
        }

        // TODO Issue #32 needs to be solved to get this working
//        ClauseDatabaseUtils.notWellFormedClauses.forEach {
//            assertFalse { it in wellFormedClauseDatabase }
//        }
    }

    @Test
    fun containsStruct() {
        ClauseDatabaseUtils.wellFormedClauses.filterIsInstance<Rule>().forEach {
            assertTrue { it.head in wellFormedClauseDatabase }
        }

        // TODO Issue #32 needs to be solved to get this working
//        ClauseDatabaseUtils.notWellFormedClauses.filterIsInstance<Rule>().forEach {
//            assertFalse { it.head in wellFormedClauseDatabase }
//        }
    }
}
