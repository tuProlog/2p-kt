package it.unibo.tuprolog.theory

import kotlin.test.Test

class IndexedIssueClauseDatabaseCreationTest {

    private val prototype: PrototypeClauseDatabaseCreationTest = PrototypeClauseDatabaseCreationTest(
        ClauseDatabase.Companion::emptyIssue,
        { ClauseDatabase.issueOf(*it) },
        ClauseDatabase.Companion::issueOf,
        ClauseDatabase.Companion::issueOf,
        ClauseDatabase.Companion::issueOf
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