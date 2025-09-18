package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestSolutionPresentation
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Test

class TestStreamsSolutionPresentation :
    TestSolutionPresentation,
    SolverFactory by StreamsSolverFactory {
    val prototype = TestSolutionPresentation.prototype(this)

    @Test
    override fun testSolutionWithDandlingVars() {
        prototype.testSolutionWithDandlingVars()
    }
}
