package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestRecursion
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

// NOTE: Ignored because the heavier stack usage of this implementation makes this not meaningful.
@Ignore
class TestProblogRecursion :
    TestRecursion,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestRecursion.prototype(this)

    @Test
    override fun testRecursion1() {
        prototype.testRecursion1()
    }

    @Test
    override fun testRecursion2() {
        prototype.testRecursion2()
    }

    @Test
    override fun testTailRecursion() {
        prototype.testTailRecursion()
    }

    @Test
    override fun testNonTailRecursion() {
        prototype.testNonTailRecursion()
    }
}
