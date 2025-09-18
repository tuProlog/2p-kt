package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestRepeat
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsRepeat :
    TestRepeat,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestRepeat.prototype(this)

    @Test
    @Ignore
    override fun testRepeat() {
        prototype.testRepeat()
    }
}
