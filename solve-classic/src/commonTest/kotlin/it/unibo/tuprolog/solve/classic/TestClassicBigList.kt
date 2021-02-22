package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestBigList
import kotlin.test.Test

class TestClassicBigList : TestBigList, SolverFactory by ClassicSolverFactory {
    private val prototype = TestBigList.prototype(this)

    @Test
    override fun testBigListGeneration() {
        prototype.testBigListGeneration()
    }
}
