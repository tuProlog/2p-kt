package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestStackTrace
import kotlin.test.Test

class TestClassicStackTrace :
    TestStackTrace,
    SolverFactory by ClassicSolverFactory {
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
