package it.unibo.tuprolog.solve.problog.prolog

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestNumberCodes
import it.unibo.tuprolog.solve.problog.ProblogSolverFactory
import kotlin.test.Test

class TestProblogNumberCodes :
    TestNumberCodes,
    SolverFactory by ProblogSolverFactory {
    private val prototype = TestNumberCodes.prototype(this)

    @Test
    override fun testNumberCodesListIsVar() {
        prototype.testNumberCodesListIsVar()
    }

    @Test
    override fun testNumberCodesNumIsDecimal() {
        prototype.testNumberCodesNumIsDecimal()
    }

    @Test
    override fun testNumberCodesListIsVar2() {
        prototype.testNumberCodesListIsVar2()
    }

    @Test
    override fun testNumberCodesOk() {
        prototype.testNumberCodesOk()
    }

    @Test
    override fun testNumberCodesCompleteTest() {
        prototype.testNumberCodesCompleteTest()
    }

    @Test
    override fun testNumberCodesNegativeNumber() {
        prototype.testNumberCodesNegativeNumber()
    }

    @Test
    override fun testNumberCodesChar() {
        prototype.testNumberCodesChar()
    }
}
