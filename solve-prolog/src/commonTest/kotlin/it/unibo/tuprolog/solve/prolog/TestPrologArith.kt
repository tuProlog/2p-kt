package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestArith
import kotlin.test.Test

class TestPrologArith :
    TestArith,
    SolverFactory by PrologSolverFactory {
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
