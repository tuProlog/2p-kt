package it.unibo.tuprolog.solve.classic

import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestNumberChars
import kotlin.test.Test

class TestClassicNumberChars :
    TestNumberChars,
    SolverFactory by ClassicSolverFactory {
    private val prototype = TestNumberChars.prototype(this)

    @Test
    override fun testNumberCharsListIsVar() {
        prototype.testNumberCharsListIsVar()
    }

    @Test
    override fun testNumberCharsOK() {
        prototype.testNumberCharsOK()
    }

    @Test
    override fun testNumberCharsNumIsVar() {
        prototype.testNumberCharsNumIsVar()
    }

    @Test
    override fun testNumberCharsNumNegativeIsVar() {
        prototype.testNumberCharsNumNegativeIsVar()
    }

    @Test
    override fun testNumberCharsSpace() {
        prototype.testNumberCharsSpace()
    }

    override fun testNumberCharsDecimalNumber() {
        prototype.testNumberCharsDecimalNumber()
    }

    @Test
    override fun testNumberCharsCompleteCase() {
        prototype.testNumberCharsCompleteCase()
    }

    @Test
    override fun testNumberCharsInstationErrror() {
        prototype.testNumberCharsInstationErrror()
    }
}
