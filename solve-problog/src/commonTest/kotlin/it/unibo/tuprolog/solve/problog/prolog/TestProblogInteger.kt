package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestInteger
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogInteger :
    TestInteger,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestInteger.prototype(this)

    @Test
    override fun testIntPositiveNum() {
        prototype.testIntPositiveNum()
    }

    @Test
    override fun testIntNegativeNum() {
        prototype.testIntNegativeNum()
    }

    @Test
    override fun testIntDecNum() {
        prototype.testIntDecNum()
    }

    @Test
    override fun testIntX() {
        prototype.testIntX()
    }

    @Test
    override fun testIntAtom() {
        prototype.testIntAtom()
    }
}
