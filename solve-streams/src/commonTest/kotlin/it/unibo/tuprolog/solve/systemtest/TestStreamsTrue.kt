package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestTrue
import kotlin.test.Test

class TestStreamsTrue : TestTrue, SolverFactory by StreamsSolverFactory {
    private val prototype = TestTrue.prototype(this)

    @Test
    override fun testTrue() {
        prototype.testTrue()
    }
}
