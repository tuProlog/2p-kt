package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestTerm
import kotlin.test.Test

class TestClassicTerm :
    TestTerm,
    SolverFactory by ClassicSolverFactory {
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
