package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestBigList
import kotlin.test.Test

class TestPrologBigList :
    TestBigList,
    SolverFactory by PrologSolverFactory {
    private val prototype = TestBigList.prototype(this)

    @Test
    override fun testBigListGeneration() {
        prototype.testBigListGeneration()
    }
}
