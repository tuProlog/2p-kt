package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestTerm
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Test

class TestStreamsTerm :
    TestTerm,
    SolverFactory by StreamsSolverFactory {
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
