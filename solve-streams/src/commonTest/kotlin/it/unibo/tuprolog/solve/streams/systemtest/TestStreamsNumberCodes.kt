package it.unibo.tuprolog.solve.streams.systemtest

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestNumberCodes
import it.unibo.tuprolog.solve.streams.StreamsSolverFactory
import kotlin.test.Ignore
import kotlin.test.Test

class TestStreamsNumberCodes :
    TestNumberCodes,
    SolverFactory by StreamsSolverFactory {
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
    @Ignore
    override fun testNumberCodesChar() {
        prototype.testNumberCodesChar()
    }
}
