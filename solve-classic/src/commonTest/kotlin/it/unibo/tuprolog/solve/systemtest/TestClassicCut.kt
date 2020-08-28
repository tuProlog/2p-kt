package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCut
import kotlin.test.Test

class TestClassicCut: TestCut, SolverFactory by ClassicSolverFactory {

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