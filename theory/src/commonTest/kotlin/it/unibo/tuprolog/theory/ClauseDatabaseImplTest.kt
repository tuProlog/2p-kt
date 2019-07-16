package it.unibo.tuprolog.theory

import it.unibo.tuprolog.theory.testutils.ClauseDatabaseUtils
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

        assertTrue(ClauseDatabaseUtils.wellFormedClauses.containsAll(clauseDatabase.clauses.toList()))
        assertTrue(clauseDatabase.toList().containsAll(ClauseDatabaseUtils.wellFormedClauses))
    }

}
