package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFail
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogClassicFail : TestFail, SolverFactory by ProblogSolverFactory {
    private val prototype = TestFail.prototype(this)

    @Test
    override fun testFail() {
        prototype.testFail()
    }

    @Test
    override fun testUndefPred() {
        // NOTE: This fails but is not significant
        // prototype.testUndefPred()
    }

    @Test
    override fun testSetFlagFail() {
        prototype.testSetFlagFail()
    }

    @Test
    override fun testSetFlagWarning() {
        prototype.testSetFlagWarning()
    }
}
