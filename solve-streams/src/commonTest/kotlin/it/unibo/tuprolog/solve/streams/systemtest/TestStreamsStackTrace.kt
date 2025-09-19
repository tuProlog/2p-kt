package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestStackTrace
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

@Ignore
class TestStreamsStackTrace :
    TestStackTrace,
    SolverFactory by StreamsSolverFactory {
    val prototype = TestStackTrace.prototype(this)

    @Test
    override fun testSimpleStackTrace() {
        prototype.testSimpleStackTrace()
    }

    @Test
    override fun testDoubleStackTrace() {
        prototype.testDoubleStackTrace()
    }

    @Test
    override fun testTripleStackTrace() {
        prototype.testTripleStackTrace()
    }

    @Test
    override fun testThrowIsNotInStacktrace() {
        prototype.testThrowIsNotInStacktrace()
    }
}
