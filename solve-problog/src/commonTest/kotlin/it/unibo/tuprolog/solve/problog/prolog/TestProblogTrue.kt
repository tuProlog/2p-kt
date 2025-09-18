package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestTrue
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogTrue :
    TestTrue,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestTrue.prototype(this)

    @Test
    override fun testTrue() {
        prototype.testTrue()
    }
}
