package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.theory.ClauseDatabase
import it.unibo.tuprolog.theory.PrototypeClauseDatabaseCreationTest
import kotlin.test.Test

internal class ListedClauseDatabaseCreationTest {

    private val prototype: PrototypeClauseDatabaseCreationTest =
        PrototypeClauseDatabaseCreationTest(
            ClauseDatabase.Companion::emptyListed,
            { ClauseDatabase.listedOf(*it) },
            ClauseDatabase.Companion::listedOf,
            ClauseDatabase.Companion::listedOf,
            ClauseDatabase.Companion::listedOf
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
