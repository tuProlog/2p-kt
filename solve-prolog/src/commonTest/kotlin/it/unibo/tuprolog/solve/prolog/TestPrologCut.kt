package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCut
import kotlin.test.Test

class TestPrologCut :
    TestCut,
    SolverFactory by PrologSolverFactory {
    private val prototype = TestCut.prototype(this)

    @Test
    override fun testCut() {
        prototype.testCut()
    }

    @Test
    override fun testCutFailTrue() {
        prototype.testCutFailTrue()
    }

    @Test
    override fun testCallCutFailTrue() {
        prototype.testCallCutFailTrue()
    }
}
