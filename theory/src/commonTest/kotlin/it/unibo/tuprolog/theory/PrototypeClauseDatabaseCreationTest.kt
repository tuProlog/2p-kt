package it.unibo.tuprolog.theory

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.theory.testutils.ClauseDatabaseUtils
import kotlin.test.assertEquals

/**
 * Test class for [ClauseDatabase.Companion]
 *
 * @author Enrico
 */
internal class PrototypeClauseDatabaseCreationTest(
    private val emptyClauseDbConstructor: () -> ClauseDatabase,
    private val clauseDbConstructorFromArray: (Array<Clause>) -> ClauseDatabase,
    private val clauseDbConstructorFromIterable: (Iterable<Clause>) -> ClauseDatabase,
    private val clauseDbConstructorFromSequence: (Sequence<Clause>) -> ClauseDatabase,
    private val clauseDbConstructorFromScopes: (Array<Scope.() -> Clause>) -> ClauseDatabase
) {

    private val correctInstance = clauseDbConstructorFromIterable(ClauseDatabaseUtils.wellFormedClauses)

    fun emptyCreatesEmptyClauseDatabase() {
        val toBeTested = emptyClauseDbConstructor()

        assertEquals(IndexedClauseDatabase(emptyList()), toBeTested)
    }

    fun ofVarargClauseCreatesCorrectInstance() {
        val toBeTested = clauseDbConstructorFromArray(ClauseDatabaseUtils.wellFormedClauses.toTypedArray())

        assertEquals(correctInstance, toBeTested)
    }

    fun ofVarargScopeToClauseCreatesCorrectInstance() {
        val toBeTested = clauseDbConstructorFromScopes(
            ClauseDatabaseUtils.wellFormedClauses
                .map<Clause, Scope.() -> Clause> { { clauseOf(it.head, it.body) } }
                .toTypedArray()
        )

        assertEquals(correctInstance, toBeTested)
    }

    fun ofIterableClauseCreatesCorrectInstance() {
        val toBeTested = clauseDbConstructorFromIterable(ClauseDatabaseUtils.wellFormedClauses)

        assertEquals(correctInstance, toBeTested)
    }

    fun ofSequenceClauseCreatesCorrectInstance() {
        val toBeTested = clauseDbConstructorFromSequence(ClauseDatabaseUtils.wellFormedClauses.asSequence())

        assertEquals(correctInstance, toBeTested)
    }

}
