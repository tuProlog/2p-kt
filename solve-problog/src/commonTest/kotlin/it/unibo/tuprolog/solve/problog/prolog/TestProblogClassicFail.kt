package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFail
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestProblogClassicFail :
    TestFail,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestFail.prototype(this)

    @Test
    override fun testFail() {
        prototype.testFail()
    }

    // NOTE: Ignored because it relies on the internal representation of the knowledge base, not significant here
    @Ignore
    @Test
    override fun testUndefPred() {
        prototype.testUndefPred()
    }

    // NOTE: Ignored because of a mismatch between error codes
    @Ignore
    @Test
    override fun testSetFlagFail() {
        prototype.testSetFlagFail()
    }

    // NOTE: Ignored because of a mismatch between error codes
    @Ignore
    @Test
    override fun testSetFlagWarning() {
        prototype.testSetFlagWarning()
    }
}
