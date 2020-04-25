package it.unibo.tuprolog.theory

import kotlin.test.Test

internal class IndexedClauseDatabaseCreationTest {

    private val prototype: PrototypeClauseDatabaseCreationTest = PrototypeClauseDatabaseCreationTest(
        ClauseDatabase.Companion::emptyIndexed,
        { ClauseDatabase.indexedOf(*it) },
        ClauseDatabase.Companion::indexedOf,
        ClauseDatabase.Companion::indexedOf,
        ClauseDatabase.Companion::indexedOf
    )

    @Test
    fun emptyCreatesEmptyClauseDatabase() {
        prototype.emptyCreatesEmptyClauseDatabase()
    }

    @Test
    fun ofVarargClauseCreatesCorrectInstance() {
        prototype.ofVarargClauseCreatesCorrectInstance()
    }

    @Test
    fun ofVarargScopeToClauseCreatesCorrectInstance() {
        prototype.ofVarargScopeToClauseCreatesCorrectInstance()
    }

    @Test
    fun ofIterableClauseCreatesCorrectInstance() {
        prototype.ofIterableClauseCreatesCorrectInstance()
    }

    @Test
    fun ofSequenceClauseCreatesCorrectInstance() {
        prototype.ofSequenceClauseCreatesCorrectInstance()
    }
}
