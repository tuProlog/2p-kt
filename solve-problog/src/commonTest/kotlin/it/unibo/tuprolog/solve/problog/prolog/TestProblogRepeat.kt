package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestRepeat
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogRepeat :
    TestRepeat,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestRepeat.prototype(this)

    @Test
    override fun testRepeat() {
        prototype.testRepeat()
    }
}
