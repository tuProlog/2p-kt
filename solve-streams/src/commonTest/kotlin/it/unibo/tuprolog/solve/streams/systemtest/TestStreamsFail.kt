package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFail
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsFail :
    TestFail,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestFail.prototype(this)

    @Test
    override fun testFail() {
        prototype.testFail()
    }

    @Test
    @Ignore
    override fun testUndefPred() {
        prototype.testUndefPred()
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
