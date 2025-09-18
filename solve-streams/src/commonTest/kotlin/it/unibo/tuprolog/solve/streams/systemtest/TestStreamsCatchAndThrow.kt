package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCatchAndThrow
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Test

class TestStreamsCatchAndThrow :
    TestCatchAndThrow,
    SolverFactory by StreamsSolverFactory {
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
