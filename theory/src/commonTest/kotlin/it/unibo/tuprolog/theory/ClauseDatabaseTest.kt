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

    // TODO riprogettare questo test

    private val correctInstance = IndexedClauseDatabase(ClauseDatabaseUtils.wellFormedClauses)

    @Test
    fun emptyCreatesEmptyClauseDatabase() {
        val toBeTested = ClauseDatabase.empty()

        assertEquals(IndexedClauseDatabase(emptyList()), toBeTested)
    }

    @Test
    fun ofVarargClauseCreatesCorrectInstance() {
        val toBeTested = ClauseDatabase.indexedOf(*ClauseDatabaseUtils.wellFormedClauses.toTypedArray())

        assertEquals(correctInstance, toBeTested)
    }

    @Test
    fun ofVarargScopeToClauseCreatesCorrectInstance() {
        val toBeTested = ClauseDatabase.indexedOf(
            *ClauseDatabaseUtils.wellFormedClauses
                .map<Clause, Scope.() -> Clause> { { clauseOf(it.head, it.body) } }
                .toTypedArray()
        )

        assertEquals(correctInstance, toBeTested)
    }

    @Test
    fun ofIterableClauseCreatesCorrectInstance() {
        val toBeTested = ClauseDatabase.indexedOf(ClauseDatabaseUtils.wellFormedClauses)

        assertEquals(correctInstance, toBeTested)
    }

    @Test
    fun ofSequenceClauseCreatesCorrectInstance() {
        val toBeTested = ClauseDatabase.indexedOf(ClauseDatabaseUtils.wellFormedClauses.asSequence())

        assertEquals(correctInstance, toBeTested)
    }

}
