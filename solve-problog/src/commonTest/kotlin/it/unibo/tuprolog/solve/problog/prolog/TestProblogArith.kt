package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestArith
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogArith :
    TestArith,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestArith.prototype(this)

    @Test
    override fun testArithDiff() {
        prototype.testArithDiff()
    }

    @Test
    override fun testArithEq() {
        prototype.testArithEq()
    }

    @Test
    override fun testArithGreaterThan() {
        prototype.testArithGreaterThan()
    }

    @Test
    override fun testArithGreaterThanEq() {
        prototype.testArithGreaterThanEq()
    }

    @Test
    override fun testArithLessThan() {
        prototype.testArithLessThan()
    }

    @Test
    override fun testArithLessThanEq() {
        prototype.testArithLessThanEq()
    }
}
