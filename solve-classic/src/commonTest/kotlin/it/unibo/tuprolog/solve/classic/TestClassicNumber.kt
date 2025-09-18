package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestNumber
import kotlin.test.Test

class TestClassicNumber :
    TestNumber,
    SolverFactory by ClassicSolverFactory {
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
