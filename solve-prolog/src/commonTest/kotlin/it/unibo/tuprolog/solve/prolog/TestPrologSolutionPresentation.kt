package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSolutionPresentation
import kotlin.test.Test

class TestPrologSolutionPresentation :
    TestSolutionPresentation,
    SolverFactory by PrologSolverFactory {
    val prototype = TestSolutionPresentation.prototype(this)

    @Test
    override fun testSolutionWithDandlingVars() {
        prototype.testSolutionWithDandlingVars()
    }
}
