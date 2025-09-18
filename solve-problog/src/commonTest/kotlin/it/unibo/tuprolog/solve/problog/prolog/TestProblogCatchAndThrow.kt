package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCatchAndThrow
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogCatchAndThrow :
    TestCatchAndThrow,
    SolverFactory by ProblogSolverFactory {
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
