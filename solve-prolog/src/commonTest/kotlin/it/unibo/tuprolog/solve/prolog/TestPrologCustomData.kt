package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCustomData
import kotlin.test.Test

class TestPrologCustomData :
    TestCustomData,
    SolverFactory by PrologSolverFactory {
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
