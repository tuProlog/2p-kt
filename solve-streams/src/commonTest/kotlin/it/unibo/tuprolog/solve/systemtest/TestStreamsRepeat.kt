package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestRepeat
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsRepeat : TestRepeat, SolverFactory by StreamsSolverFactory {
    private val prototype = TestRepeat.prototype(this)

    @Test
    @Ignore
    override fun testRepeat() {
        prototype.testRepeat()
    }
}
