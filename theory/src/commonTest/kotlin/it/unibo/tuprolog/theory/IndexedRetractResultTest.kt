package it.unibo.tuprolog.theory

import kotlin.test.Test

internal class IndexedRetractResultTest {

    private val prototype: PrototypeRetractResultTest = PrototypeRetractResultTest(
        ClauseDatabase.Companion::emptyIndexed,
        ClauseDatabase.Companion::indexedOf
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
