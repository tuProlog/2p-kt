package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCustomData
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogCustomData :
    TestCustomData,
    SolverFactory by ProblogSolverFactory {
    val prototype = TestCustomData.prototype(this)

    @Test
    override fun testApi() {
        prototype.testApi()
    }

    @Test
    override fun testEphemeralData() {
        prototype.testEphemeralData()
    }

    @Test
    override fun testDurableData() {
        prototype.testDurableData()
    }

    @Test
    override fun testPersistentData() {
        prototype.testPersistentData()
    }
}
