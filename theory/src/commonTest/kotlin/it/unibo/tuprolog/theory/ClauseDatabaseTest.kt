package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.theory.testutils.ClauseDatabaseUtils
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Test class for [ClauseDatabase.Companion]
 *
 * @author Enrico
 */
internal class ClauseDatabaseTest {

    private val correctInstance = ClauseDatabaseImpl(ClauseDatabaseUtils.wellFormedClauses)

    @Test
    fun emptyCreatesEmptyClauseDatabase() {
        val toBeTested = ClauseDatabase.empty()

        assertEquals(ClauseDatabaseImpl(emptyList()), toBeTested)
    }

    @Test
    fun ofVarargClauseCreatesCorrectInstance() {
        val toBeTested = ClauseDatabase.of(*ClauseDatabaseUtils.wellFormedClauses.toTypedArray())

        assertEquals(correctInstance, toBeTested)
    }

    @Test
    fun ofVarargScopeToClauseCreatesCorrectInstance() {
        val toBeTested = ClauseDatabase.of(
            *ClauseDatabaseUtils.wellFormedClauses
                .map<Clause, Scope.() -> Clause> { { clauseOf(it.head, it.body) } }
                .toTypedArray()
        )

        assertEquals(correctInstance, toBeTested)
    }

    @Test
    fun ofIterableClauseCreatesCorrectInstance() {
        val toBeTested = ClauseDatabase.of(ClauseDatabaseUtils.wellFormedClauses)

        assertEquals(correctInstance, toBeTested)
    }

    @Test
    fun ofSequenceClauseCreatesCorrectInstance() {
        val toBeTested = ClauseDatabase.of(ClauseDatabaseUtils.wellFormedClauses.asSequence())

        assertEquals(correctInstance, toBeTested)
    }

}
