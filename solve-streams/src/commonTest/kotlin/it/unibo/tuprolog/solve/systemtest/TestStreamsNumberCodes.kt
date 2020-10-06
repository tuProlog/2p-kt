package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.StreamsSolverFactory
import it.unibo.tuprolog.solve.TestNumberCodes
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsNumberCodes : TestNumberCodes, SolverFactory by StreamsSolverFactory {
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
    @Ignore
    override fun testNumberCodesListIsVar2() {
        prototype.testNumberCodesListIsVar2()
    }

    @Test
    @Ignore
    override fun testNumberCodesOk() {
        prototype.testNumberCodesOk()
    }

    @Test
    @Ignore
    override fun testNumberCodesCompleteTest() {
        prototype.testNumberCodesCompleteTest()
    }

    @Test
    @Ignore
    override fun testNumberCodesNegativeNumber() {
        prototype.testNumberCodesNegativeNumber()
    }

    @Test
    @Ignore
    override fun testNumberCodesChar() {
        prototype.testNumberCodesChar()
    }
}
