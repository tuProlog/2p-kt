package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestRetract
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class TestStreamsRetract :
    TestRetract,
    SolverFactory by StreamsSolverFactory {
    private val prototype = TestRetract.prototype(this)

    @Test
    override fun testRetractNumIfX() {
        prototype.testRetractNumIfX()
    }

    @Test
    override fun testRetractAtomEmptyList() {
        prototype.testRetractAtomEmptyList()
    }
}
