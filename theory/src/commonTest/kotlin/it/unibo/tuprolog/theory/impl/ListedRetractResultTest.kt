package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.theory.ClauseDatabase
import it.unibo.tuprolog.theory.PrototypeRetractResultTest
import kotlin.test.Test

internal class ListedRetractResultTest {

    private val prototype: PrototypeRetractResultTest =
        PrototypeRetractResultTest(
            ClauseDatabase.Companion::emptyListed,
            ClauseDatabase.Companion::listedOf
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
