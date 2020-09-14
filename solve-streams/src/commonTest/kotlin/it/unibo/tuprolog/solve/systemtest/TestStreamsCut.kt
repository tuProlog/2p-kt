package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestCut
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsCut: TestCut, SolverFactory by StreamsSolverFactory {

    private val prototype = TestCut.prototype(this)

    @Test
    override fun testCut() {
        prototype.testCut()
    }

    @Test
    @Ignore
    override fun testCutFailTrue() {
        prototype.testCutFailTrue()
    }

    @Test
    override fun testCallCutFailTrue() {
        prototype.testCallCutFailTrue()
    }
}