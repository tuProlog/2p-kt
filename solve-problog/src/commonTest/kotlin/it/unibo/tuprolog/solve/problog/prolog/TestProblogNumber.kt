package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestNumber
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogNumber :
    TestNumber,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestNumber.prototype(this)

    @Test
    override fun testBasicNum() {
        prototype.testBasicNum()
    }

    @Test
    override fun testDecNum() {
        prototype.testDecNum()
    }

    @Test
    override fun testNegNum() {
        prototype.testNegNum()
    }

    @Test
    override fun testLetterNum() {
        prototype.testLetterNum()
    }

    @Test
    override fun testXNum() {
        prototype.testXNum()
    }
}
