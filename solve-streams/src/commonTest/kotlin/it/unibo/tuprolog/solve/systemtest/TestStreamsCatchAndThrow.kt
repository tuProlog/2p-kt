package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestCatchAndThrow
import kotlin.test.Test

class TestStreamsCatchAndThrow : TestCatchAndThrow, SolverFactory by StreamsSolverFactory {
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
