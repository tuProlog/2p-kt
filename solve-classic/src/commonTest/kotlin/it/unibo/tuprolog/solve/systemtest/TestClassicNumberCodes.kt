package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestNumberCodes
import kotlin.test.Test

class TestClassicNumberCodes : TestNumberCodes, SolverFactory by ClassicSolverFactory {
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
