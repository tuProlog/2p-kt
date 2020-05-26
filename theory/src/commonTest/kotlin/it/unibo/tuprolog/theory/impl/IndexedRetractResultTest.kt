package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.theory.PrototypeRetractResultTest
import it.unibo.tuprolog.theory.Theory
import kotlin.test.Test

internal class IndexedRetractResultTest {

    private val prototype: PrototypeRetractResultTest =
        PrototypeRetractResultTest(
            Theory.Companion::emptyIndexed,
            Theory.Companion::indexedOf
        )

    @Test
    fun successTheoryCorrect() {
        prototype.successTheoryCorrect()
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
    fun failTheoryCorrect() {
        prototype.failTheoryCorrect()
    }
}
