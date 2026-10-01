package it.unibo.tuprolog.solve.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestTerm
import kotlin.test.Test

class TestPrologTerm :
    TestTerm,
    SolverFactory by PrologSolverFactory {
    private val prototype = TestTerm.prototype(this)

    @Test
    override fun testTermDiff() {
        prototype.testTermDiff()
    }

    @Test
    override fun testTermEq() {
        prototype.testTermEq()
    }

    @Test
    override fun testTermGreaterThan() {
        prototype.testTermGreaterThan()
    }

    @Test
    override fun testTermGreaterThanEq() {
        prototype.testTermGreaterThanEq()
    }

    @Test
    override fun testTermLessThan() {
        prototype.testTermLessThan()
    }

    @Test
    override fun testTermLessThanEq() {
        prototype.testTermLessThanEq()
    }
}
