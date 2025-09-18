package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestOr
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogOr :
    TestOr,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestOr.prototype(this)

    @Test
    override fun testTrueOrFalse() {
        prototype.testTrueOrFalse()
    }

    @Test
    override fun testCutFalseOrTrue() {
        prototype.testCutFalseOrTrue()
    }

    @Test
    override fun testCutCall() {
        prototype.testCutCall()
    }

    @Test
    override fun testCutAssignedValue() {
        prototype.testCutAssignedValue()
    }

    @Test
    override fun testOrDoubleAssignment() {
        prototype.testOrDoubleAssignment()
    }
}
