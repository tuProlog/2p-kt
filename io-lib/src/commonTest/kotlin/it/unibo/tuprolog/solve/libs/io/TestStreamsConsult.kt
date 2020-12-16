package it.unibo.tuprolog.solve.libs.io

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Test

class TestStreamsConsult : TestConsult, SolverFactory by StreamsSolverFactory {
    private val prototype = TestConsult.prototype(this)

    @Test
    override fun testApi() {
        prototype.testApi()
    }

    @Test
    override fun testConsultWorksLocally() {
        prototype.testConsultWorksLocally()
    }

    @Test
    override fun testConsultWorksRemotely() {
        prototype.testConsultWorksRemotely()
    }
}
