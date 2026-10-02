package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCatchAndThrow
import kotlin.test.Test

class TestPrologCatchAndThrow :
    TestCatchAndThrow,
    SolverFactory by PrologSolverFactory {
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
