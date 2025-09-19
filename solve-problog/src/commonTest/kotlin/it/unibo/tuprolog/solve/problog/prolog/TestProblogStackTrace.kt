package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestStackTrace
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

// NOTE: Ignored because the heavier stack usage of this implementation makes this not meaningful.
@Ignore
class TestProblogStackTrace :
    TestStackTrace,
    SolverFactory by ProblogSolverFactory {
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
