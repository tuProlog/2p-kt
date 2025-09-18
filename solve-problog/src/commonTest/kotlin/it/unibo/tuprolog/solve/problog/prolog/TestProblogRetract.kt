package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestRetract
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogRetract :
    TestRetract,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestRetract.prototype(this)

    @Test
    override fun testRetractNumIfX() {
        prototype.testRetractNumIfX()
    }

    @Test
    override fun testRetractAtomEmptyList() {
        prototype.testRetractAtomEmptyList()
    }
}
