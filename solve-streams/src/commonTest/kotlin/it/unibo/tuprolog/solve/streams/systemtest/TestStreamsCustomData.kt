package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestCustomData
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class TestStreamsCustomData :
    TestCustomData,
    SolverFactory by StreamsSolverFactory {
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
