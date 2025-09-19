package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCatchAndThrow
import kotlin.test.Test

class TestClassicCatchAndThrow :
    TestCatchAndThrow,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestCatchAndThrow.prototype(this)

    @Test
    override fun testCatchThrow() {
        prototype.testCatchThrow()
    }

    @Test
    override fun testCatchFail() {
        prototype.testCatchFail()
    }
}
