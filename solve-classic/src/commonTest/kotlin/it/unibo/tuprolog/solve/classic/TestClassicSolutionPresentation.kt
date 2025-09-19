package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSolutionPresentation
import kotlin.test.Test

class TestClassicSolutionPresentation :
    TestSolutionPresentation,
    SolverFactory by ClassicSolverFactory {
    val prototype = TestSolutionPresentation.prototype(this)

    @Test
    override fun testSolutionWithDandlingVars() {
        prototype.testSolutionWithDandlingVars()
    }
}
