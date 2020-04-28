package it.unibo.tuprolog.theory

import kotlin.test.Test

class IndexedIssueRetractResult {

    private val prototype: PrototypeRetractResultTest = PrototypeRetractResultTest(
        ClauseDatabase.Companion::emptyIssue,
        ClauseDatabase.Companion::issueOf
    )

    @Test
    fun successClauseDatabaseCorrect() {
        prototype.successClauseDatabaseCorrect()
    }

    @Test
    fun successClausesListCorrect() {
        prototype.successClausesListCorrect()
    }

    @Test
    fun successFirstClauseCorrect() {
        prototype.successFirstClauseCorrect()
    }

    @Test
    fun successFirstClauseWithEmptyClauseListThrowsException() {
        prototype.successFirstClauseWithEmptyClauseListThrowsException()
    }

    @Test
    fun failClauseDatabaseCorrect() {
        prototype.failClauseDatabaseCorrect()
    }
}