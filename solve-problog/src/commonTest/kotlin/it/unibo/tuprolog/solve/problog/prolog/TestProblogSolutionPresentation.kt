package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSolutionPresentation
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogSolutionPresentation :
    TestSolutionPresentation,
    SolverFactory by ProblogSolverFactory {
    val prototype = TestSolutionPresentation.prototype(this)

    @Test
    override fun testSolutionWithDandlingVars() {
        prototype.testSolutionWithDandlingVars()
    }
}
